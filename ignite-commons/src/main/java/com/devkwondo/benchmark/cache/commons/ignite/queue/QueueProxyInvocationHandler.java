package com.devkwondo.benchmark.cache.commons.ignite.queue;

import lombok.AllArgsConstructor;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.internal.processors.cache.GridCacheAdapter;
import org.apache.ignite.internal.processors.cache.GridCacheGateway;
import org.apache.ignite.internal.processors.datastructures.*;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.lang.IgniteUuid;

import javax.cache.processor.EntryProcessor;
import javax.cache.processor.MutableEntry;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

import static org.apache.ignite.IgniteSystemProperties.IGNITE_ATOMIC_CACHE_QUEUE_RETRY_TIMEOUT;


@AllArgsConstructor
public class QueueProxyInvocationHandler<T> implements InvocationHandler {

    private static final long RETRY_TIMEOUT = Integer.getInteger(IGNITE_ATOMIC_CACHE_QUEUE_RETRY_TIMEOUT, 10000);

    private final GridCacheQueueProxy<T> gridCacheQueueProxy;
    private final GridCacheQueueAdapter<T> gridCacheQueueAdapter;
    private final GridCacheGateway gate;
    private final GridCacheAdapter cache;
    private final GridCacheQueueHeaderKey queueKey;
    private final Method itemKeyMethod;

    public QueueProxyInvocationHandler(GridCacheQueueProxy<T> gridCacheQueueProxy) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {
        this.gridCacheQueueProxy = gridCacheQueueProxy;
        this.gridCacheQueueAdapter = gridCacheQueueProxy.delegate();

        Field gateField = GridCacheQueueProxy.class.getDeclaredField("gate");
        gateField.setAccessible(true);
        this.gate = (GridCacheGateway) gateField.get(gridCacheQueueProxy);

        Field cacheField = GridCacheQueueAdapter.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        this.cache = (GridCacheAdapter) cacheField.get(gridCacheQueueAdapter);

        Field queueKeyField = GridCacheQueueAdapter.class.getDeclaredField("queueKey");
        queueKeyField.setAccessible(true);
        this.queueKey = (GridCacheQueueHeaderKey) queueKeyField.get(gridCacheQueueAdapter);

        Method method = GridCacheQueueAdapter.class.getDeclaredMethod("itemKey", Long.class);
        method.setAccessible(true);
        this.itemKeyMethod = method;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("drainTo".equalsIgnoreCase(method.getName()) && method.getParameterCount() == 2) {
            Collection<T> targetCollection = (Collection<T>) args[0];
            int size = (int) args[1];
            gate.enter();
            try {

                List<Long> idxs = (List<Long>) cache.invoke(queueKey, new DrainProcessor(gridCacheQueueAdapter.id(), size)).get();

                if (idxs == null || idxs.isEmpty())
                    return 0;

                if (idxs.get(0).equals(Long.MIN_VALUE))
                    gridCacheQueueAdapter.onRemoved(true);

                long stop = U.currentTimeMillis() + RETRY_TIMEOUT;

                for (Long idx : idxs) {
                    QueueItemKey key = (QueueItemKey) itemKeyMethod.invoke(gridCacheQueueAdapter, idx);
                    T data = (T) cache.getAndRemove(key);

                    if (data != null) {
                        targetCollection.add(data);
                        continue;
                    }


                    while (U.currentTimeMillis() < stop) {
                        data = (T)cache.getAndRemove(key);

                        if (data != null) {
                            targetCollection.add(data);
                            continue;
                        }
                    }
//                        log.warn("Failed to get item due to drain timeout [queue={}, size={}]. " +
//                                "Poll timeout can be redefined by 'IGNITE_ATOMIC_CACHE_QUEUE_RETRY_TIMEOUT' system property.", gridCacheQueueAdapter.name(), size);
                    break;
                }
                return targetCollection.size();
            }
            catch (IgniteCheckedException e) {
                throw U.convertException(e);
            } finally {
                gate.leave();
            }
        } else {
            return method.invoke(gridCacheQueueProxy, args);
        }
    }


    /**
     */
    protected static class DrainProcessor implements
            EntryProcessor<GridCacheQueueHeaderKey, GridCacheQueueHeader, List<Long>>, Externalizable {
        /** */
        private static final long serialVersionUID = 0L;

        /** */
        private IgniteUuid id;

        private int size;

        /**
         * Required by {@link Externalizable}.
         */
        public DrainProcessor() {
            // No-op.
        }

        /**
         * @param id Queue unique ID.
         */
        public DrainProcessor(IgniteUuid id, int size) {
            this.id = id;
            this.size = size;
        }

        /** {@inheritDoc} */
        @Override public List<Long> process(
                MutableEntry<GridCacheQueueHeaderKey, GridCacheQueueHeader> e, Object... args) {
            GridCacheQueueHeader hdr = e.getValue();

            boolean rmvd = (hdr == null || !id.equals(hdr.id()));

            if (rmvd || hdr.empty())
                return rmvd ? Arrays.asList(Long.MIN_VALUE) : null;

            Set<Long> rmvdIdxs = hdr.removedIndexes();
            List<Long> result = new LinkedList<>();

            if (rmvdIdxs == null) {
                GridCacheQueueHeader newHdr = new GridCacheQueueHeader(hdr.id(),
                        hdr.capacity(),
                        hdr.collocated(),
                        hdr.head() + size >= hdr.tail() ? hdr.tail() : hdr.head() + size,
                        hdr.tail(),
                        null);

                e.setValue(newHdr);


                for (long l = hdr.head(); l < newHdr.head(); l++) {
                    result.add(l);
                }

                return result;
            }

            long next = hdr.head();
            int counter = 0;
            rmvdIdxs = new HashSet<>(rmvdIdxs);

            do {
                if (!rmvdIdxs.remove(next)) {

                    result.add(next);
                    counter++;
                }

                next++;
            } while (next != hdr.tail() && counter < size);

            GridCacheQueueHeader newHdr = new GridCacheQueueHeader(hdr.id(),
                    hdr.capacity(),
                    hdr.collocated(),
                    next,
                    hdr.tail(),
                    rmvdIdxs.isEmpty() ? null : rmvdIdxs);

            e.setValue(newHdr);

            return result;
        }

        /** {@inheritDoc} */
        @Override public void writeExternal(ObjectOutput out) throws IOException {
            U.writeGridUuid(out, id);
            out.writeInt(size);
        }

        /** {@inheritDoc} */
        @Override public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            id = U.readGridUuid(in);
            size = in.readInt();
        }
    }
}

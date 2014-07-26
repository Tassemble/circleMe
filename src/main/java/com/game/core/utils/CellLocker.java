package com.game.core.utils;


import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 按对象进行锁，比如对于满足name="abc" id=123的对象A 进行锁，这样，如果A并发操作的话就会锁住，但不会锁住对象 为name="acd" id=123的对象
 * 
 * @author CHQ
 * @date 2012-8-11
 * @since alpha2
 */
public class CellLocker<K> {

    private static final Logger LOG              = LoggerFactory.getLogger(CellLocker.class);

    static final int            MAX_SEGMENTS     = 1 << 16;

    static final int            MAXIMUM_CAPACITY = 1 << 30;

    final Segment<K>[]          segments;

    final int                   segmentMask;

    final int                   segmentShift;

    final Segment<K> segmentFor(int hash) {
        return segments[(hash >>> segmentShift) & segmentMask];
    }

    public CellLocker(int concurrencyLevel) {
        if (concurrencyLevel > MAX_SEGMENTS) concurrencyLevel = MAX_SEGMENTS;

        int sshift = 0;
        int ssize = 1;
        while (ssize < concurrencyLevel) {
            ++sshift;
            ssize <<= 1;
        }
        segmentShift = 32 - sshift;
        segmentMask = ssize - 1;
        this.segments = Segment.newArray(ssize);

        for (int i = 0; i < this.segments.length; ++i)
            this.segments[i] = new Segment<K>();
    }

    /**
     * @param name 只是标识用的
     * @param key 会使用key的hashcode，因此，如果不是java内部对象的话，则需要有hashcode
     */
    public void lock(String name, K key) {
        if (key == null) throw new NullPointerException();
        int hash = hash(key.hashCode());
        segmentFor(hash).lock();
        if (LOG.isDebugEnabled()) {
            LOG.debug("name:" + name + " entered!");
        }
    }

    public void unLock(String name, K key) {
        if (key == null) throw new NullPointerException();
        int hash = hash(key.hashCode());
        if (LOG.isDebugEnabled()) {
            LOG.debug("name:" + name + " went out!");
        }
        segmentFor(hash).unlock();
    }

    private static int hash(int h) {
        // Spread bits to regularize both segment and index locations,
        // using variant of single-word Wang/Jenkins hash.
        h += (h << 15) ^ 0xffffcd7d;
        h ^= (h >>> 10);
        h += (h << 3);
        h ^= (h >>> 6);
        h += (h << 2) + (h << 14);
        return h ^ (h >>> 16);
    }

    static final class Segment<K> extends ReentrantLock {

        private static final long serialVersionUID = 1L;
        volatile int              cnt              = 0;

        @SuppressWarnings("unchecked")
        static final <K> Segment<K>[] newArray(int i) {
            return new Segment[i];
        }

    }

    public static void main(String[] args) {
        System.out.println(1 << 16);
        int concurrencyLevel = 1 << 16;
        int sshift = 0;
        int ssize = 1;
        System.out.println("sshift" + sshift);
        System.out.println("ssize" + sshift);
        while (ssize < concurrencyLevel) {
            ++sshift;
            ssize <<= 1;
            System.out.println("sshift" + sshift);
            System.out.println("ssize" + ssize);
        }
    }
}

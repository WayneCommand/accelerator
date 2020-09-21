package ltd.inmind.accelerator.utils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author shenlan#https://github.com/shenlanAZ
 * @version 0.5
 */
public class KVPlusMap<K, V> extends AbstractMap<K, V> {

    private Map<K, V> kvHashMap;
    private Map<K, Long> expiryMap;

    /**
     * default expiry time (ms).
     * if value = -1, this kv never expired.
     */
    private static final long DEFAULT_EXPIRED_TIME = -1L;
    /**
     * The load factor used when none specified in constructor.
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    /**
     * The default initial capacity - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
    /**
     * The value affect gc
     *
     * @serial
     */
    final int threshold = 1 << 20; // aka 1,048,576
    /**
     * The value affect gc run
     */
    int thresholdCount;

    final float loadFactor;

    public KVPlusMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public KVPlusMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public KVPlusMap(int initialCapacity, float _loadFactor) {
        loadFactor = _loadFactor;
        kvHashMap = new HashMap<>(initialCapacity,loadFactor);
        expiryMap = new HashMap<>(initialCapacity,loadFactor);
    }

    @Override
    public synchronized V put(K key, V value) {
        return put(key, value, DEFAULT_EXPIRED_TIME);
    }

    public synchronized V put(K key, V value,long expiryTime) {
        if (expiryTime < 0)
            expiryTime = DEFAULT_EXPIRED_TIME;

        V put = kvHashMap.put(key, value);

        if (expiryTime == DEFAULT_EXPIRED_TIME)
            expiryMap.put(key, expiryTime);
        else
            expiryMap.put(key, System.currentTimeMillis() + expiryTime);

        if (++thresholdCount > threshold){
            gc();
            thresholdCount = 0;
        }

        return put;
    }

    public synchronized void gc() {
        int initialCapacity = size();
        Map<K, V> _kvHashMap;
        Map<K, Long> _expiryMap;

        _kvHashMap = new HashMap<>(initialCapacity, loadFactor);
        _expiryMap = new HashMap<>(initialCapacity, loadFactor);
        forEach((key, value) -> {
            _kvHashMap.put(key, value);
            _expiryMap.put(key, expiryMap.get(key));
        });

        kvHashMap = _kvHashMap;
        expiryMap = _expiryMap;
    }

    @Override
    public synchronized V get(Object key) {
        if (isExpired(key))
            return null;
        return kvHashMap.get(key);
    }

    @Override
    public synchronized boolean remove(Object key, Object value) {
        if (isExpired(key))
            return false;

        boolean remove = kvHashMap.remove(key, value);
        if (remove){
            expiryMap.remove(key);
        }
        return remove;
    }


    @Override
    public synchronized V remove(Object key) {
        if (isExpired(key))
            return null;

        expiryMap.remove(key);
        return kvHashMap.remove(key);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return kvHashMap.entrySet().stream().filter(entry -> !isExpired(entry.getKey()))
                .collect(Collectors.toSet());
    }

    private synchronized boolean isExpired(Object key) {
        if (!expiryMap.containsKey(key))
            return true;

        Long expiryTime  = expiryMap.get(key);

        if (expiryTime == DEFAULT_EXPIRED_TIME)
            return false;

        return System.currentTimeMillis() > expiryTime;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        int modCount = size();

        super.forEach(action);

        if (modCount != size())
            throw new ConcurrentModificationException();
    }

    @Override
    public boolean containsKey(Object key) {
        return !isExpired(key);
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}

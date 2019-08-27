package ltd.inmind.accelerator.utils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author shenlan#https://github.com/shenlanAZ
 * @version 0.2
 */
public class KVPlusMap<K, V> extends AbstractMap<K, V> {

    private Map<K, V> kvHashMap = new HashMap<>();
    private Map<K, Long> expiryMap = new HashMap<>();

    /**
     * default expiry time (ms).
     * if value = -1, this kv never expired.
     */
    private static final long DEFAULT_EXPIRED_TIME = -1L;

    @Override
    public synchronized V put(K key, V value) {
        V put = kvHashMap.put(key, value);
        expiryMap.put(key, DEFAULT_EXPIRED_TIME);
        return put;
    }

    public synchronized V put(K key, V value,long expiryTime) {
        if (expiryTime < 1)
            return null;

        V put = kvHashMap.put(key, value);
        expiryMap.put(key, System.currentTimeMillis() + expiryTime);
        return put;
    }



    @Override
    public synchronized V get(Object key) {
        if (isExpired(key))
            return null;
        return kvHashMap.get(key);
    }

    @Override
    public synchronized boolean remove(Object key, Object value) {
        if (isExpired(key, true))
            return false;

        expiryMap.remove(key);
        return kvHashMap.remove(key, value);
    }


    @Override
    public synchronized V remove(Object key) {
        if (isExpired(key, true))
            return null;

        expiryMap.remove(key);
        return kvHashMap.remove(key);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return kvHashMap.entrySet().stream().filter(entry -> isExpired(entry.getKey()))
                .collect(Collectors.toSet());
    }


    /**
     * 是否已经过期 默认不移除
     *
     * @return true = 过期 ， false = 有效。
     */
    private synchronized boolean isExpired(Object key) {
        return isExpired(key, false);
    }

    private synchronized boolean isExpired(Object key, boolean remove) {
        if (!expiryMap.containsKey(key))
            return true;

        Long expiryTime  = expiryMap.get(key);

        if (expiryTime == DEFAULT_EXPIRED_TIME)
            return false;

        if (System.currentTimeMillis() > expiryTime){
            if (remove){
                kvHashMap.remove(key);
                expiryMap.remove(key);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return !isExpired(key, true);
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        if (action == null)
            throw new NullPointerException();

        int modCount = kvHashMap.size();

        kvHashMap.forEach((k, v) -> {
            if (!isExpired(k))
                action.accept(k, v);

        });

        if (modCount != kvHashMap.size())
            throw new ConcurrentModificationException();
    }

}

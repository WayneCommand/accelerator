package ltd.inmind.user_service.utils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class KVPlusMap<K, V> extends AbstractMap<K, V> {

    private Map<K, V> kvHashMap = new HashMap<>();
    private Map<K, Long> expiryMap = new HashMap<>();

    private static final byte[] LOCK = new byte[0];

    /**
     * default expiry time (ms).
     * if value = -1, this kv never expired.
     */
    private static final long DEFAULT_EXPIRED_TIME = -1L;

    @Override
    public V put(K key, V value) {
        synchronized (LOCK) {
            V put = kvHashMap.put(key, value);
            expiryMap.put(key, DEFAULT_EXPIRED_TIME);
            return put;
        }
    }

    public V put(K key, V value,long expiryTime) {
        synchronized (LOCK) {
            if (expiryTime < 1)
                return null;

            V put = kvHashMap.put(key, value);
            expiryMap.put(key, System.currentTimeMillis() + expiryTime);
            return put;
        }
    }



    @Override
    public V get(Object key) {
        synchronized (LOCK){
            if (isExpired(key))
                return null;
            return kvHashMap.get(key);
        }
    }

    @Override
    public boolean remove(Object key, Object value) {
        synchronized (LOCK){
            if (isExpired(key, true)) {
                return false;
            }
            kvHashMap.remove(key, value);
            expiryMap.remove(key);
            return true;
        }
    }


    @Override
    public V remove(Object key) {
        synchronized (LOCK){
            if (isExpired(key, true)) {
                return null;
            }

            expiryMap.remove(key);
            return kvHashMap.remove(key);
        }
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
    public boolean isExpired(Object key) {
        synchronized (LOCK){
            if (!expiryMap.containsKey(key))
                return true;

            Long expiryTime  = expiryMap.get(key);

            if (expiryTime == DEFAULT_EXPIRED_TIME)
                return false;

            return System.currentTimeMillis() > expiryTime;
        }
    }

    public boolean isExpired(Object key, boolean remove) {
        synchronized (LOCK) {
            if (isExpired(key)){
                if (remove){
                    kvHashMap.remove(key);
                    expiryMap.remove(key);
                }
                return true;
            }
            return false;
        }
    }



    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        if (action == null)
            throw new NullPointerException();

        int modCount = kvHashMap.size();

        kvHashMap.forEach((k, v) -> {
            if (!isExpired(k)) {
                action.accept(k, v);
            }
        });

        if (modCount != kvHashMap.size())
            throw new ConcurrentModificationException();
    }

}

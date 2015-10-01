package de.mytfg.app.android.utils;

import java.util.Map;
import java.util.TreeMap;

/**
 * Maps key / value in both directions by using two HashMaps.
 */
public class BiMap<K, V> {
    private Map<K, V> forward = new TreeMap<>();
    private Map<V, K> backward = new TreeMap<>();
    private int count = 0;

    public synchronized void add(K key, V value) {
        if (!forward.containsKey(key)) {
            count++;
        }
        forward.put(key, value);
        backward.put(value, key);
    }

    public synchronized V getValue(K key) {
        return forward.get(key);
    }

    public synchronized K getKey(V value) {
        return backward.get(value);
    }

    public synchronized int getSize() {
        return count;
    }

    public synchronized boolean containsKey(K key) {
        return forward.containsKey(key);
    }

    public synchronized boolean containsValue(V value) {
        return backward.containsKey(value);
    }
}

package com.zving.framework.collection;

import java.util.Map;

public class CaseIgnoreMapx<K, V> extends Mapx<K, V> {
	private static final long serialVersionUID = 1L;

	public CaseIgnoreMapx() {
	}

	public CaseIgnoreMapx(Map<? extends K, ? extends V> map) {
		putAll(map);
	}

	public void putAll(Map<? extends K, ? extends V> map) {
		for (K k : map.keySet())
			put(k, map.get(k));
	}

	public V put(K key, V value) {
		if ((key != null) && ((key instanceof String))) {
			return super.put((K)key.toString().toLowerCase(), value);
		}
		return super.put(key, value);
	}

	public V get(Object key) {
		if ((key != null) && ((key instanceof String))) {
			return super.get(key.toString().toLowerCase());
		}
		return super.get(key);
	}

	public boolean containsKey(Object key) {
		if ((key != null) && ((key instanceof String))) {
			return super.containsKey(key.toString().toLowerCase());
		}
		return super.containsKey(key);
	}

	public V remove(Object key) {
		if ((key != null) && ((key instanceof String))) {
			return super.remove(key.toString().toLowerCase());
		}
		return super.remove(key);
	}
}
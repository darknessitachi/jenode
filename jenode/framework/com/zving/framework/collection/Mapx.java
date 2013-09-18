package com.zving.framework.collection;

import com.zving.framework.data.DataColumn;
import com.zving.framework.data.DataTable;
import com.zving.framework.utility.DateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Mapx<K, V> extends LinkedHashMap<K, V> {
	private static final float DEFAULT_LOAD_FACTOR = 0.75F;
	private static final int DEFAULT_INIT_CAPACITY = 16;
	private static final long serialVersionUID = 200904201752L;
	private final int maxCapacity;
	private final boolean maxFlag;
	private int hitCount = 0;

	private int missCount = 0;

	private long lastWarnTime = 0L;
	private ExitEventListener<K, V> exitListener;
	private KeyNotFoundEventListener<K, V> getListener;
	private ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();

	private Lock rlock = this.rwlock.readLock();

	private Lock wlock = this.rwlock.writeLock();

	public Mapx(int maxCapacity, boolean LRUFlag) {
		super(maxCapacity, 0.75F, LRUFlag);
		this.maxCapacity = maxCapacity;
		this.maxFlag = true;
	}

	public Mapx(int maxCapacity) {
		this(maxCapacity, true);
	}

	public Mapx() {
		super(16, 0.75F, false);
		this.maxCapacity = 0;
		this.maxFlag = false;
	}

	public Mapx<K, V> clone() {
		this.wlock.lock();
		try {
			Mapx map = (Mapx) super.clone();
			for (Object k : keyArray()) {
				Object v = get(k);
				if ((v instanceof Mapx)) {
					map.put(k, ((Mapx) v).clone());
				}
			}
			return map;
		} finally {
			this.wlock.unlock();
		}
	}

	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		boolean flag = (this.maxFlag) && (size() > this.maxCapacity);
		if ((flag) && (this.exitListener != null)) {
			this.exitListener.onExit(eldest.getKey(), eldest.getValue());
		}
		return flag;
	}

	public void setExitEventListener(ExitEventListener<K, V> listener) {
		this.exitListener = listener;
	}

	public void setGetEventListener(KeyNotFoundEventListener<K, V> listener) {
		this.getListener = listener;
	}

	public String getString(K key) {
		Object o = get(key);
		if (o == null) {
			return null;
		}
		return o.toString();
	}

	public boolean getBoolean(K key) {
		Object o = get(key);
		if (o == null) {
			return false;
		}
		return "true".equals(o.toString());
	}

	public int getInt(K key) {
		Object o = get(key);
		if ((o instanceof Number))
			return ((Number) o).intValue();
		if (o != null) {
			try {
				return Integer.parseInt(o.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public long getLong(K key) {
		Object o = get(key);
		if ((o instanceof Number))
			return ((Number) o).longValue();
		if (o != null) {
			try {
				return Long.parseLong(o.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0L;
	}

	public Date getDate(K key) {
		Object o = get(key);
		if ((o instanceof Date))
			return (Date) o;
		if (o != null) {
			try {
				return DateUtil.parseDateTime(o.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public ArrayList<K> keyArray() {
		ArrayList list = new ArrayList();
		for (Map.Entry e : entrySet()) {
			list.add(e.getKey());
		}
		return list;
	}

	public ArrayList<V> valueArray() {
		ArrayList list = new ArrayList();
		for (Map.Entry e : entrySet()) {
			list.add(e.getValue());
		}
		return list;
	}

	public V get(Object key) {
		if ((this.getListener != null) && (!containsKey(key))) {
			Object k = key;
			try {
				V v = this.getListener.findKeyValue((K)k);
				put((K)k, v);
				return v;
			} catch (ClassCastException e) {
				return null;
			}
		}
		Object o = null;
		this.rlock.lock();
		try {
			o = super.get(key);
		} finally {
			this.rlock.unlock();
		}
		if (this.maxFlag) {
			if (o == null)
				this.missCount += 1;
			else {
				this.hitCount += 1;
			}
			if ((this.missCount > 1000)
					&& (this.hitCount * 1.0D / this.missCount < 0.1D)
					&& (System.currentTimeMillis() - this.lastWarnTime > 1000000L)) {
				this.lastWarnTime = System.currentTimeMillis();
			}
		}

		return (V)o;
	}

	public boolean containsKey(Object k) {
		this.rlock.lock();
		try {
			return super.containsKey(k);
		} finally {
			this.rlock.unlock();
		}
	}

	public void clear() {
		this.wlock.lock();
		try {
			super.clear();
		} finally {
			this.wlock.unlock();
		}
	}

	public V put(K k, V v) {
		this.wlock.lock();
		try {
			return super.put(k, v);
		} finally {
			this.wlock.unlock();
		}
	}

	public V remove(Object k) {
		this.wlock.lock();
		try {
			return super.remove(k);
		} finally {
			this.wlock.unlock();
		}
	}

	public boolean containsValue(Object v) {
		this.rlock.lock();
		try {
			return super.containsValue(v);
		} finally {
			this.rlock.unlock();
		}
	}

	public static <K, V> Mapx<K, V> convertToMapx(
			Map<? extends K, ? extends V> map) {
		Mapx mapx = new Mapx();
		mapx.putAll(map);
		return mapx;
	}

	public DataTable toDataTable() {
		DataColumn[] dcs = { new DataColumn("Key", 1),
				new DataColumn("Value", 1) };
		Object[] ks = keySet().toArray();
		Object[][] vs = new Object[ks.length][2];
		DataTable dt = new DataTable(dcs, vs);
		for (int i = 0; i < ks.length; i++) {
			dt.set(i, 0, ks[i]);
			dt.set(i, 1, get(ks[i]));
		}
		return dt;
	}
}
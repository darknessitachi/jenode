package com.zving.framework.extend;

import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.User.UserData;
import com.zving.framework.collection.Mapx;
import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.extend.plugin.PluginManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReplaceableItemFactory {
	static Mapx<String, Object> singletonMap;
	static Mapx<String, String> classnameMap;
	static Lock lock = new ReentrantLock();

	private static void init() {
		if (singletonMap == null)
			synchronized (ReplaceableItemFactory.class) {
				if (singletonMap == null) {
					singletonMap = new Mapx();
					classnameMap = new Mapx();
					
					for (PluginConfig pc : PluginManager.getInstance()
							.getAllPluginConfig()) {
						for (String id : pc.getReplaceableItems().keyArray()) {
							ReplaceableItemConfig ric = (ReplaceableItemConfig) pc
									.getReplaceableItems().get(id);
							classnameMap.put(ric.getClassName(), id);
						}
					}
					
				}
			}
	}

	private static Object get(ReplaceableItemConfig ric) {
		Object v = null;
		if (ric.isAlwaysCreate()) {
			return create(ric);
		}
		if (ric.isSingleton()) {
			lock.lock();
			if (singletonMap.containsKey(ric.getID())) {
				return singletonMap.get(ric.getID());
			}
			v = create(ric);
			singletonMap.put(ric.getID(), v);
			lock.unlock();
			return v;
		}
		String id = "_Replaceable_" + ric.getID();
		if (ric.isThreadLocalScope()) {
			if (Current.getValues() == null) {
				return create(ric);
			}
			if (Current.getValues().containsKey(id)) {
				return Current.getValues().get(ric.getID());
			}
			v = create(ric);
			Current.getValues().put(ric.getID(), v);
			return v;
		}
		if (ric.isSessionScope()) {
			if (Current.getUser() == null) {
				return create(ric);
			}
			if (Current.getUser().containsKey(id)) {
				return Current.getUser().get(ric.getID());
			}
			v = create(ric);
			Current.getUser().put(ric.getID(), v);
			return v;
		}
		if (ric.isRequestScope()) {
			if (Current.getRequest() == null) {
				return create(ric);
			}
			if (Current.getRequest().containsKey(id)) {
				return Current.getRequest().get(ric.getID());
			}
			v = create(ric);
			Current.getRequest().put(ric.getID(), v);
			return v;
		}
		return null;
	}

	private static Object create(ReplaceableItemConfig ric) {
		try {
			Class clazz = Class.forName(ric.getClassName());
			return clazz.newInstance();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

	public static <T> T get(Class<T> clazz) {
		init();
		return (T)get((String) classnameMap.get(clazz.getName()));
	}

	public static Object get(String id) {
		init();
		ReplaceableItemConfig ric = ExtendManager.getInstance()
				.findReplaceableItem(id);
		return get(ric);
	}
}
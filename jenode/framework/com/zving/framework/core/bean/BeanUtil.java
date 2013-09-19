package com.zving.framework.core.bean;

import com.zving.framework.collection.Mapx;
import com.zving.framework.core.exception.BeanInitException;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BeanUtil {
	public static void fill(Object bean, Map<?, ?> values) {
		fill(bean, values, "");
	}

	public static void fill(Object bean, Map<?, ?> values, String valuePrefix) {
		Map map = new HashMap();
		for (Iterator localIterator = values.keySet().iterator(); localIterator
				.hasNext();) {
			Object k = localIterator.next();
			if (k != null) {
				String key = k.toString();
				if (key.startsWith(valuePrefix)) {
					map.put(key.substring(valuePrefix.length()), values.get(k));
				}
			}
		}
		BeanDescription bim = BeanManager.getBeanDescription(bean.getClass());
		fillArrays(bean, map);
		fillInnerBeans(bean, map);
		
		for (Object k : map.keySet()) {
			if (k != null) {
				String key = k.toString();
				if ((key.indexOf("[") <= 0) && (key.indexOf(".") <= 0)) {
					Object v = map.get(k);
					BeanProperty bip = bim.getProperty(key);
					if (bip != null)
						bip.write(bean, v);
				}
			}
		}
		
	}

	private static void fillArrays(Object bean, Map<String, Object> map) {
		BeanDescription bim = BeanManager.getBeanDescription(bean.getClass());
		HashMap<String, Object> arraySizes = new HashMap<String, Object>();
		int i1;
		for (Iterator localIterator = map.keySet().iterator(); localIterator
				.hasNext();) {
			Object k = localIterator.next();
			if (k != null) {
				String key = k.toString();
				if (key.indexOf("[") > 0) {
					i1 = key.indexOf("[");
					int i2 = key.indexOf("]");
					String prefix = key.substring(0, i1);
					int index = 0;
					try {
						index = Integer.parseInt(key.substring(i1 + 1, i2));
						if (arraySizes.get(prefix) == null) {
							arraySizes.put(prefix, Integer.valueOf(index));
						} else if (((Integer) arraySizes.get(prefix))
								.intValue() < index) {
							arraySizes.put(prefix, Integer.valueOf(index));
						}
					} catch (Exception localException) {
					}
				}
			}
		}
		String[] keys = arraySizes.keySet().toArray(new String[0]);
		for (int str1 = 0; str1 < arraySizes.keySet().toArray().length; str1++) {
			Object k = keys[str1];
			String prefix = k.toString();
			BeanProperty bip = bim.getProperty(prefix);
			if ((bip == null) || (!bip.getPropertyType().isArray())) {
				arraySizes.remove(k);
			} else {
				Object[] arr = (Object[]) createArray(bip.getPropertyType(),
						((Integer) arraySizes.get(prefix)).intValue() + 1);
				for (int i = 0; i < arr.length; i++) {
					arr[i] = create(bip.getPropertyType().getComponentType());
					fill(arr[i], map, prefix + "[" + i + "].");
				}
				bip.write(bean, arr);
			}
		}
	}

	private static void fillInnerBeans(Object bean, Map<String, Object> map) {
		BeanDescription bim = BeanManager.getBeanDescription(bean.getClass());
		HashMap<String, String> innerBeans = new HashMap();
		for (Iterator localIterator = map.keySet().iterator(); localIterator
				.hasNext();) {
			Object k = localIterator.next();
			if (k != null) {
				String key = k.toString();
				if (key.indexOf("[") <= 0) {
					int i = key.indexOf(".");
					if (i > 0)
						innerBeans.put(key.substring(0, i), "");
				}
			}
		}
		for (String name : innerBeans.keySet()) {
			BeanProperty bip = bim.getProperty(name);
			if (bip != null) {
				Object v = bip.read(bean);
				if (v == null) {
					v = create(bip.getPropertyType());
					bip.write(bean, v);
				}
				if (!(v instanceof Date))
					fill(v, map, name + ".");
			}
		}
	}

	public static Object create(Class<?> type) {
		try {
			if (Modifier.isAbstract(type.getModifiers())) {
				throw new BeanInitException("Bean class " + type.getName()
						+ " is a abstract class!");
			}
			return type.newInstance();
		} catch (Exception e) {
		}
		throw new BeanInitException("Bean class " + type.getName()
				+ " has not default constructor!");
	}

	public static Object createArray(Class<?> type, int length) {
		try {
			if (!type.isArray()) {
				return null;
			}
			if (Modifier.isAbstract(type.getComponentType().getModifiers())) {
				throw new BeanInitException("Bean class " + type.getName()
						+ " is a abstract class!");
			}
			return Array.newInstance(type.getComponentType(), length);
		} catch (Exception e) {
		}
		throw new BeanInitException("Bean class " + type.getName()
				+ " has not default constructor!");
	}

	public static void copy(Object srcBean, Object targetBean) {
		if ((srcBean instanceof Map)) {
			fill(targetBean, (Map) srcBean);
		}
		copyProperties(srcBean, targetBean);
	}

	public static void copyProperties(Object srcBean, Object targetBean) {
		BeanDescription m1 = BeanManager.getBeanDescription(srcBean.getClass());
		BeanDescription m2 = BeanManager.getBeanDescription(targetBean
				.getClass());
		
		for (String k1 : m1.getPropertyMap().keyArray()) {
			for (String k2 : m2.getPropertyMap().keyArray()) {
				if (k1.equals(k2))
					m2.getProperty(k2).write(targetBean,
							m1.getProperty(k1).read(srcBean));
			}
		}
	}

	public static Object get(Object bean, String field) {
		return null;
	}

	public static void set(Object bean, String field, Object value) {
	}

	public static Map<String, Object> toMap(Object bean) {
		return null;
	}
}
package com.zving.framework.utility;

import com.zving.framework.collection.Filter;
import com.zving.framework.collection.Mapx;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ObjectUtil {
	public static Comparator<String> ASCStringComparator = new Comparator<String>() {
		public int compare(String s1, String s2) {
			if (s1 == null) {
				if (s2 == null) {
					return 0;
				}
				return -1;
			}

			if (s2 == null) {
				return 1;
			}
			return s1.compareTo(s2);
		}
	};

	public static Comparator<String> DESCStringComparator = new Comparator<String>() {
		public int compare(String o1, String o2) {
			return -ObjectUtil.ASCStringComparator.compare(o1, o2);
		}
	};

	public static boolean in(Object[] args) {
		if ((args == null) || (args.length < 2)) {
			return false;
		}
		Object arg1 = args[0];
		for (int i = 1; i < args.length; i++) {
			if (arg1 == null) {
				if (args[i] == null)
					return true;
			} else {
				if (arg1.equals(args[i])) {
					return true;
				}

				if ((!arg1.getClass().isArray()) && (args[i] != null)
						&& (args[i].getClass().isArray())) {
					for (Object obj : (Object[]) args[i]) {
						if (arg1.equals(obj)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public static boolean notIn(Object[] args) {
		return !in(args);
	}

	public static boolean empty(Object obj) {
		if (obj == null) {
			return true;
		}
		if ((obj instanceof String)) {
			return obj.equals("");
		}
		if ((obj instanceof Number)) {
			return ((Number) obj).doubleValue() == 0.0D;
		}
		if (obj.getClass().isArray()) {
			return Array.getLength(obj) == 0;
		}
		if ((obj instanceof Collection)) {
			return ((Collection) obj).size() == 0;
		}
		return false;
	}

	public static boolean notEmpty(Object obj) {
		return !empty(obj);
	}

	public static boolean equal(Object obj1, Object obj2) {
		if (obj1 == obj2) {
			return true;
		}
		if (obj1 == null) {
			if (obj2 == null) {
				return true;
			}
			return false;
		}

		return obj1.equals(obj2);
	}

	public static boolean notEqual(Object obj1, Object obj2) {
		return !equal(obj1, obj2);
	}

	public static Number minNumber(double[] args) {
		if ((args == null) || (args.length == 0)) {
			return null;
		}
		Number minus = null;
		double[] arrayOfDouble = args;
		int j = args.length;
		for (int i = 0; i < j; i++) {
			double t = arrayOfDouble[i];
			if (minus == null) {
				minus = Double.valueOf(t);
			} else if (minus.doubleValue() > t) {
				minus = Double.valueOf(t);
			}
		}
		return minus;
	}

	public static Number maxNumber(double[] args) {
		if ((args == null) || (args.length == 0)) {
			return null;
		}
		Number max = null;
		double[] arrayOfDouble = args;
		int j = args.length;
		for (int i = 0; i < j; i++) {
			double t = arrayOfDouble[i];
			if (max == null) {
				max = Double.valueOf(t);
			} else if (max.doubleValue() < t) {
				max = Double.valueOf(t);
			}
		}
		return max;
	}

	public static <T extends Comparable<T>> T min(T[] args) {
		if ((args == null) || (args.length == 0)) {
			return null;
		}
		T minus = null;
		T[] arrayOfComparable = args;
		int j = args.length;
		for (int i = 0; i < j; i++) {
			T t = arrayOfComparable[i];
			if ((minus == null) && (t != null)) {
				minus = t;
			} else if (minus.compareTo(t) > 0) {
				minus = t;
			}
		}
		return minus;
	}

	public static <T extends Comparable<T>> T max(T[] args) {
		if ((args == null) || (args.length == 0)) {
			return null;
		}
		T max = null;
		T[] arrayOfComparable = args;
		int j = args.length;
		for (int i = 0; i < j; i++) {
			T t = arrayOfComparable[i];
			if ((max == null) && (t != null)) {
				max = t;
			} else if (max.compareTo(t) < 1) {
				max = t;
			}
		}
		return max;
	}

	public static <T> T ifEmpty(T obj1, T obj2) {
		return empty(obj1) ? obj2 : obj1;
	}

	public static String toString(Object obj) {
		return obj == null ? null : obj.toString();
	}

	public static <T> List<T> toList(T[] args) {
		ArrayList list = new ArrayList();
		Object[] arrayOfObject = args;
		int j = args.length;
		for (int i = 0; i < j; i++) {
			Object t = arrayOfObject[i];
			list.add(t);
		}
		return list;
	}

	public static List<String> toStringList(Collection<?> list) {
		List r = new ArrayList();
		for (Iterator localIterator = list.iterator(); localIterator.hasNext();) {
			Object obj = localIterator.next();
			r.add(obj == null ? null : obj.toString());
		}
		return r;
	}

	public static List<String> toStringList(Object[] arr) {
		List r = new ArrayList();
		Object[] arrayOfObject = arr;
		int j = arr.length;
		for (int i = 0; i < j; i++) {
			Object obj = arrayOfObject[i];
			r.add(obj == null ? null : obj.toString());
		}
		return r;
	}

	public static Map<String, Object> toStringObjectMap(Map<?, ?> map) {
		Mapx r = new Mapx();
		for (Map.Entry e : map.entrySet()) {
			r.put(e.getKey() == null ? null : e.getKey().toString(),
					e.getValue());
		}
		return r;
	}

	public static Map<String, String> toStringStringMap(Map<?, ?> map) {
		Mapx r = new Mapx();
		for (Map.Entry e : map.entrySet()) {
			r.put(e.getKey() == null ? null : e.getKey().toString(),
					e.getValue() == null ? null : e.getValue().toString());
		}
		return r;
	}

	public static <T> T[] filter(T[] arr, Filter<T> filter) {
		if ((arr == null) || (arr.length == 0)) {
			return arr;
		}
		ArrayList list = new ArrayList();
		T[] localObject1 = arr;
		int j = arr.length;
		for (int i = 0; i < j; i++) {
			T t = localObject1[i];
			if (filter.filter(t)) {
				list.add(t);
			}
		}

		T[] a = (T[]) Array.newInstance(arr.getClass()
				.getComponentType(), list.size());
		int i = 0;
		
		for (T t : localObject1) {
			a[(i++)] = t;
		}
		return a;
	}

	public static <T> List<T> filter(List<T> arr, Filter<T> filter) {
		if ((arr == null) || (arr.size() == 0)) {
			return arr;
		}
		try {
			List<T> list = (List<T>) arr.getClass().newInstance();
			for (T t : arr) {
				if (filter.filter(t)) {
					list.add(t);
				}
			}
			return list;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <K, V> Mapx<K, V> filter(Mapx<K, V> map,
			Filter<Map.Entry<K, V>> filter) {
		if ((map == null) || (map.size() == 0)) {
			return map;
		}
		try {
			Mapx map2 = (Mapx) map.getClass().newInstance();
			for (Map.Entry t : map.entrySet()) {
				if (filter.filter(t)) {
					map2.put(t.getKey(), t.getValue());
				}
			}
			return map2;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int[] toIntArray(Object[] arr) {
		int[] r = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			r[i] = Integer.parseInt(String.valueOf(arr[i]));
		}
		return r;
	}

	public static long[] toLongArray(Object[] arr) {
		long[] r = new long[arr.length];
		for (int i = 0; i < arr.length; i++) {
			r[i] = Long.parseLong(String.valueOf(arr[i]));
		}
		return r;
	}

	public static float[] toFloatArray(Object[] arr) {
		float[] r = new float[arr.length];
		for (int i = 0; i < arr.length; i++) {
			r[i] = Float.parseFloat(String.valueOf(arr[i]));
		}
		return r;
	}

	public static double[] toDoubleArray(Object[] arr) {
		double[] r = new double[arr.length];
		for (int i = 0; i < arr.length; i++) {
			r[i] = Double.parseDouble(String.valueOf(arr[i]));
		}
		return r;
	}

	public static boolean[] toBooleanArray(Object[] arr) {
		boolean[] r = new boolean[arr.length];
		for (int i = 0; i < arr.length; i++) {
			r[i] = Boolean.valueOf(String.valueOf(arr[i])).booleanValue();
		}
		return r;
	}

	public static String[] toStringArray(Collection<?> list) {
		String[] r = new String[list.size()];
		int i = 0;
		for (Iterator localIterator = list.iterator(); localIterator.hasNext();) {
			Object obj = localIterator.next();
			r[(i++)] = (obj == null ? null : obj.toString());
		}
		return r;
	}

	public static String[] toStringArray(Object[] arr) {
		String[] r = new String[arr.length];
		Object[] arrayOfObject = arr;
		int j = arr.length;
		for (int i = 0; i < j; i++) {
			Object obj = arrayOfObject[i];
			r[i] = (obj == null ? null : obj.toString());
		}
		return r;
	}

	public static <T> Object[] toObjectArray(T[] arr) {
		Object[] r = new Object[arr.length];
		for (int i = 0; i < arr.length; i++) {
			r[i] = arr[i];
		}
		return r;
	}

	public static Object[] toObjectArray(Object arrayObject) {
		if (arrayObject == null) {
			return null;
		}
		if (!arrayObject.getClass().isArray()) {
			throw new RuntimeException("Not an array!");
		}
		if ((arrayObject instanceof byte[])) {
			return toObjectArray((byte[]) arrayObject);
		}
		if ((arrayObject instanceof short[])) {
			return toObjectArray((short[]) arrayObject);
		}
		if ((arrayObject instanceof int[])) {
			return toObjectArray((int[]) arrayObject);
		}
		if ((arrayObject instanceof long[])) {
			return toObjectArray((long[]) arrayObject);
		}
		if ((arrayObject instanceof float[])) {
			return toObjectArray((float[]) arrayObject);
		}
		if ((arrayObject instanceof double[])) {
			return toObjectArray((double[]) arrayObject);
		}
		if ((arrayObject instanceof boolean[])) {
			return toObjectArray((boolean[]) arrayObject);
		}
		return (Object[]) arrayObject;
	}

	public static <T> T[] sort(T[] arr, Comparator<T> c) {
		if ((arr == null) || (arr.length == 0)) {
			return arr;
		}

		T[] a = (T[]) Array.newInstance(arr.getClass()
				.getComponentType(), arr.length);
		T[] arrayOfObject1 = arr;
		int j = arr.length;
		for (int i = 0; i < j; i++) {
			T t = arrayOfObject1[i];
			a[(i++)] = t;
		}
		Arrays.sort(a, c);
		return a;
	}

	public static <T> List<T> sort(List<T> arr, Comparator<T> c) {
		if ((arr == null) || (arr.size() == 0)) {
			return arr;
		}
		try {
			T[] a = (T[]) Array.newInstance(arr.toArray().getClass()
					.getComponentType(), arr.size());
			a = arr.toArray(a);
			Arrays.sort(a, c);

			List list = (List) arr.getClass().newInstance();
			for (Object t : a) {
				list.add(t);
			}
			return list;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <K, V> Mapx<K, V> sort(Mapx<K, V> map, Comparator<Map.Entry<K, V>> c)
  {
    if ((map == null) || (map.size() == 0)) {
      return map;
    }
    try
    {
      Map.Entry<K, V>[] a = (Map.Entry<K, V>[])Array.newInstance(Entry.class.getComponentType(), map.size());

      Mapx map2 = (Mapx)map.getClass().newInstance();
      a = (Map.Entry[])map.entrySet().toArray(a);
      Arrays.sort(a, c);
      for (Map.Entry t : a) {
        map2.put(t.getKey(), t.getValue());
      }
      return map2;
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

	public static String getCurrentStack() {
		return getStack(new Throwable());
	}

	public static String getStack(Throwable t) {
		StackTraceElement[] stack = t.getStackTrace();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < stack.length; i++) {
			StackTraceElement ste = stack[i];
			if (ste.getClassName().indexOf("ObjectUtil.getCurrentStack") == -1) {
				sb.append("\tat ");
				sb.append(ste.getClassName());
				sb.append(".");
				sb.append(ste.getMethodName());
				sb.append("(");
				sb.append(ste.getFileName());
				sb.append(":");
				sb.append(ste.getLineNumber());
				sb.append(")\n");
			}
		}
		return sb.toString();
	}

	public static <T> T[] copyOf(T[] arr, int length) {
		T[] copy = (T[]) Array.newInstance(arr.getClass()
				.getComponentType(), length);
		System.arraycopy(arr, 0, copy, 0, Math.min(arr.length, length));
		return copy;
	}

	public static byte[] copyOf(byte[] arr, int length) {
		byte[] copy = new byte[length];
		System.arraycopy(arr, 0, copy, 0, Math.min(arr.length, length));
		return copy;
	}

	public static boolean[] copyOf(boolean[] arr, int length) {
		boolean[] copy = new boolean[length];
		System.arraycopy(arr, 0, copy, 0, Math.min(arr.length, length));
		return copy;
	}

	public static float[] copyOf(float[] arr, int length) {
		float[] copy = new float[length];
		System.arraycopy(arr, 0, copy, 0, Math.min(arr.length, length));
		return copy;
	}

	public static int[] copyOf(int[] arr, int length) {
		int[] copy = new int[length];
		System.arraycopy(arr, 0, copy, 0, Math.min(arr.length, length));
		return copy;
	}

	public static boolean isBoolean(String v) {
		return ("true".equals(v)) || ("false".equals(v));
	}

	public static boolean isTrue(Object v) {
		if (v == null) {
			return false;
		}
		if ((v instanceof Boolean)) {
			return ((Boolean) v).booleanValue();
		}
		return "true".equals(v.toString());
	}
}
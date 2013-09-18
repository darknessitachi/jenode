package com.zving.framework.utility;

import com.zving.framework.core.castor.BooleanCastor;
import com.zving.framework.core.castor.DoubleCastor;
import com.zving.framework.core.castor.FloatCastor;
import com.zving.framework.core.castor.IntCastor;
import com.zving.framework.core.castor.LongCastor;

public class Primitives {
	public static boolean isPrimitives(Object obj)
  {
    if (ObjectUtil.in(new Object[] { 
    		obj.getClass(), 
    		Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Boolean.TYPE, 
    		int.class, long.class, float.class, double.class, 
    		Integer.class, Long.class, Float.class, Double.class, 
    		Integer[].class, Long[].class, Float[].class, Double[].class, Boolean.class })) {
      return true;
    }
    return false;
  }

	public static double getDouble(Object obj) {
		return ((Number) DoubleCastor.getInstance().cast(obj, Double.class))
				.doubleValue();
	}

	public static double getFloat(Object obj) {
		return ((Number) FloatCastor.getInstance().cast(obj, Float.class))
				.floatValue();
	}

	public static long getLong(Object obj) {
		return ((Number) LongCastor.getInstance().cast(obj, Long.class))
				.longValue();
	}

	public static int getInteger(Object obj) {
		return ((Number) IntCastor.getInstance().cast(obj, Integer.class))
				.intValue();
	}

	public static Boolean getBoolean(boolean flag) {
		return flag ? Boolean.TRUE : Boolean.FALSE;
	}

	public static boolean getBoolean(Object obj) {
		return ((Boolean) BooleanCastor.getInstance().cast(obj, Boolean.class))
				.booleanValue();
	}
}
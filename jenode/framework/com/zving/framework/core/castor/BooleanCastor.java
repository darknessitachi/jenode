package com.zving.framework.core.castor;

import com.zving.framework.utility.NumberUtil;

public class BooleanCastor extends AbstractInnerCastor
{
  private static BooleanCastor singleton = new BooleanCastor();

  public static BooleanCastor getInstance() {
    return singleton;
  }

  public boolean canCast(Class<?> type)
  {
    return (Boolean.class == type) || (Boolean.TYPE == type);
  }

  public Object cast(Object obj, Class<?> type) {
    if (obj == null) {
      return Boolean.valueOf(false);
    }
    if ((obj instanceof Number)) {
      if (((Number)obj).doubleValue() > 0.0D) return Boolean.valueOf(true); return Boolean.valueOf(false);
    }if ((obj instanceof Boolean))
      return Boolean.valueOf(((Boolean)obj).booleanValue());
    if ((obj instanceof String)) {
      if ((obj.equals("")) || (obj.equals("false")) || (obj.equals("null"))) {
        return Boolean.valueOf(false);
      }
      if (NumberUtil.isNumber((String)obj)) {
        if (Double.parseDouble((String)obj) > 0.0D) return Boolean.valueOf(true); return Boolean.valueOf(false);
      }
      return Boolean.valueOf(true);
    }
    return Boolean.valueOf(false);
  }
}
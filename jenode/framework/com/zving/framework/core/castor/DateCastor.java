package com.zving.framework.core.castor;

import com.zving.framework.utility.DateUtil;
import java.util.Date;

public class DateCastor extends AbstractInnerCastor
{
  private static DateCastor singleton = new DateCastor();

  public static DateCastor getInstance() {
    return singleton;
  }

  public boolean canCast(Class<?> type)
  {
    return Date.class == type;
  }

  public Object cast(Object obj, Class<?> type) {
    if (obj == null) {
      return null;
    }
    if ((obj instanceof Date))
      return (Date)obj;
    if ((obj instanceof Long)) {
      return new Date(((Long)obj).longValue());
    }
    return DateUtil.parseDateTime(obj.toString());
  }
}
package com.zving.framework.security;

import com.zving.framework.Current;
import com.zving.framework.annotation.Verify;
import com.zving.framework.collection.Mapx;
import com.zving.framework.core.method.IMethodLocator;
import com.zving.framework.extend.ExtendManager;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;

public class VerifyCheck
{
  public static boolean check(IMethodLocator m)
  {
    if (m == null) {
      return false;
    }

    boolean flag = m.isAnnotationPresent(Verify.class);
    if (flag) {
      Verify v = (Verify)m.getAnnotation(Verify.class);
      boolean ignoreAll = v.ignoreAll();
      if (ignoreAll) {
        return true;
      }
      String nocheck = v.ignoredKeys();
      Mapx map = new Mapx();
      String[] rules = v.value();
      for (String r : rules) {
        int i = r.indexOf("=");
        if (i >= 0)
        {
          String name = r.substring(0, i);
          String value = r.substring(i + 1);
          map.put(name, value);
        }
      }
      return check(m, Current.getRequest(), nocheck, map);
    }
    return check(m, Current.getRequest(), null, new Mapx());
  }

  public static boolean check(Mapx<String, Object> data, String nocheck, Mapx<String, String> rules)
  {
    return check(null, data, nocheck, rules);
  }

  public static boolean check(IMethodLocator m, Mapx<String, Object> data, String nocheck, Mapx<String, String> rules) {
    String nocheck2 = "," + nocheck + ",";
    for (String k : data.keySet())
      if ((data.get(k) instanceof String))
      {
        String v = data.getString(k);
        if ((!ObjectUtil.empty(v)) && (nocheck2.indexOf(k) <= 0))
        {
          if (!k.startsWith("_ZVING_"))
          {
            if (rules.containsKey(k)) {
              VerifyRule verify = new VerifyRule((String)rules.get(k));
              if (!verify.verify(v)) {
                log(m, k, v, (String)rules.get(k));
                return false;
              }
            } else {
              v = StringUtil.htmlTagEncode(v);

              if (v.indexOf("'") >= 0) {
                v = v.replaceAll("'", "&#39;");
              }
              if (v.indexOf("\"") >= 0) {
                v = v.replaceAll("\"", "&quot;");
              }
              data.put(k, v);
            }
          }
        }
      }
    return true;
  }

  private static void log(IMethodLocator m, String k, String v, String rule) {
    String methodName = m == null ? "-" : m.getName();

    ExtendManager.invoke("com.zving.framework.AfterPrivCheckFailedAction", new Object[] { methodName, k, v, rule });
  }
}
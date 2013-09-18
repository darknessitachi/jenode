package com.zving.framework.core.method;

import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.UIFacade;
import com.zving.framework.annotation.Priv;
import com.zving.framework.annotation.util.AliasMapping;
import com.zving.framework.collection.Mapx;
import com.zving.framework.core.exception.UIMethodNotFoundException;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.StringUtil;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MethodLocatorUtil
{
  static Mapx<String, IMethodLocator> map = new Mapx();

  public static IMethodLocator create(String fullName)
  {
    try {
      if (fullName.indexOf("$") > 0) {
        Class clazz = Class.forName(fullName);
        if (!Modifier.isPublic(clazz.getModifiers()))
          return null;
        if (Modifier.isStatic(clazz.getModifiers()))
          return null;
        if (!UIMethod.class.isAssignableFrom(clazz))
          return null;
        if (!UIFacade.class.isAssignableFrom(clazz.getDeclaringClass())) {
          return null;
        }
        return new FacadeInnerClassLocator(clazz);
      }
      if (fullName.startsWith("com.zving.framework.")) {
        int j = fullName.lastIndexOf(".");
        fullName = fullName.substring(0, j) + "#" + fullName.substring(j + 1);
      }
      int i = fullName.lastIndexOf("#");
      if (i < 0) {
        Class clazz = Class.forName(fullName);
        if (!UIMethod.class.isAssignableFrom(clazz)) {
          return null;
        }
        return new OuterClassLocator(clazz);
      }
      String className = fullName.substring(0, i);
      String methodName = fullName.substring(i + 1);
      Class clazz = Class.forName(className);
      if (!UIFacade.class.isAssignableFrom(clazz)) {
        return null;
      }
      for (Method m : clazz.getMethods())
        if (Modifier.isPublic(m.getModifiers()))
        {
          if (!Modifier.isStatic(m.getModifiers()))
          {
            if (m.isAnnotationPresent(Priv.class))
            {
              if (m.getName().equals(methodName))
                return new FacadeMemberMethodLocator(m);
            }
          }
        }
    }
    catch (ClassNotFoundException e) {
      LogUtil.error("UIMethod class [" + fullName + "] not found!");
    }
    return null;
  }

  public static IMethodLocator find(String alias) {
    int i = alias.indexOf("?");
    if (i > 0) {
      String params = alias.substring(i + 1);
      alias = alias.substring(0, i);
      Mapx map = StringUtil.splitToMapx(params, "&", "=");
      if (Current.getRequest() != null) {
        Current.getRequest().putAll(map);
      }
    }
    String fullName = AliasMapping.get(alias);
    IMethodLocator m = (IMethodLocator)map.get(fullName);
    if (m == null) {
      m = create(fullName);
      map.put(fullName, m);
      if (m == null) {
        throw new UIMethodNotFoundException(alias);
      }
    }
    return m;
  }
}
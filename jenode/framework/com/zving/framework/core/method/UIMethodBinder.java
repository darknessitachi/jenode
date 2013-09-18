package com.zving.framework.core.method;

import com.zving.framework.Current;
import com.zving.framework.collection.Mapx;
import com.zving.framework.core.bean.BeanDescription;
import com.zving.framework.core.bean.BeanManager;
import com.zving.framework.core.bean.BeanProperty;
import com.zving.framework.core.bean.BeanUtil;
import com.zving.framework.expression.Constants;
import com.zving.framework.utility.Primitives;
import java.lang.reflect.Method;

public class UIMethodBinder
{
  public static void bind(UIMethod m, Object[] args)
  {
    BeanUtil.fill(m, Current.getRequest());
    BeanDescription bd = BeanManager.getBeanDescription(m.getClass());
    for (BeanProperty bp : bd.getPropertyMap().valueArray())
      if (bp.read(m) == null)
        for (Object arg : args)
          if ((!Primitives.isPrimitives(arg)) && ((arg instanceof String)) && (bp.getPropertyType().isInstance(arg)))
            bp.write(m, arg);
  }

  public static Object[] convertArg(Method m, Object[] args)
  {
    Class[] cs = m.getParameterTypes();
    if ((cs == null) || (cs.length == 0)) {
      return Constants.NoArgs;
    }
    Object[] arr = new Object[cs.length];
    int i = 0;
    for (Class c : cs) {
      for (Object obj : args) {
        if (c.isInstance(obj)) {
          arr[i] = obj;
          break;
        }
      }
      i++;
    }
    return arr;
  }
}
package com.zving.framework.expression.function;

import com.zving.framework.expression.core.AbstractFunction;
import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IVariableResolver;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

public class Length extends AbstractFunction
{
  public Object execute(IVariableResolver resolver, Object[] args)
    throws ExpressionException
  {
    Object obj = args[0];
    if (obj == null) {
      return Integer.valueOf(0);
    }
    if ((obj instanceof String))
      return Integer.valueOf(((String)obj).length());
    if ((obj instanceof Collection))
      return Integer.valueOf(((Collection)obj).size());
    if ((obj instanceof Map)) {
      return Integer.valueOf(((Map)obj).size());
    }
    int count = 0;
    if ((obj instanceof Iterator)) {
      Iterator iter = (Iterator)obj;
      count = 0;
      while (iter.hasNext()) {
        count++;
        iter.next();
      }
      return Integer.valueOf(count);
    }
    if ((obj instanceof Enumeration)) {
      Enumeration enum_ = (Enumeration)obj;
      count = 0;
      while (enum_.hasMoreElements()) {
        count++;
        enum_.nextElement();
      }
      return Integer.valueOf(count);
    }
    try {
      count = Array.getLength(obj);
      return Integer.valueOf(count);
    } catch (IllegalArgumentException localIllegalArgumentException) {
    }
    throw new ExpressionException("Invalid length property");
  }

  public String getFunctionPrefix() {
    return "";
  }

  public Class<?>[] getArgumentTypes() {
    return AbstractFunction.Arg_Object;
  }

  public String getFunctionName() {
    return "length";
  }
}
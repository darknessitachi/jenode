package com.zving.framework.expression.function;

import com.zving.framework.expression.core.AbstractFunction;
import com.zving.framework.expression.core.IVariableResolver;

public class Join extends AbstractFunction
{
  public Object execute(IVariableResolver resolver, Object[] args)
  {
    String[] array = (String[])args[0];
    String separator = (String)args[1];
    if (array == null)
      return "";
    if (separator == null) {
      separator = "";
    }
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < array.length; i++) {
      buf.append(array[i]);
      if (i < array.length - 1) {
        buf.append(separator);
      }
    }
    return buf.toString();
  }

  public Class<?>[] getArgumentTypes() {
    return new Class[] { String[].class, String.class };
  }

  public String getFunctionPrefix() {
    return "";
  }

  public String getFunctionName() {
    return "join";
  }
}
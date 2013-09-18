package com.zving.framework.expression.function;

import com.zving.framework.expression.core.AbstractFunction;
import com.zving.framework.expression.core.IVariableResolver;

public class Contains extends AbstractFunction
{
  public Object execute(IVariableResolver resolver, Object[] args)
  {
    String input = (String)args[0];
    String substring = (String)args[1];
    if (input == null)
      input = "";
    if (substring == null)
      substring = "";
    return Integer.valueOf(input.indexOf(substring));
  }

  public Class<?>[] getArgumentTypes() {
    return AbstractFunction.Arg_String_String;
  }

  public String getFunctionPrefix() {
    return "";
  }

  public String getFunctionName() {
    return "contains";
  }
}
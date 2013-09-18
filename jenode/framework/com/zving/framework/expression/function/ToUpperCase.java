package com.zving.framework.expression.function;

import com.zving.framework.expression.core.AbstractFunction;
import com.zving.framework.expression.core.IVariableResolver;

public class ToUpperCase extends AbstractFunction
{
  public Object execute(IVariableResolver resolver, Object[] args)
  {
    String input = (String)args[0];
    if (input == null) {
      return "";
    }
    return input.toUpperCase();
  }

  public String getFunctionPrefix() {
    return "";
  }

  public Class<?>[] getArgumentTypes() {
    return AbstractFunction.Arg_String;
  }

  public String getFunctionName() {
    return "toUpperCase";
  }
}
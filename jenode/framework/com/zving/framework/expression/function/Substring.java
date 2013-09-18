package com.zving.framework.expression.function;

import com.zving.framework.expression.core.AbstractFunction;
import com.zving.framework.expression.core.IVariableResolver;

public class Substring extends AbstractFunction
{
  public Object execute(IVariableResolver resolver, Object[] args)
  {
    String input = (String)args[0];
    Integer start = (Integer)args[1];
    Integer end = null;
    if (args.length > 2) {
      end = (Integer)args[2];
    }
    if (input == null) {
      input = "";
    }
    if (end != null) {
      return input.substring(start.intValue(), end.intValue());
    }
    return input.substring(start.intValue());
  }

  public String getFunctionPrefix()
  {
    return "";
  }

  public String getFunctionName() {
    return "substring";
  }

  public Class<?>[] getArgumentTypes() {
    return AbstractFunction.Arg_String_Int_Int;
  }
}
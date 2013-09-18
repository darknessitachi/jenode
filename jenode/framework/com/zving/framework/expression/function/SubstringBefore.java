package com.zving.framework.expression.function;

import com.zving.framework.expression.core.AbstractFunction;
import com.zving.framework.expression.core.IVariableResolver;

public class SubstringBefore extends AbstractFunction
{
  public Object execute(IVariableResolver resolver, Object[] args)
  {
    String input = (String)args[0];
    String substring = (String)args[1];
    if (input == null)
      input = "";
    if (input.length() == 0)
      return "";
    if (substring == null)
      substring = "";
    if (substring.length() == 0) {
      return "";
    }
    int index = input.indexOf(substring);
    if (index == -1) {
      return "";
    }
    return input.substring(0, index);
  }

  public Class<?>[] getArgumentTypes()
  {
    return AbstractFunction.Arg_String_String;
  }

  public String getFunctionPrefix() {
    return "";
  }

  public String getFunctionName() {
    return "substringBefore";
  }
}
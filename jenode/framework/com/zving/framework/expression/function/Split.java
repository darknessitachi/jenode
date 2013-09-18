package com.zving.framework.expression.function;

import com.zving.framework.expression.core.AbstractFunction;
import com.zving.framework.expression.core.IVariableResolver;
import java.util.StringTokenizer;

public class Split extends AbstractFunction
{
  public Object execute(IVariableResolver resolver, Object[] args)
  {
    String input = (String)args[0];
    String delimiters = (String)args[1];

    if (input == null)
      input = "";
    if (input.length() == 0) {
      String[] array = new String[1];
      array[0] = "";
      return array;
    }

    if (delimiters == null) {
      delimiters = "";
    }
    StringTokenizer tok = new StringTokenizer(input, delimiters);
    int count = tok.countTokens();
    String[] array = new String[count];
    int i = 0;
    while (tok.hasMoreTokens()) {
      array[(i++)] = tok.nextToken();
    }
    return array;
  }

  public Class<?>[] getArgumentTypes() {
    return AbstractFunction.Arg_String_String;
  }

  public String getFunctionPrefix() {
    return "";
  }

  public String getFunctionName() {
    return "split";
  }
}
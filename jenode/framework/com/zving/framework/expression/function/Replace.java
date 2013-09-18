package com.zving.framework.expression.function;

import com.zving.framework.expression.core.AbstractFunction;
import com.zving.framework.expression.core.IVariableResolver;
import com.zving.framework.utility.StringUtil;

public class Replace extends AbstractFunction
{
  public Object execute(IVariableResolver resolver, Object[] args)
  {
    String input = (String)args[0];
    String src = (String)args[1];
    String dest = (String)args[2];
    String type = "";
    if (args.length > 3) {
      type = (String)args[3];
    }
    if (input == null)
      input = "";
    if (src == null)
      return input;
    if (dest == null)
      dest = "";
    if ("regex".equalsIgnoreCase(type)) {
      return input.replaceAll(src, dest);
    }
    return StringUtil.replaceEx(input, src, dest);
  }

  public Class<?>[] getArgumentTypes()
  {
    return new Class[] { String.class, String.class, String.class, String.class };
  }

  public String getFunctionPrefix() {
    return "";
  }

  public String getFunctionName() {
    return "replace";
  }
}
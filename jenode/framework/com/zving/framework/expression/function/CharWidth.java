package com.zving.framework.expression.function;

import com.zving.framework.expression.core.AbstractFunction;
import com.zving.framework.expression.core.IVariableResolver;
import com.zving.framework.utility.StringUtil;

public class CharWidth extends AbstractFunction
{
  public Object execute(IVariableResolver resolver, Object[] args)
  {
    String input = (String)args[0];
    Integer charWidth = (Integer)args[1];
    String suffix = "...";
    if (args.length > 2) {
      suffix = (String)args[2];
    }
    if (StringUtil.lengthEx(input) > charWidth.intValue()) {
      return StringUtil.subStringEx(input, charWidth.intValue() - 2) + suffix;
    }
    return input;
  }

  public String getFunctionPrefix()
  {
    return "";
  }

  public String getFunctionName() {
    return "charWidth";
  }

  public Class<?>[] getArgumentTypes() {
    return new Class[] { String.class, Integer.class, String.class };
  }
}
package com.zving.framework.expression.function;

import com.zving.framework.expression.core.AbstractFunction;
import com.zving.framework.expression.core.IVariableResolver;
import com.zving.framework.utility.StringUtil;

public class EscapeXml extends AbstractFunction
{
  public String getFunctionName()
  {
    return "escapeXml";
  }

  public Object execute(IVariableResolver resolver, Object[] args) {
    String input = (String)args[0];
    if (input == null)
      return "";
    return StringUtil.htmlEncode(input);
  }

  public String getFunctionPrefix() {
    return "";
  }

  public Class<?>[] getArgumentTypes() {
    return AbstractFunction.Arg_String;
  }
}
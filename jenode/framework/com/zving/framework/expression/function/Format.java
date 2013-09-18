package com.zving.framework.expression.function;

import com.zving.framework.expression.core.AbstractFunction;
import com.zving.framework.expression.core.IVariableResolver;
import com.zving.framework.utility.DateUtil;
import java.text.DecimalFormat;
import java.util.Date;

public class Format extends AbstractFunction
{
  public Object execute(IVariableResolver resolver, Object[] args)
  {
    Object value = args[0];
    String format = (String)args[1];
    if ((value instanceof Date)) {
      return DateUtil.toString((Date)value, format);
    }
    if (((value instanceof String)) && (DateUtil.isDateTime((String)value))) {
      return DateUtil.toString(DateUtil.parseDateTime((String)value), format);
    }
    if ((value instanceof Number)) {
      return new DecimalFormat(format).format((Number)value);
    }
    return value;
  }

  public Class<?>[] getArgumentTypes() {
    return new Class[] { Object.class, String.class };
  }

  public String getFunctionPrefix() {
    return "";
  }

  public String getFunctionName() {
    return "format";
  }
}
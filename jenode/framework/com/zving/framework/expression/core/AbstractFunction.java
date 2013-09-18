package com.zving.framework.expression.core;

public abstract class AbstractFunction
  implements IFunction
{
  public static final Class<?>[] Arg_Object = { Object.class };
  public static final Class<?>[] Arg_String = { String.class };
  public static final Class<?>[] Arg_String_String = { String.class, String.class };
  public static final Class<?>[] Arg_String_Int_Int = { String.class, Integer.class, Integer.class };
  public static final Class<?>[] Arg_String_Int = { String.class, Integer.class };

  public String getID() {
    String prefix = getFunctionPrefix();
    return (prefix == null ? "" : prefix) + ":" + getFunctionName();
  }

  public String getName() {
    return getID();
  }
}
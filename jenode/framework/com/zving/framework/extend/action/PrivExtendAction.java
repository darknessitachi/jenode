package com.zving.framework.extend.action;

import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;

public abstract class PrivExtendAction
  implements IExtendAction
{
  public static String ExtendPointID = "com.zving.framework.PrivCheck";

  public Object execute(Object[] args) throws ExtendException {
    return Integer.valueOf(getPrivFlag((String)args[0]));
  }

  public abstract int getPrivFlag(String paramString)
    throws ExtendException;
}
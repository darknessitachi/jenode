package com.zving.framework.extend.action;

import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;

public abstract class BeforeUIMethodInvokeAction
  implements IExtendAction
{
  public static final String ExtendPointID = "com.zving.framework.BeforeUIMethodInvoke";

  public Object execute(Object[] args)
    throws ExtendException
  {
    execute((String)args[0]);
    return null;
  }

  public abstract void execute(String paramString)
    throws ExtendException;
}
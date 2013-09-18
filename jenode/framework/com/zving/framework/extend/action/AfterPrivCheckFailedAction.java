package com.zving.framework.extend.action;

import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;

public abstract class AfterPrivCheckFailedAction
  implements IExtendAction
{
  public static final String ID = "com.zving.framework.AfterPrivCheckFailedAction";

  public Object execute(Object[] args)
    throws ExtendException
  {
    String message = (String)args[0];
    execute(message);
    return null;
  }

  public abstract void execute(String paramString);
}
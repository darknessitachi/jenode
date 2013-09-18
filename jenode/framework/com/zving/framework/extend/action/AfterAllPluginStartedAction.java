package com.zving.framework.extend.action;

import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;

public abstract class AfterAllPluginStartedAction
  implements IExtendAction
{
  public static final String ExtendPointID = "com.zving.framework.AfterAllPluginStarted";

  public Object execute(Object[] args)
    throws ExtendException
  {
    execute();
    return null;
  }

  public abstract void execute();
}
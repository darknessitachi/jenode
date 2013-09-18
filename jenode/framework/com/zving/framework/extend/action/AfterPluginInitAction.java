package com.zving.framework.extend.action;

import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;

public abstract class AfterPluginInitAction
  implements IExtendAction
{
  public static final String ExtendPointID = "com.zving.framework.AfterPluginInitAction";

  public Object execute(Object[] args)
    throws ExtendException
  {
    execute();
    return null;
  }

  public abstract void execute();
}
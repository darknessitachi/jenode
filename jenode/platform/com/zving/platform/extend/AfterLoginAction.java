package com.zving.platform.extend;

import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;

public abstract class AfterLoginAction
  implements IExtendAction
{
  public static final String ID = "com.zving.platform.AfterLogin";

  public Object execute(Object[] args)
    throws ExtendException
  {
    return execute();
  }

  public abstract Object execute();
}
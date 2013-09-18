package com.zving.framework.extend.action;

import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;

public abstract class AfterSQLExecutedAction
  implements IExtendAction
{
  public static final String ID = "com.zving.framework.AfterSQLExecutedAction";

  public Object execute(Object[] args)
    throws ExtendException
  {
    long costTime = ((Long)args[0]).longValue();
    String message = (String)args[1];
    execute(costTime, message);
    return null;
  }

  public abstract void execute(long paramLong, String paramString);
}
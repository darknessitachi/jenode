package com.zving.framework.extend.action;

import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;

public abstract class AfterCronTaskExecutedAction
  implements IExtendAction
{
  public static final String ID = "com.zving.framework.AfterCronTaskExecutedAction";

  public Object execute(Object[] args)
    throws ExtendException
  {
    String taskManagerID = (String)args[0];
    String taskID = (String)args[1];
    execute(taskManagerID, taskID);
    return null;
  }

  public abstract void execute(String paramString1, String paramString2);
}
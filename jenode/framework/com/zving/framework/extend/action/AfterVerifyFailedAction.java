package com.zving.framework.extend.action;

import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;

public abstract class AfterVerifyFailedAction
  implements IExtendAction
{
  public static final String ID = "com.zving.framework.AfterPrivCheckFailedAction";

  public Object execute(Object[] args)
    throws ExtendException
  {
    String methodName = (String)args[0];
    String k = (String)args[1];
    String v = (String)args[2];
    String rule = (String)args[3];
    execute(methodName, k, v, rule);
    return null;
  }

  public abstract void execute(String paramString1, String paramString2, String paramString3, String paramString4);
}
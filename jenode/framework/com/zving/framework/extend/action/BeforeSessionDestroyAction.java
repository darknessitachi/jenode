package com.zving.framework.extend.action;

import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;
import javax.servlet.http.HttpSession;

public abstract class BeforeSessionDestroyAction
  implements IExtendAction
{
  public static final String ExtendPointID = "com.zving.framework.BeforeSessionDestory";

  public Object execute(Object[] args)
    throws ExtendException
  {
    HttpSession session = (HttpSession)args[0];
    execute(session);
    return null;
  }

  public abstract void execute(HttpSession paramHttpSession)
    throws ExtendException;
}
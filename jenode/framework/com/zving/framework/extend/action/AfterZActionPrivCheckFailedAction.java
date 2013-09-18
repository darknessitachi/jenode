package com.zving.framework.extend.action;

import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AfterZActionPrivCheckFailedAction
  implements IExtendAction
{
  public static final String ID = "com.zving.framework.AfterZActionPrivCheckFailedAction";

  public Object execute(Object[] args)
    throws ExtendException
  {
    HttpServletRequest request = (HttpServletRequest)args[0];
    HttpServletResponse response = (HttpServletResponse)args[1];
    Method m = (Method)args[2];
    execute(m, request, response);
    return null;
  }

  public abstract void execute(Method paramMethod, HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse);
}
package com.zving.framework.extend.action;

import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AfterMainFilterAction
  implements IExtendAction
{
  public static final String ExtendPointID = "com.zving.framework.AfterMainFilter";

  public Object execute(Object[] args)
    throws ExtendException
  {
    HttpServletRequest request = (HttpServletRequest)args[0];
    HttpServletResponse response = (HttpServletResponse)args[1];
    FilterChain chain = (FilterChain)args[2];
    execute(request, response, chain);
    return null;
  }

  public abstract void execute(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, FilterChain paramFilterChain)
    throws ExtendException;
}
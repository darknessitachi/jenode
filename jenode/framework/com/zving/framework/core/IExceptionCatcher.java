package com.zving.framework.core;

import com.zving.framework.extend.IExtendItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract interface IExceptionCatcher extends IExtendItem
{
  public abstract Class<?>[] getTargetExceptionClass();

  public abstract void doCatch(RuntimeException paramRuntimeException, HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse);
}
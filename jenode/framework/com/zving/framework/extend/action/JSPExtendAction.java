package com.zving.framework.extend.action;

import com.zving.framework.extend.ExtendException;

@Deprecated
public abstract class JSPExtendAction extends ZhtmlExtendAction
{
  public void execute(ZhtmlContext context)
    throws ExtendException
  {
    execute(new JSPContext(context));
  }

  public abstract void execute(JSPContext paramJSPContext)
    throws ExtendException;
}
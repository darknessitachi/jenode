package com.zving.framework.extend.action;

import com.zving.framework.RequestData;

@Deprecated
public class JSPContext extends ZhtmlContext
{
  public JSPContext(RequestData request)
  {
    super(request);
  }

  public JSPContext(ZhtmlContext c) {
    super(c.request);
  }
}
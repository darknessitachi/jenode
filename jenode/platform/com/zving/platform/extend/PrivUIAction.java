package com.zving.platform.extend;

import com.zving.framework.RequestData;
import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.action.ZhtmlContext;
import com.zving.framework.extend.action.ZhtmlExtendAction;

public class PrivUIAction extends ZhtmlExtendAction
{
  public void execute(ZhtmlContext context)
    throws ExtendException
  {
    String Type = context.getRequest().getString("Type");
    String UserName = context.getRequest().getString("UserName");
    String RoleCode = context.getRequest().getString("RoleCode");
    if ("Role".equals(Type))
      context.include("platform/menuPrivExtend.zhtml?Type=Role&RoleCode=" + RoleCode);
    else
      context.include("platform/menuPrivExtend.zhtml?Type=User&UserName=" + UserName);
  }

  public boolean isUsable()
  {
    return true;
  }
}
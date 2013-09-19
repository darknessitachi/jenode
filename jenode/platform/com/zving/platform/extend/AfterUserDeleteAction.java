package com.zving.platform.extend;

import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;

public class AfterUserDeleteAction
  implements IExtendAction
{
  public static final String ExtendPointID = "com.zving.Platform.AfterRoleModify";

  public Object execute(Object[] args)
    throws ExtendException
  {
    return null;
  }

  public boolean isUsable() {
    return true;
  }
}
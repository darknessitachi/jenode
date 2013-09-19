package com.zving.platform.extend;

import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;

public class AfterUserAddAction
  implements IExtendAction
{
  public Object execute(Object[] args)
    throws ExtendException
  {
    return null;
  }

  public boolean isUsable() {
    return true;
  }
}
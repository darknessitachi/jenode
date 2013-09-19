package com.zving.platform.extend;

import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;

public abstract class AfterBranchDeleteAction
  implements IExtendAction
{
  public static final String ExtendPointID = "com.zving.Platform.AfterBranchDelete";

  public Object execute(Object[] args)
    throws ExtendException
  {
    String[] ids = (String[])args[0];
    execute(ids);
    return null;
  }

  public abstract void execute(String[] paramArrayOfString);
}
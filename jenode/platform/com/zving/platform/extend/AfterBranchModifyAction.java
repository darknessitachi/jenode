package com.zving.platform.extend;

import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;
import com.zving.schema.ZDBranch;

public abstract class AfterBranchModifyAction
  implements IExtendAction
{
  public static final String ExtendPointID = "com.zving.Platform.AfterBranchModify";

  public Object execute(Object[] args)
    throws ExtendException
  {
    ZDBranch branch = (ZDBranch)args[0];
    execute(branch);
    return null;
  }

  public abstract void execute(ZDBranch paramZDBranch)
    throws ExtendException;
}
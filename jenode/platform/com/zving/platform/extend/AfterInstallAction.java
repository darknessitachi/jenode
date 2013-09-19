package com.zving.platform.extend;

import com.zving.framework.data.DBConn;
import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;

public abstract class AfterInstallAction
  implements IExtendAction
{
  public static final String ExtendPointID = "com.zving.platform.AfterInstall";

  public Object execute(Object[] args)
    throws ExtendException
  {
    DBConn conn = (DBConn)args[0];
    execute(conn);
    return null;
  }

  public abstract void execute(DBConn paramDBConn)
    throws ExtendException;
}
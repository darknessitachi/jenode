package com.zving.platform.extend;

import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;
import com.zving.framework.orm.DAOSet;
import com.zving.schema.ZDMetaModel;

public abstract class BeforeMetaModelDelete
  implements IExtendAction
{
  public static final String ExtendPointID = "com.zving.platform.BeforeMetaModelDelete";

  public Object execute(Object[] args)
    throws ExtendException
  {
    DAOSet set = (DAOSet)args[0];
    return execute(set);
  }

  public abstract Object execute(DAOSet<ZDMetaModel> paramDAOSet);
}
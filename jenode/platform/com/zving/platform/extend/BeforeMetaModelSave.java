package com.zving.platform.extend;

import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;
import com.zving.schema.ZDMetaModel;

public abstract class BeforeMetaModelSave
  implements IExtendAction
{
  public static final String ExtendPointID = "com.zving.platform.BeforeMetaModelSave";

  public Object execute(Object[] args)
    throws ExtendException
  {
    ZDMetaModel model = (ZDMetaModel)args[0];
    String oldModelCode = args[1].toString();
    return execute(model, oldModelCode);
  }

  public abstract Object execute(ZDMetaModel paramZDMetaModel, String paramString);
}
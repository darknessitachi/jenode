package com.zving.platform.extend;

import com.zving.framework.UIFacade;
import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;

public abstract class AfterSSOLogin
  implements IExtendAction
{
  public static final String ExtendPointID = "com.zving.platform.AfterSSOLogin";

  public Object execute(Object[] args)
    throws ExtendException
  {
    UIFacade ui = (UIFacade)args[0];
    execute(ui);
    return null;
  }

  public abstract void execute(UIFacade paramUIFacade);
}
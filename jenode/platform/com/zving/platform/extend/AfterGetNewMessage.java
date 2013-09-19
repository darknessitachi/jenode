package com.zving.platform.extend;

import com.zving.framework.UIFacade;
import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.IExtendAction;

public abstract class AfterGetNewMessage
  implements IExtendAction
{
  public static final String ID = "com.zving.platform.AfterGetNewMessage";

  public Object execute(Object[] args)
    throws ExtendException
  {
    UIFacade ui = (UIFacade)args[0];
    execute(ui);
    return null;
  }

  public abstract void execute(UIFacade paramUIFacade);

  public boolean isUsable() {
    return true;
  }
}
package com.zving.platform.extend;

import com.zving.framework.User;
import com.zving.framework.extend.ExtendException;
import com.zving.framework.extend.action.PrivExtendAction;
import com.zving.framework.security.Privilege;
import com.zving.platform.config.AdminUserName;

public class MenuPrivCheck extends PrivExtendAction
{
  public int getPrivFlag(String priv)
    throws ExtendException
  {
    if (AdminUserName.getValue().equals(User.getUserName())) {
      return 1;
    }
    if (User.getPrivilege().hasPriv(priv)) {
      return 1;
    }
    return 0;
  }

  public boolean isUsable() {
    return true;
  }
}
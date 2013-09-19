package com.zving.platform.extend;

import com.zving.framework.Member;
import com.zving.framework.User;
import com.zving.framework.extend.action.AfterPrivCheckFailedAction;
import com.zving.framework.utility.StringFormat;
import com.zving.platform.bl.LogBL;

public class AfterPrivCheckFailed extends AfterPrivCheckFailedAction
{
  public void execute(String message)
  {
    String str = "Login=?,LoginType=?,UserType=?,Message=?";
    String loginType = "-";
    if (User.isLogin())
      loginType = "User";
    else if (Member.isLogin())
      loginType = "Member";
    else {
      return;
    }
    message = StringFormat.format(str, new Object[] { Boolean.valueOf(User.isLogin()), loginType, User.getType(), message });
    LogBL.addLog("SecurityLog", "PrivCheck", message);
  }
  public boolean isUsable() {
    return true;
  }
}
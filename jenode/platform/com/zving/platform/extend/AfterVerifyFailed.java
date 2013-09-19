package com.zving.platform.extend;

import com.zving.framework.extend.action.AfterVerifyFailedAction;
import com.zving.framework.utility.StringFormat;
import com.zving.platform.bl.LogBL;

public class AfterVerifyFailed extends AfterVerifyFailedAction
{
  public void execute(String methodName, String k, String v, String rule)
  {
    String str = "Method=?,Key=?,Value=?,Rule=?";
    String message = StringFormat.format(str, new Object[] { methodName, k, v, rule });
    LogBL.addLog("SecurityLog", "Verify", message);
  }
  public boolean isUsable() {
    return true;
  }
}
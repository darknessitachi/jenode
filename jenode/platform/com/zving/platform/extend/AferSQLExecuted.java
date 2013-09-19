package com.zving.platform.extend;

import com.zving.framework.extend.action.AfterSQLExecutedAction;
import com.zving.platform.bl.LogBL;
import com.zving.platform.config.SlowSQLThreshold;

public class AferSQLExecuted extends AfterSQLExecutedAction
{
  public void execute(long costTime, String message)
  {
    if ((costTime < SlowSQLThreshold.getValue()) && (!message.startsWith("Error:")) && (!message.startsWith("Timeout:"))) {
      return;
    }
    String subType = null;
    if (costTime < SlowSQLThreshold.getValue())
      subType = "Slow";
    else if (message.startsWith("Error:"))
      subType = "Error";
    else {
      subType = "Timeout";
    }
    message = "CostTime=" + costTime + ",Message=" + message;
    LogBL.addLog("SQLLog", subType, message);
  }

  public boolean isUsable() {
    return true;
  }
}
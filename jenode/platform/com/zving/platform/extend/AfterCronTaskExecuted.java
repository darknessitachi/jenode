package com.zving.platform.extend;

import com.zving.framework.data.Q;
import com.zving.framework.extend.action.AfterCronTaskExecutedAction;
import com.zving.framework.utility.DateUtil;
import java.util.Date;

public class AfterCronTaskExecuted extends AfterCronTaskExecutedAction
{
  private static long lastTime = 0L;

  public void execute(String taskManagerID, String taskID)
  {
    if (System.currentTimeMillis() - lastTime > 10800000L) {
      lastTime = System.currentTimeMillis();
      new Thread() {
        public void run() {
          Date d = DateUtil.addDay(new Date(), -7);
          Q qb = new Q("delete from ZDUserLog where LogType=? and AddTime<?", new Object[] { "CronLog", d });
          qb.executeNoQuery();
        }
      }
      .start();
    }
  }

  public boolean isUsable() { return true; }

}
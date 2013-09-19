package com.zving.platform.bl;

import com.zving.framework.Config;
import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.User;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.utility.DateUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.ILogType;
import com.zving.platform.pub.NoUtil;
import com.zving.platform.service.LogTypeService;
import com.zving.schema.ZDUserLog;
import java.util.Date;

public class LogBL
{
  private static DAOSet<ZDUserLog> set = new DAOSet();
  private static long lastTime = 0L;
  private static Object mutex = new Object();

  public static void addLog(String type, String subType, String message)
  {
    if (!Config.isInstalled) {
      return;
    }
    ZDUserLog log = new ZDUserLog();
    log.setAddTime(new Date(System.currentTimeMillis()));
    log.setUserName(User.getUserName() == null ? "-" : User.getUserName());
    log.setIP(Current.getRequest() == null ? "-" : Current.getRequest().getClientIP());
    log.setLogMessage(message);
    log.setLogType(type);
    log.setSubType(subType);
    synchronized (mutex) {
      set.add(log);
      if ((set.size() > 100) || (System.currentTimeMillis() - lastTime > 180000L)) {
        lastTime = System.currentTimeMillis();

        set = new DAOSet();
        new Thread()
        {
          public void run()
          {
          }
        }

        .start();
      }
    }
  }

  public static void addLog(String userName, String type, String subType, String message)
  {
    ZDUserLog log = new ZDUserLog();
    log.setLogID(NoUtil.getMaxID("ZDUserLogID"));
    log.setAddTime(new Date());
    log.setUserName(userName == null ? User.getUserName() : userName);
    log.setIP(Current.getRequest() == null ? "-" : Current.getRequest().getClientIP());
    log.setLogMessage(message);
    log.setLogType(type);
    log.setSubType(subType);
    log.insert();
  }

  public static void bindGridAction(DataGridAction dga, String logType)
  {
    Q qb = new Q("Select * from ZDUserLog where LogType=?", new Object[] { logType });
    if (ObjectUtil.notEmpty(dga.getParam("SubType"))) {
      qb.append(" and SubType=?", new Object[] { dga.getParam("SubType") });
    }
    if (ObjectUtil.notEmpty(dga.getParam("UserName"))) {
      qb.append(" and UserName like ?", new Object[] { "%" + dga.getParam("UserName") + "%" });
    }
    if (ObjectUtil.notEmpty(dga.getParam("IP"))) {
      qb.append(" and IP like ?", new Object[] { "%" + dga.getParam("IP") + "%" });
    }
    if (ObjectUtil.notEmpty(dga.getParam("Message"))) {
      qb.append(" and LogMessage like ?", new Object[] { "%" + dga.getParam("Message") + "%" });
    }
    String startDate = dga.getParams().getString("StartDate");
    String endDate = dga.getParams().getString("EndDate");

    if ((StringUtil.isNotEmpty(startDate)) && (StringUtil.isNotEmpty(endDate))) {
      qb.append(" and AddTime >=?", new Object[] { DateUtil.parse(startDate, "yyyy-MM-dd") });
      qb.append(" and AddTime<=?", new Object[] { DateUtil.parse(endDate, "yyyy-MM-dd") });
    }

    ILogType type = (ILogType)LogTypeService.getInstance().get(logType);
    DataTable dt = qb.fetch(dga.getPageSize(), dga.getPageIndex());
    type.decodeMessage(dt);
    dga.setTotal(qb);
    dga.bindData(dt);
  }
}
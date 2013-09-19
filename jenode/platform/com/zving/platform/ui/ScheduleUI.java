package com.zving.platform.ui;

import com.zving.framework.RequestData;
import com.zving.framework.ResponseData;
import com.zving.framework.UIFacade;
import com.zving.framework.User;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataColumn;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.data.Transaction;
import com.zving.framework.i18n.Lang;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.schedule.AbstractTaskManager;
import com.zving.framework.schedule.CronManager;
import com.zving.framework.schedule.CronMonitor;
import com.zving.framework.schedule.CronTaskManagerService;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.utility.DateUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.pub.NoUtil;
import com.zving.platform.pub.PlatformUtil;
import com.zving.schema.ZDSchedule;
import java.util.Calendar;
import java.util.Date;

@Alias("Schedule")
public class ScheduleUI extends UIFacade
{
  @Priv("Platform.Schedule")
  public void init()
  {
    this.Response.put("IsUsing", "Y");
    if (ObjectUtil.empty($V("ID"))) {
      return;
    }
    ZDSchedule plan = new ZDSchedule();
    plan.setID($V("ID"));
    if (plan.fill()) {
      this.Response.putAll(plan.toMapx());
      if (ObjectUtil.notEmpty(plan.getStartTime())) {
        this.Response.put("StartTime", DateUtil.toDateTimeString(plan.getStartTime()));
      }
      if ("Period".equals(plan.getPlanType()))
        if (plan.getCronExpression().endsWith(" * * * *"))
          $S("PeriodType", "Minute");
        else if (plan.getCronExpression().endsWith(" * * *"))
          $S("PeriodType", "Hour");
        else if (plan.getCronExpression().endsWith(" * *"))
          $S("PeriodType", "Day");
        else if (plan.getCronExpression().endsWith(" *"))
          $S("PeriodType", "Month");
    }
  }

  @Priv("Platform.Schedule")
  public DataTable getTypes()
  {
    Mapx params = CronManager.getInstance().getTaskTypes();
    return params.toDataTable();
  }

  @Priv("Platform.Schedule")
  public DataTable getUsableTask() {
    String type = $V("TypeCode");
    if (ObjectUtil.empty(type)) {
      return new DataTable();
    }
    Mapx map = CronManager.getInstance().getConfigEnableTasks(type);
    return map.toDataTable();
  }

  @Priv("Platform.Schedule")
  public void dg1DataBind(DataGridAction dga) {
    String sql = "select * from ZDSchedule " + dga.getSortString();
    if (ObjectUtil.empty(dga.getSortString())) {
      sql = sql + "order by id desc";
    }
    DataTable dt = new Q(sql, new Object[0]).fetch();
    dt.decodeColumn("IsUsing", PlatformUtil.getCodeMap("Enable"));

    Mapx map = CronManager.getInstance().getTaskTypes();
    dt.decodeColumn("TypeCode", map);

    dt.insertColumn("SourceIDName");
    dt.insertColumn(new DataColumn("NextRunTime", 12));
    for (int i = dt.getRowCount() - 1; i >= 0; i--) {
      String typeCode = dt.getString(i, "TypeCode");
      String sourceID = dt.getString(i, "SourceID");

      Mapx taskMap = CronManager.getInstance().getConfigEnableTasks(typeCode);
      if (taskMap == null) {
        dt.deleteRow(i);
      }
      else {
        String sourceIDName = taskMap.getString(sourceID);
        if (StringUtil.isEmpty(sourceIDName)) {
          dt.deleteRow(i);
        }
        else {
          dt.set(i, "SourceIDName", sourceIDName);
          Date nextRunTime = null;
          try {
            Date st = dt.getDate(i, "StartTime");
            Date now = new Date();
            if (!now.before(st)) {
              st = now;
              nextRunTime = CronMonitor.getNextRunTime(st, dt.getString(i, "CronExpression"));
            } else {
              nextRunTime = st;
            }
          } catch (Exception e) {
            nextRunTime = DateUtil.parse("2999-01-01");
          }
          dt.set(i, "NextRunTime", nextRunTime);
        }
      }
    }
    dga.setTotal(dt.getRowCount());
    DataTable result = new DataTable(dt.getDataColumns(), null);
    for (int i = dga.getPageIndex() * dga.getPageSize(); (i < (dga.getPageIndex() + 1) * dga.getPageSize()) && (i < dt.getRowCount()); i++) {
      result.insertRow(dt.getDataRow(i));
    }
    dga.bindData(result);
  }

  @Priv("Platform.Schedule.Add||Platform.Schedule.Edit")
  public void save()
  {
    ZDSchedule schedule = new ZDSchedule();
    String id = $V("ID");
    if (StringUtil.isEmpty(id)) {
      schedule.setID(NoUtil.getMaxID("ScheduleID"));
      schedule.setAddTime(new Date());
      schedule.setAddUser(User.getUserName());
    } else {
      schedule.setID(Long.parseLong(id));
      schedule.fill();
      schedule.setModifyTime(new Date());
      schedule.setModifyUser(User.getUserName());
    }

    String startTime = $V("StartTime");
    if ($V("PlanType").equals("Period")) {
      this.Request.put("CronExpression", 
        getCronExpression($V("PeriodType"), Integer.valueOf($V("Period")).intValue(), DateUtil.parseDateTime(startTime)));
    }
    schedule.setValue(this.Request);
    try {
      boolean flag = StringUtil.isEmpty(id) ? schedule.insert() : schedule.update();
      if (!flag)
        fail(Lang.get("Common.ExecuteFailed"));
      else
        success(Lang.get("Common.ExecuteSuccess"));
    }
    catch (Exception e) {
      e.printStackTrace();
      fail(Lang.get("Common.SaveFailed") + "\n" + Lang.get("Cron.InvalidExpr"));
    }
  }

  @Priv("Platform.Schedule.Delete")
  public void del()
  {
    String ids = $V("IDs");
    if (!StringUtil.checkID(ids)) {
      fail("Invalid data!");
      return;
    }
    DAOSet set = new ZDSchedule().query(new Q("where id in (" + ids + ")", new Object[0]));
    Transaction trans = new Transaction();
    trans.add(set, 5);
    if (!trans.commit())
      fail(Lang.get("Common.DeleteFailed"));
    else
      success(Lang.get("Common.DeleteSuccess"));
  }

  @Priv("Platform.Schedule.ManualExecute")
  public void execute()
  {
    long id = Long.parseLong($V("ID"));
    ZDSchedule s = new ZDSchedule();
    s.setID(id);
    if (!s.fill()) {
      fail(Lang.get("Cron.TaskNotFound"));
      return;
    }
    AbstractTaskManager ctm = (AbstractTaskManager)CronTaskManagerService.getInstance().get(s.getTypeCode());
    if (ctm.isRunning(s.getSourceID())) {
      success("<font color=red>" + Lang.get("Cron.RunningNow") + "</font>");
    } else {
      ctm.execute(s.getSourceID());
      success(Lang.get("Cron.ManualSuccess"));
    }
  }

  public static String getCronExpression(String periodType, int period, Date startTime) {
    Calendar c = Calendar.getInstance();
    c.setTime(startTime);
    StringBuffer sb = new StringBuffer();
    if ("Minute".equals(periodType)) {
      int second = c.get(13);
      sb.append(second).append(" ");
      int minute = c.get(12);
      sb.append(minute);
      sb.append("-");
      if (minute == 0)
        sb.append("59");
      else {
        sb.append(minute - 1);
      }
      sb.append("/");
      sb.append(period);
      sb.append(" * * * *");
    } else if ("Hour".equals(periodType)) {
      int second = c.get(13);
      sb.append(second).append(" ");
      int minute = c.get(12);
      int hour = c.get(11);
      sb.append(minute);
      sb.append(" ");
      sb.append(hour);
      sb.append("-");
      if (hour == 0)
        sb.append("23");
      else {
        sb.append(hour - 1);
      }
      sb.append("/");
      sb.append(period);
      sb.append(" * * *");
    } else if ("Day".equals(periodType)) {
      int second = c.get(13);
      int minute = c.get(12);
      int hour = c.get(11);
      int day = c.get(5);
      sb.append(second);
      sb.append(" ");
      sb.append(minute);
      sb.append(" ");
      sb.append(hour);
      sb.append(" ");
      sb.append(day);
      sb.append("-");
      if (day == 1)
        sb.append(c.getActualMaximum(5));
      else {
        sb.append(day - 1);
      }
      sb.append("/");
      sb.append(period);
      sb.append(" * *");
    } else if ("Month".equals(periodType)) {
      int second = c.get(13);
      int minute = c.get(12);
      int hour = c.get(11);
      int day = c.get(5);
      int month = c.get(2) + 1;
      sb.append(second);
      sb.append(" ");
      sb.append(minute);
      sb.append(" ");
      sb.append(hour);
      sb.append(" ");
      sb.append(day);
      sb.append(" ");
      if (month == 1) {
        sb.append(month + 1);
        sb.append("-");
        sb.append(month);
      } else {
        sb.append(month);
        sb.append("-");
        sb.append(month - 1);
      }
      sb.append("/");
      sb.append(period);
      sb.append(" *");
    }
    return sb.toString();
  }
}
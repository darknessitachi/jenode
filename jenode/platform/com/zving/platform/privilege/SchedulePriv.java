package com.zving.platform.privilege;

public class SchedulePriv extends AbstractMenuPriv
{
  public static final String MenuID = "Platform.Schedule";
  public static final String Add = "Platform.Schedule.Add";
  public static final String Edit = "Platform.Schedule.Edit";
  public static final String Delete = "Platform.Schedule.Delete";
  public static final String ManualExecute = "Platform.Schedule.ManualExecute";

  public SchedulePriv()
  {
    super("Platform.Schedule", null);
    addItem("Platform.Schedule.Add", "@{Common.Add}");
    addItem("Platform.Schedule.Edit", "@{Common.Edit}");
    addItem("Platform.Schedule.Delete", "@{Common.Delete}");
    addItem("Platform.Schedule.ManualExecute", "@{Platform.Schedule.ManualExecute}");
  }
}
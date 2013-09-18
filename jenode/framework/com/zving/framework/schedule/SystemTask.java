package com.zving.framework.schedule;

import com.zving.framework.utility.StringUtil;

public abstract class SystemTask extends AbstractTask
{
  protected boolean isRunning = false;
  protected String cronExpression;
  protected boolean disabled;

  public String getType()
  {
    return "General";
  }

  public abstract void execute();

  public boolean isRunning()
  {
    return this.isRunning;
  }

  public void setRunning(boolean isRunning) {
    this.isRunning = isRunning;
  }

  public String getCronExpression() {
    if (StringUtil.isEmpty(this.cronExpression)) {
      this.cronExpression = getDefaultCronExpression();
    }
    return this.cronExpression;
  }

  public String setCronExpression(String expr) {
    return this.cronExpression = expr;
  }

  public boolean isDisabled() {
    return this.disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }
}
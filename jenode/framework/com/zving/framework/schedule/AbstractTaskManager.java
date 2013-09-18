package com.zving.framework.schedule;

import com.zving.framework.collection.Mapx;
import com.zving.framework.extend.IExtendItem;

public abstract class AbstractTaskManager
  implements IExtendItem
{
  public abstract Mapx<String, String> getUsableTasks();

  public abstract Mapx<String, String> getConfigEnableTasks();

  public abstract String getTaskCronExpression(String paramString);

  public abstract void execute(String paramString);

  public abstract boolean isRunning(String paramString);

  public boolean enable4Front()
  {
    return false;
  }
}
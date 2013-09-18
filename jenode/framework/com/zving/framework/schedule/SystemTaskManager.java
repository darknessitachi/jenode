package com.zving.framework.schedule;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import java.util.List;

public class SystemTaskManager extends AbstractTaskManager
{
  public static final String ID = "SYSTEM";

  public synchronized void execute(final String id)
  {
    new Thread() {
      public void run() {
        SystemTask gt = (SystemTask)SystemTaskService.getInstance().get(id);
        if (gt != null) {
          if (!gt.isRunning()) {
            gt.setRunning(true);
            try {
              gt.execute();
            } finally {
              gt.setRunning(false);
            }
          }
        }
        else throw new RuntimeException("Task not found:" + id);
      }
    }
    .start();
  }

  public boolean isRunning(String id) {
    SystemTask gt = (SystemTask)SystemTaskService.getInstance().get(id);
    if (gt != null) {
      return gt.isRunning();
    }
    throw new RuntimeException("Task not found:" + id);
  }

  public String getID()
  {
    return "SYSTEM";
  }

  public String getName() {
    return "System Task";
  }

  public SystemTask getTask(String id) {
    return (SystemTask)SystemTaskService.getInstance().get(id);
  }

  public List<SystemTask> getAllTask() {
    return SystemTaskService.getInstance().getAll();
  }

  public Mapx<String, String> getUsableTasks() {
    Mapx map = new Mapx();
    for (SystemTask gt : SystemTaskService.getInstance().getAll()) {
      if ((!Config.isFrontDeploy()) || (gt.enable4Front()))
      {
        if (!gt.isDisabled())
          map.put(gt.getID(), gt.getName());
      }
    }
    return map;
  }

  public String getTaskCronExpression(String id) {
    SystemTask gt = (SystemTask)SystemTaskService.getInstance().get(id);
    return gt.getCronExpression();
  }

  public Mapx<String, String> getConfigEnableTasks() {
    return null;
  }

  public boolean enable4Front() {
    return true;
  }
}
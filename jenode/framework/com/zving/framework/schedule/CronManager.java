package com.zving.framework.schedule;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.utility.LogUtil;
import java.util.Timer;

public class CronManager
{
  private Timer mTimer;
  private CronMonitor mMonitor;
  private static CronManager instance;
  public static final long SCAN_INTERVAL = 1000L;
  private static Object mutex = new Object();

  public static synchronized CronManager getInstance() {
    if (instance == null) {
      synchronized (mutex) {
        if (instance == null) {
          instance = new CronManager();
        }
      }
    }
    return instance;
  }

  private CronManager() {
    init();
  }

  public void init() {
    if (!Config.isInstalled) {
      return;
    }
    this.mTimer = new Timer("Cron Manager Timer", true);
    this.mMonitor = new CronMonitor();
    this.mTimer.schedule(this.mMonitor, System.currentTimeMillis() % 1000L, 1000L);
    LogUtil.info("----" + Config.getAppCode() + "(" + LangUtil.get(Config.getAppName()) + "): CronManager Initialized----");
  }

  public Mapx<String, String> getTaskTypes()
  {
    Mapx rmap = new Mapx();
    for (AbstractTaskManager ctm : CronTaskManagerService.getInstance().getAll())
      if (!(ctm instanceof SystemTaskManager))
      {
        rmap.put(ctm.getID(), ctm.getName());
      }
    return rmap;
  }

  public Mapx<String, String> getConfigEnableTasks(String id)
  {
    AbstractTaskManager ctm = (AbstractTaskManager)CronTaskManagerService.getInstance().get(id);
    if (ctm == null) {
      return null;
    }
    return ctm.getConfigEnableTasks();
  }

  protected AbstractTaskManager getCronTaskManager(String id) {
    return (AbstractTaskManager)CronTaskManagerService.getInstance().get(id);
  }

  public void destory() {
    if (this.mMonitor != null) {
      this.mMonitor.destory();
      this.mTimer.cancel();
      this.mTimer = null;
    }
  }
}
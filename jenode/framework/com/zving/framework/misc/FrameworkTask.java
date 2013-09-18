package com.zving.framework.misc;

import com.zving.framework.Config;
import com.zving.framework.schedule.SystemTask;
import com.zving.framework.utility.FileUtil;
import java.io.File;

public class FrameworkTask extends SystemTask
{
  public void execute()
  {
    if (!Config.isDebugMode()) {
      return;
    }
    File dir = new File(Config.getContextRealPath() + "WEB-INF/cache/");
    File[] fs = dir.listFiles();
    for (int i = 0; i < fs.length; i++) {
      File f = fs[i];
      if (f.isFile())
        FileUtil.delete(f);
    }
  }

  public String getID()
  {
    return "com.zving.framwork.FrameworkTask";
  }

  public String getName() {
    return "Framework Cron Task";
  }

  public String getDefaultCronExpression()
  {
    return "30 10,16 * * *";
  }

  public boolean enable4Front() {
    return true;
  }
}
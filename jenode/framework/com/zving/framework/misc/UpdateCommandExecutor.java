package com.zving.framework.misc;

import com.zving.framework.Config;
import com.zving.framework.utility.FileUtil;
import com.zving.preloader.Updater.UpdateCommand;
import com.zving.preloader.Updater.UpdateCommandList;
import java.io.File;

public class UpdateCommandExecutor
{
  public static void execute()
  {
    String path = Config.getPluginPath() + "update/current/commands.txt";
    if (new File(path).exists()) {
      return;
    }
    StringBuilder sb = new StringBuilder();
    try {
      Updater.UpdateCommandList list = new Updater.UpdateCommandList();
      list.parse(FileUtil.readText(path, "UTF-8"));
      for (Updater.UpdateCommand uc : list.getAll()) {
        uc.Type.equals("#[SQL]");
      }
    }
    finally
    {
      if (sb.length() != 0)
        FileUtil.writeText(Config.getPluginPath() + "update/UpdaterCommandExecutor_" + System.currentTimeMillis() + ".log", 
          sb.toString());
    }
  }
}
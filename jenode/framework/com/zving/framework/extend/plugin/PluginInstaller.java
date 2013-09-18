package com.zving.framework.extend.plugin;

import org.apache.commons.fileupload.FileItem;

public class PluginInstaller
{
  public static void verify(FileItem file)
  {
  }

  public static int install(FileItem file)
  {
    return 0;
  }

  public static int uninstall(String pluginID) {
    return 0;
  }
}
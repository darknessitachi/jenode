package com.zving.framework.extend.plugin;

import com.zving.framework.i18n.LangMapping;

public class FrameworkPlugin extends AbstractPlugin
{
  public static final String ID = "com.zving.framework";

  public void install()
    throws PluginException
  {
    throw new PluginException(LangMapping.get("Framework.Plugin.InstallFail"));
  }

  public void start() throws PluginException {
  }

  public void stop() throws PluginException {
    throw new PluginException(LangMapping.get("Framework.Plugin.StopFail"));
  }

  public void uninstall() throws PluginException {
    throw new PluginException(LangMapping.get("Framework.Plugin.UninstallFail"));
  }
}
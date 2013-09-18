package com.zving.framework.extend.plugin;

public abstract class AbstractPlugin
  implements IPlugin
{
  private PluginConfig config;

  public void install()
    throws PluginException
  {
  }

  public void uninstall()
    throws PluginException
  {
  }

  public PluginConfig getConfig()
  {
    return this.config;
  }

  public void setConfig(PluginConfig config) {
    this.config = config;
  }

  public void destory()
  {
  }
}
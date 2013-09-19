package com.zving.platform.privilege;

public class PluginPriv extends AbstractMenuPriv
{
  public static final String MenuID = "Platform.Plugin";
  public static final String Start = "Platform.Plugin.Start";
  public static final String Stop = "Platform.Plugin.Stop";

  public PluginPriv()
  {
    super("Platform.Plugin", null);
    addItem("Platform.Plugin.Start", "@{Common.Start}");
    addItem("Platform.Plugin.Stop", "@{Common.Stop}");
  }
}
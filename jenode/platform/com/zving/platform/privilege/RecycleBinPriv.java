package com.zving.platform.privilege;

public class RecycleBinPriv extends AbstractMenuPriv
{
  public static final String MenuID = "Platform.RecycleBin";
  public static final String DeleteReally = "Platform.RecycleBin.DeleteReally";
  public static final String Recovery = "Platform.RecycleBin.Recovery";

  public RecycleBinPriv()
  {
    super("Platform.RecycleBin", null);
    addItem("Platform.RecycleBin.DeleteReally", "彻底删除");
    addItem("Platform.RecycleBin.Recovery", "恢复");
  }
}
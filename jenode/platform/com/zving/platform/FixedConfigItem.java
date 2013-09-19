package com.zving.platform;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import com.zving.framework.extend.IExtendItem;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.StringUtil;

public class FixedConfigItem
  implements IExtendItem
{
  private String Code;
  private String DataType;
  private String ControlType;
  private String Memo;
  private String PluginID;
  private Mapx<String, String> Options = new Mapx();

  public FixedConfigItem(String code, String dataType, String controlType, String memo, String pluginID) {
    this.Code = code;
    this.DataType = dataType;
    this.ControlType = controlType;
    this.Memo = memo;
    this.PluginID = pluginID;
  }

  public void addOption(String key, String value) {
    this.Options.put(key, value);
  }

  public String getCode() {
    return this.Code;
  }

  public String getDataType() {
    return this.DataType;
  }

  public String getControlType() {
    return this.ControlType;
  }

  public Mapx<String, String> getOptions() {
    return this.Options;
  }

  public String getID()
  {
    return getCode();
  }

  public String getName() {
    return getMemo();
  }

  public String getMemo() {
    return this.Memo;
  }
  public String getPluginID() {
    return this.PluginID;
  }

  public void setPluginID(String pluginID) {
    this.PluginID = pluginID;
  }

  public static String replacePathHolder(String v)
  {
    String path = Config.getContextRealPath();
    if (path.endsWith("/")) {
      path = path.substring(0, path.length() - 1);
    }
    v = StringUtil.replaceEx(v, "${Parent}", path.substring(0, path.lastIndexOf("/")));
    v = StringUtil.replaceEx(v, "${Self}", path);
    v = FileUtil.normalizePath(v);
    return v;
  }
}
package com.zving.framework.template;

import com.zving.framework.collection.Mapx;

public class TagAttr
{
  String Name;
  boolean isMandatory = false;
  String Usage;
  int DataType = 1;
  Mapx<String, String> Options = new Mapx();

  public static Mapx<String, String> BOOL_OPTIONS = new Mapx();

  static { BOOL_OPTIONS.put("true", "true");
    BOOL_OPTIONS.put("false", "false");
  }

  public String getName()
  {
    return this.Name;
  }

  public boolean isMandatory()
  {
    return this.isMandatory;
  }

  public String getUsage()
  {
    return this.Usage;
  }

  public int getDataType()
  {
    return this.DataType;
  }

  public TagAttr(String name) {
    this.Name = name;
  }

  public TagAttr(String name, boolean mandatory) {
    this.Name = name;
    this.isMandatory = mandatory;
  }

  public TagAttr(String name, int dataType) {
    this.Name = name;
    this.DataType = dataType;
  }

  public TagAttr(String name, boolean mandatory, int dataType, String usage) {
    this.Name = name;
    this.isMandatory = mandatory;
    this.DataType = dataType;
    this.Usage = usage;
  }

  public TagAttr(String name, int dataType, String usage) {
    this.Name = name;
    this.DataType = dataType;
    this.Usage = usage;
  }

  public TagAttr(String name, Mapx<String, String> options, String usage) {
    this.Name = name;
    this.Options = options;
    this.Usage = usage;
  }

  public TagAttr(String name, Mapx<String, String> options) {
    this.Name = name;
    this.Options = options;
  }

  public Mapx<String, String> getOptions() {
    return this.Options;
  }

  public void setOptions(Mapx<String, String> options) {
    this.Options = options;
  }
}
package com.zving.platform.meta;

public class SystemModelTemplateType
  implements IMetaModelTemplateType
{
  public static final String ID = "System";

  public String getID()
  {
    return "System";
  }

  public String getName() {
    return "@{Platform.DefaultShowTemplate}";
  }
}
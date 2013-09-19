package com.zving.platform.service;

import com.zving.framework.collection.Mapx;
import com.zving.framework.extend.IExtendItem;

public abstract class AbstractUserPreferences
  implements IExtendItem
{
  public abstract boolean validate(String paramString);

  public String process(Mapx<String, Object> map)
  {
    return map.getString(getID());
  }

  public abstract String defaultValue();
}
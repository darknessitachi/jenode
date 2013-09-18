package com.zving.framework.json.convert;

import com.zving.framework.extend.IExtendItem;
import com.zving.framework.json.JSONObject;

public abstract interface IJSONConvertor extends IExtendItem
{
  public abstract String getTypeID();

  public abstract boolean match(Object paramObject);

  public abstract JSONObject toJSON(Object paramObject);

  public abstract Object fromJSON(JSONObject paramJSONObject);
}
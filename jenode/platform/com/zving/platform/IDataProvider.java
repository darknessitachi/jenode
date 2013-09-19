package com.zving.platform;

import com.zving.framework.collection.Mapx;
import com.zving.framework.extend.IExtendItem;

public abstract interface IDataProvider extends IExtendItem
{
  public abstract String getData(Mapx<String, Object> paramMapx);
}
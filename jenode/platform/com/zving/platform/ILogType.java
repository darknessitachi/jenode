package com.zving.platform;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataTable;
import com.zving.framework.extend.IExtendItem;

public abstract interface ILogType extends IExtendItem
{
  public abstract Mapx<String, String> getSubTypes();

  public abstract void decodeMessage(DataTable paramDataTable);
}
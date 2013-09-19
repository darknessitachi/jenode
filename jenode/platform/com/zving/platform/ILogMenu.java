package com.zving.platform;

import com.zving.framework.extend.IExtendItem;

public abstract interface ILogMenu extends IExtendItem
{
  public abstract String getDetailURL();

  public abstract String getGroupID();
}
package com.zving.framework.schedule;

import com.zving.framework.extend.IExtendItem;

public abstract class AbstractTask
  implements IExtendItem
{
  public abstract String getType();

  public abstract String getCronExpression();

  public abstract String getDefaultCronExpression();

  public boolean enable4Front()
  {
    return false;
  }
}
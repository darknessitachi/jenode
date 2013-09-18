package com.zving.framework.ui;

import com.zving.framework.data.QueryBuilder;

public abstract interface IPageEnableAction
{
  public abstract int getPageSize();

  public abstract int getPageIndex();

  public abstract int getTotal();

  public abstract void setPageSize(int paramInt);

  public abstract void setPageIndex(int paramInt);

  public abstract void setTotal(QueryBuilder paramQueryBuilder);

  public abstract void setTotal(int paramInt);
}
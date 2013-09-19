package com.zving.platform;

import com.zving.framework.data.DataTable;

public class RecycbinListData
{
  private DataTable dt;
  private int count;

  public DataTable getDt()
  {
    return this.dt;
  }
  public void setDt(DataTable dt) {
    this.dt = dt;
  }
  public int getCount() {
    return this.count;
  }
  public void setCount(int count) {
    this.count = count;
  }
}
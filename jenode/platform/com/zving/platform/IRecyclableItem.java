package com.zving.platform;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.Transaction;
import com.zving.framework.extend.IExtendItem;

public abstract interface IRecyclableItem extends IExtendItem
{
  public abstract void executeDeleteAction(String paramString, Transaction paramTransaction);

  public abstract void executeRecoveryAction(String paramString, Transaction paramTransaction);

  public abstract RecycbinListData getRecycleBinList(Mapx<String, Object> paramMapx, int paramInt1, int paramInt2, String paramString);

  public abstract int getDeleteCount(String paramString);
}
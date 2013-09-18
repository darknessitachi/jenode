package com.zving.framework.cache;

import com.zving.framework.collection.Mapx;
import com.zving.framework.extend.IExtendItem;

public abstract class CacheDataProvider
  implements IExtendItem
{
  protected Mapx<String, Mapx<String, Object>> TypeMap = new Mapx();

  public void init() {
    synchronized (this) {
      this.TypeMap = new Mapx();
    }
  }

  public void onKeySet(String type, String key, Object value)
  {
  }

  public abstract void onTypeNotFound(String paramString);

  public abstract void onKeyNotFound(String paramString1, String paramString2);

  public void destory()
  {
    for (Mapx map : this.TypeMap.valueArray()) {
      map.clear();
      map = null;
    }
    this.TypeMap.clear();
    this.TypeMap = null;
  }
}
package com.zving.framework.cache;

import com.zving.framework.collection.Mapx;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.ObjectUtil;

public class CacheManager
{
  public static CacheDataProvider getCache(String providerID)
  {
    return (CacheDataProvider)CacheService.getInstance().get(providerID);
  }

  public static Object get(String providerID, String type, int key)
  {
    return get(providerID, type, String.valueOf(key));
  }

  public static Object get(String providerID, String type, long key) {
    return get(providerID, type, String.valueOf(key));
  }

  public static Object get(String providerID, String type, String key) {
    CacheDataProvider cp = getCache(providerID);
    if ((cp == null) || (cp.TypeMap == null)) {
      throw new RuntimeException("CacheProvider not found:" + providerID);
    }
    Mapx map = (Mapx)cp.TypeMap.get(type);
    if (map == null) {
      synchronized (cp) {
        map = (Mapx)cp.TypeMap.get(type);
        if (map == null) {
          cp.onTypeNotFound(type);
          map = (Mapx)cp.TypeMap.get(type);
        }
        if (map == null) {
          LogUtil.warn("CacheManager.get():Can't found cache type '" + type + "' in CacheProvider " + providerID);
          return null;
        }
      }
    }
    if (!map.containsKey(key)) {
      synchronized (cp) {
        if (!map.containsKey(key)) {
          cp.onKeyNotFound(type, key);
        }
        if (!map.containsKey(key)) {
          LogUtil.warn("Get cache data failed: Provider=" + providerID + ",Type=" + type + ",Key=" + key);
          LogUtil.warn(ObjectUtil.getCurrentStack());
        }
      }
    }
    return map.get(key);
  }

  public static boolean contains(String providerID, String type, Object key)
  {
    CacheDataProvider cp = getCache(providerID);
    if (cp == null) {
      return false;
    }
    Mapx map = (Mapx)cp.TypeMap.get(type);
    if (map == null) {
      synchronized (cp) {
        map = (Mapx)cp.TypeMap.get(type);
        if (map == null) {
          cp.onTypeNotFound(type);
          map = (Mapx)cp.TypeMap.get(type);
        }
        if (map == null) {
          return false;
        }
      }
    }
    String strKey = String.valueOf(key);
    if (!map.containsKey(strKey)) {
      synchronized (cp) {
        if (!map.containsKey(strKey)) {
          cp.onKeyNotFound(type, strKey);
        }
        if (!map.containsKey(strKey)) {
          return false;
        }
      }
    }
    return true;
  }

  public static void set(String providerID, String type, int key, Object value)
  {
    set(providerID, type, String.valueOf(key), value);
  }

  public static void set(String providerID, String type, long key, Object value) {
    set(providerID, type, String.valueOf(key), value);
  }

  public static void set(String providerID, String type, String key, Object value) {
    CacheDataProvider cp = getCache(providerID);
    if (cp == null) {
      LogUtil.warn("未找到CacheProvider:" + providerID);
      return;
    }
    Mapx map = (Mapx)cp.TypeMap.get(type);
    if (map == null) {
      synchronized (cp) {
        if (!cp.TypeMap.containsKey(type)) {
          map = new Mapx();
          cp.TypeMap.put(type, map);
        }
      }
    }
    synchronized (cp) {
      map.put(key, value);
      if (CacheRefreshServlet.isClusteringEnable()) {
        CacheRefreshServlet.refresh(cp.getID(), type, key);
      }
      cp.onKeySet(type, key, value);
    }
  }

  public static void remove(String providerID, String type, int key)
  {
    remove(providerID, type, String.valueOf(key));
  }

  public static void remove(String providerID, String type, long key) {
    remove(providerID, type, String.valueOf(key));
  }

  public static void remove(String providerID, String type, String key) {
    CacheDataProvider cp = getCache(providerID);
    if (cp == null) {
      LogUtil.warn("CacheProvider not found:" + providerID);
      return;
    }
    Mapx map = (Mapx)cp.TypeMap.get(type);
    if (map == null) {
      LogUtil.warn("CacheManager.remove():Can't found cache type '" + type + "' in CacheProvider " + providerID);
      return;
    }
    synchronized (cp) {
      map.remove(key);
      if (CacheRefreshServlet.isClusteringEnable())
        CacheRefreshServlet.remove(cp.getID(), type, key);
    }
  }

  public static void removeType(String providerID, String type)
  {
    CacheDataProvider cp = getCache(providerID);
    if (cp == null) {
      LogUtil.warn("CacheProvider not found:" + providerID);
      return;
    }
    synchronized (cp) {
      cp.TypeMap.remove(type);
      if (CacheRefreshServlet.isClusteringEnable())
        CacheRefreshServlet.remove(cp.getID(), type);
    }
  }

  public static Mapx<String, Object> getMapx(String providerID, String type)
  {
    CacheDataProvider cp = getCache(providerID);
    if (cp == null) {
      LogUtil.warn("CacheProvider not found:" + providerID);
      return null;
    }
    Mapx map = (Mapx)cp.TypeMap.get(type);
    if (map == null) {
      synchronized (cp) {
        map = (Mapx)cp.TypeMap.get(type);
        if (map == null) {
          cp.onTypeNotFound(type);
          map = (Mapx)cp.TypeMap.get(type);
        }
        if (map == null) {
          LogUtil.warn("CacheManager.getMapx():Can't found cache type '" + type + "' in CacheProvider " + providerID);
          return null;
        }
      }
    }
    return map;
  }

  public static void setMapx(String providerID, String type, Mapx<String, Object> map)
  {
    CacheDataProvider cp = getCache(providerID);
    if (cp == null) {
      LogUtil.warn("CacheProvider not found:" + providerID);
      return;
    }
    synchronized (cp) {
      cp.TypeMap.put(type, map);
    }
    if (CacheRefreshServlet.isClusteringEnable())
      CacheRefreshServlet.refresh(cp.getID(), type);
  }

  /** @deprecated */
  public static Mapx<String, Object> get(String providerID, String type)
  {
    return getMapx(providerID, type);
  }

  public static void destory() {
    for (CacheDataProvider cdp : CacheService.getInstance().getAll())
      cdp.destory();
  }
}
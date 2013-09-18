package com.zving.framework.orm;

import com.zving.framework.collection.Mapx;

public class DAOMetadataManager
{
  static Mapx<String, DAOMetadata> map = new Mapx();

  public static DAOMetadata getMetadata(Class<? extends DAO> clazz)
  {
    String className = clazz.getName();
    if (!map.containsKey(className)) {
      DAOMetadata dm = new DAOMetadata(clazz);
      map.put(className, dm);
      return dm;
    }
    return (DAOMetadata)map.get(className);
  }
}
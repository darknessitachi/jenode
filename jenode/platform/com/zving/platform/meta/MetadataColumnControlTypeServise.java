package com.zving.platform.meta;

import com.zving.framework.collection.Mapx;
import com.zving.framework.extend.AbstractExtendService;

public class MetadataColumnControlTypeServise extends AbstractExtendService<IMetadataColumnControlType>
{
  public static MetadataColumnControlTypeServise getInstance()
  {
    return (MetadataColumnControlTypeServise)findInstance(MetadataColumnControlTypeServise.class);
  }

  public static Mapx<String, Object> getColumnControlTypeNameMap() {
    Mapx mapx = new Mapx();
    for (IMetadataColumnControlType t : getInstance().getAll()) {
      mapx.put(t.getID(), t.getName());
    }
    return mapx;
  }

  public static IMetadataColumnControlType getColumnControlTypoe(String typeID) {
    for (IMetadataColumnControlType t : getInstance().getAll()) {
      if (typeID.equals(t.getID())) {
        return t;
      }
    }
    return null;
  }
}
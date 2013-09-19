package com.zving.platform.meta;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.Transaction;
import com.zving.framework.orm.DAO;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.utility.ObjectUtil;
import com.zving.platform.pub.PlatformCache;
import com.zving.schema.ZDMetaColumn;
import com.zving.schema.ZDMetaModel;
import com.zving.schema.ZDMetaValue;

public class MetadataBL
{
  public static void addMetadata(Mapx<String, Object> params, Transaction trans)
  {
    long id = params.getLong("ModelID");
    String pk = params.getString("PKValue");
    String oldPk = params.getString("OldPKValue");
    MetaModel mm = PlatformCache.getMetaModel(id);
    if (mm == null) {
      return;
    }

    Mapx map = new Mapx();
    map.put("ModelID", Long.valueOf(id));
    map.put("PKValue", pk);
    Mapx mapping = mm.getMapping();
    for (String k : params.keyArray()) {
      if (k.startsWith("MetaValue_")) {
        String k2 = k.substring(10);
        if (mapping.get(k2) != null)
        {
          k2 = ((ZDMetaColumn)mapping.get(k2)).getTargetField();
          map.put(k2, params.get(k));
        }
      }
    }
    DAO dao = MetaUtil.newTargetTableInstance(mm.getDAO().getTargetTable());
    if (ObjectUtil.empty(oldPk)) {
      dao.setValue(map);
      trans.insert(dao);
    }
    else if (pk.equals(oldPk)) {
      dao.setValue(map);
      trans.update(dao);
    } else {
      dao.setV("ModelID", Long.valueOf(id));
      dao.setV("PKValue", oldPk);
      dao.fill();
      dao.setValue(map);
      trans.update(dao);
    }
  }

  public static void deleteMetadata(DAOSet<ZDMetaValue> set, Transaction trans)
  {
    trans.deleteAndBackup(set);
  }
}
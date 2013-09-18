package com.zving.framework.data.dbtype;

import com.zving.framework.extend.AbstractExtendService;

public class DBTypeService extends AbstractExtendService<IDBType>
{
  private static DBTypeService instance = null;

  public static DBTypeService getInstance() {
    if (instance == null) {
      instance = (DBTypeService)findInstance(DBTypeService.class);
    }
    return instance;
  }
}
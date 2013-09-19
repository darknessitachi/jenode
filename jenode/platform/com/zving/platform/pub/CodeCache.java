package com.zving.platform.pub;

import com.zving.framework.Config;
import com.zving.framework.cache.CacheDataProvider;
import com.zving.framework.cache.CacheManager;
import com.zving.framework.data.Q;
import com.zving.framework.orm.DAOSet;
import com.zving.schema.ZDCode;

public class CodeCache extends CacheDataProvider
{
  public static final String ProviderID = "Code";

  public String getID()
  {
    return "Code";
  }

  public void onKeyNotFound(String type, String key) {
    if (!"true".equals(Config.getValue("App.ExistPlatformDB"))) {
      return;
    }
    ZDCode code = new ZDCode();
    code.setCodeType(type);
    code.setCodeValue(key);
    DAOSet set = code.query();
    if (set.size() == 0) {
      return;
    }
    CacheManager.set("Code", type, key, set.get(0));
  }

  public void onTypeNotFound(String type) {
    if (!"true".equals(Config.getValue("App.ExistPlatformDB"))) {
      return;
    }
    DAOSet set = new ZDCode().query(new Q("where CodeType=? order by CodeOrder", new Object[] { type }));
    for (ZDCode code2 : set) {
      String parentcode = code2.getParentCode();
      if (!parentcode.equals("System"))
      {
        CacheManager.set("Code", code2.getCodeType(), code2.getCodeValue(), code2);
      }
    }
  }

  public String getName() { return "代码缓存"; }

}
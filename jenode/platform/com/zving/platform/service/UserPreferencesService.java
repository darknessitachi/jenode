package com.zving.platform.service;

import com.zving.framework.collection.Mapx;
import com.zving.framework.extend.AbstractExtendService;
import java.util.ArrayList;
import java.util.List;

public class UserPreferencesService extends AbstractExtendService<AbstractUserPreferences>
{
  public static UserPreferencesService getInstance()
  {
    return (UserPreferencesService)findInstance(UserPreferencesService.class);
  }

  public static boolean validate(Mapx<String, Object> map)
  {
    ArrayList keys = map.keyArray();
    for (String key : keys) {
      AbstractUserPreferences up = (AbstractUserPreferences)getInstance().get(key);
      if (up != null)
      {
        if (!up.validate(map.getString(key)))
          return false;
      }
    }
    return true;
  }

  public static Mapx<String, String> process(Mapx<String, Object> request)
  {
    Mapx map = new Mapx();
    List list = getInstance().getAll();
    for (AbstractUserPreferences up : list) {
      map.put(up.getID(), up.process(request));
    }
    return map;
  }
}
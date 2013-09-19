package com.zving.platform.log.type;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataTable;
import com.zving.framework.extend.menu.Menu;
import com.zving.framework.extend.menu.MenuManager;
import com.zving.platform.ILogType;

public class UserLog
  implements ILogType
{
  public static final String ID = "UserLog";
  private Mapx<String, String> map;

  public String getID()
  {
    return "UserLog";
  }

  public String getName() {
    return "@{Logs.UserLogMenu}";
  }

  public Mapx<String, String> getSubTypes() {
    if (this.map == null) {
      this.map = new Mapx();
      Mapx menus = MenuManager.getMenus();
      if ((menus != null) && (menus.size() > 0)) {
        for (Menu menu : menus.valueArray()) {
          this.map.put(menu.getID(), menu.getName());
        }
      }
    }
    return this.map;
  }

  public void decodeMessage(DataTable dt)
  {
  }
}
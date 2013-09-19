package com.zving.platform.service;

import com.zving.framework.User;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.extend.AbstractExtendService;
import com.zving.framework.extend.ExtendManager;
import com.zving.framework.extend.menu.Menu;
import com.zving.framework.extend.menu.MenuManager;
import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.utility.ObjectUtil;
import com.zving.platform.config.MenuOrder;
import com.zving.platform.privilege.AbstractMenuPriv;
import java.util.Comparator;

public class MenuPrivService extends AbstractExtendService<AbstractMenuPriv>
{
  public static MenuPrivService getInstance()
  {
    return (MenuPrivService)findInstance(MenuPrivService.class);
  }

  public static DataTable getAllMenus() {
    Mapx list = MenuManager.getMenus();
    DataTable dt = new DataTable();
    dt.insertColumn("ID");
    dt.insertColumn("ParentID");
    dt.insertColumn("Name");
    dt.insertColumn("Icon");
    dt.insertColumn("URL");
    dt.insertColumn("Order");
    dt.insertColumn("Description");
    dt.insertColumn("PluginID");
    dt.insertColumn("PluginName");
    dt.insertColumn("HasChild");
    for (Menu menu : list.valueArray()) {
      if ("Backend".equals(menu.getType()))
      {
        if (ObjectUtil.notEmpty(menu.getPluginConfig()))
          dt.insertRow(new Object[] { menu.getID(), menu.getParentID(), menu.getName(User.getLanguage()), menu.getIcon(), 
            menu.getURL(), menu.getOrder(), menu.getDescription(User.getLanguage()), menu.getPluginConfig().getID(), 
            menu.getPluginConfig().getName(), Boolean.valueOf(hasChild(menu.getID())) });
        else
          dt.insertRow(new Object[] { menu.getID(), menu.getParentID(), menu.getName(User.getLanguage()), menu.getIcon(), 
            menu.getURL(), menu.getOrder(), menu.getDescription(User.getLanguage()), "", "", Boolean.valueOf(hasChild(menu.getID())) });
      }
    }
    sortMenu(dt);
    dt = DataGridAction.sortTreeDataTable(dt, "ID", "ParentID");
    return dt;
  }

  public static boolean hasChild(String menuID) {
    Mapx list = MenuManager.getMenus();
    for (Menu menu : list.valueArray()) {
      if ((menu.getParentID() != null) && (menu.getParentID().equals(menuID))) {
        return true;
      }
    }
    return false;
  }

  public static DataTable getChildMenus(String parentID) {
    Mapx list = MenuManager.getMenus();
    DataTable dt = new DataTable();
    dt.insertColumn("ID");
    dt.insertColumn("ParentID");
    dt.insertColumn("Name");
    dt.insertColumn("Icon");
    dt.insertColumn("URL");
    dt.insertColumn("Order");
    dt.insertColumn("Description");
    dt.insertColumn("PluginID");
    dt.insertColumn("PluginName");
    dt.insertColumn("HasChild");
    for (Menu menu : list.valueArray()) {
      if ((menu.getParentID() != null) && (menu.getParentID().equals(parentID)))
      {
        if (ExtendManager.getInstance().isMenuEnable(menu.getID()))
        {
          if (ObjectUtil.notEmpty(menu.getPluginConfig()))
            dt.insertRow(new Object[] { menu.getID(), menu.getParentID(), menu.getName(User.getLanguage()), menu.getIcon(), 
              menu.getURL(), menu.getOrder(), menu.getDescription(User.getLanguage()), menu.getPluginConfig().getID(), 
              menu.getPluginConfig().getName(), Boolean.valueOf(hasChild(menu.getID())) });
          else
            dt.insertRow(new Object[] { menu.getID(), menu.getParentID(), menu.getName(User.getLanguage()), menu.getIcon(), 
              menu.getURL(), menu.getOrder(), menu.getDescription(User.getLanguage()), "", 
              "", Boolean.valueOf(hasChild(menu.getID())) });
        }
      }
    }
    dt.sort("Order", "asc");
    return dt;
  }

  public static DataTable getMainMenus() {
    Mapx list = MenuManager.getMenus();
    DataTable dt = new DataTable();
    dt.insertColumn("ID");
    dt.insertColumn("ParentID");
    dt.insertColumn("Name");
    dt.insertColumn("Icon");
    dt.insertColumn("URL");
    dt.insertColumn("Order");
    dt.insertColumn("Description");
    dt.insertColumn("PluginID");
    dt.insertColumn("PluginName");
    dt.insertColumn("HasChild");
    for (Menu menu : list.valueArray()) {
      if (ObjectUtil.empty(menu.getParentID()))
      {
        if (ExtendManager.getInstance().isMenuEnable(menu.getID()))
        {
          if (ObjectUtil.notEmpty(menu.getPluginConfig()))
            dt.insertRow(new Object[] { menu.getID(), menu.getParentID(), menu.getName(User.getLanguage()), menu.getIcon(), 
              menu.getURL(), menu.getOrder(), menu.getDescription(User.getLanguage()), menu.getPluginConfig().getID(), 
              menu.getPluginConfig().getName(), Boolean.valueOf(hasChild(menu.getID())) });
          else
            dt.insertRow(new Object[] { menu.getID(), menu.getParentID(), menu.getName(User.getLanguage()), menu.getIcon(), 
              menu.getURL(), menu.getOrder(), menu.getDescription(User.getLanguage()), "", "", Boolean.valueOf(hasChild(menu.getID())) });
        }
      }
    }
    sortMenu(dt);
    return dt;
  }

  private static void sortMenu(DataTable dt) {
    dt.sort("Order", "asc");
    String str = MenuOrder.getValue() + "\n";
    String order = str.replaceAll("\\s+", "\n");
    dt.sort(new Comparator() {
      public int compare(DataRow dr1, DataRow dr2) {
        String str1 = dr1.getString("ID") + "\n";
        String str2 = dr2.getString("ID") + "\n";
        int i1 = MenuPrivService.this.indexOf(str1);
        int i2 = MenuPrivService.this.indexOf(str2);
        if ((i1 >= 0) && (i2 < 0)) {
          return -1;
        }
        if ((i2 >= 0) && (i1 < 0)) {
          return 1;
        }
        return i1 - i2;
      }
    });
  }
}
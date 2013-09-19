package com.zving.platform.ui;

import com.zving.framework.UIFacade;
import com.zving.framework.User;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.extend.ExtendActionConfig;
import com.zving.framework.extend.ExtendItemConfig;
import com.zving.framework.extend.ExtendManager;
import com.zving.framework.extend.ExtendPointConfig;
import com.zving.framework.extend.ExtendServiceConfig;
import com.zving.framework.extend.menu.Menu;
import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.extend.plugin.PluginException;
import com.zving.framework.extend.plugin.PluginManager;
import com.zving.framework.i18n.Lang;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.ui.control.TreeAction;
import com.zving.framework.ui.control.TreeItem;
import com.zving.platform.pub.PlatformUtil;
import java.util.ArrayList;

@Alias("Plugin")
public class PluginUI extends UIFacade
{
  @Priv
  public void bindTree(TreeAction ta)
  {
    DataTable dt = new DataTable();
    dt.insertColumn("ID");
    dt.insertColumn("Name");
    dt.insertColumn("Author");
    dt.insertColumn("Version");
    for (PluginConfig pc : PluginManager.getInstance().getAllPluginConfig()) {
      dt.insertRow(new Object[] { pc.getID(), pc.getName(User.getLanguage()), pc.getAuthor(), pc.getVersion() });
    }
    ta.bindData(dt);
    ta.getItem(0).setText(Lang.get("Platform.PluginList"));
    for (int i = 1; i < ta.getItemSize(); i++) {
      ta.getItem(i).setIcon("icons/icon024a1.png");
      if (!PluginManager.getInstance().getPluginConfig(ta.getItem(i).getData().getString("ID")).isEnabling())
        ta.getItem(i).setIcon("icons/icon024a17.png");
    }
  }

  @Priv
  public void initBasicInfo()
  {
    String id = $V("ID");
    PluginConfig pc = PluginManager.getInstance().getPluginConfig(id);
    if (pc != null) {
      $S("ID", pc.getID());
      $S("Name", pc.getName(User.getLanguage()));
      $S("Version", pc.getVersion());
      $S("Author", pc.getAuthor());
      $S("Provider", pc.getProvider());
      if (pc.isEnabling())
        $S("Status", Lang.get("User.Enable"));
      else {
        $S("Status", Lang.get("User.Disable"));
      }
      $S("PluginClass", pc.getClassName());
      $S("Description", pc.getDescription(User.getLanguage()));
    }
  }

  @Priv
  public void bindPluginGrid(DataGridAction dga)
  {
    PluginConfig pc = PluginManager.getInstance().getPluginConfig($V("ID"));
    if (pc != null) {
      DataTable dt = new DataTable();
      dt.insertColumn("ID");
      dt.insertColumn("Name");
      dt.insertColumn("Version");
      for (String id : pc.getRequiredPlugins().keyArray()) {
        PluginConfig pc2 = PluginManager.getInstance().getPluginConfig(id);
        dt.insertRow(new Object[] { pc2.getID(), pc2.getName(User.getLanguage()), pc2.getVersion() });
      }
      dga.bindData(dt);
    }
  }

  @Priv
  public void bindDependOnPluginGrid(DataGridAction dga) {
    PluginConfig pc = PluginManager.getInstance().getPluginConfig($V("ID"));
    Mapx map = new Mapx();
    if (pc != null) {
      ArrayList list = PluginManager.getInstance().getAllPluginConfig();
      addDependOn(map, list, pc.getID());
    }
    DataTable dt = new DataTable();
    dt.insertColumn("ID");
    dt.insertColumn("Name");
    dt.insertColumn("Version");
    for (PluginConfig pc2 : map.valueArray()) {
      dt.insertRow(new Object[] { pc2.getID(), pc2.getName(User.getLanguage()), pc2.getVersion() });
    }
    dga.bindData(dt);
  }

  private void addDependOn(Mapx<String, PluginConfig> map, ArrayList<PluginConfig> list, String id)
  {
    for (PluginConfig pc : list)
      if ((pc.getRequiredPlugins().containsKey(id)) && 
        (!map.containsKey(pc.getID()))) {
        map.put(pc.getID(), pc);
        addDependOn(map, list, pc.getID());
      }
  }

  @Priv
  public void bindExtendPointGrid(DataGridAction dga)
  {
    PluginConfig pc = PluginManager.getInstance().getPluginConfig($V("ID"));
    if (pc == null) {
      return;
    }
    DataTable dt = new DataTable();
    dt.insertColumn("ID");
    dt.insertColumn("Icon");
    dt.insertColumn("ClassName");
    dt.insertColumn("UIFlag");
    dt.insertColumn("Description");
    for (ExtendPointConfig ep : pc.getExtendPoints().valueArray()) {
      dt.insertRow(new Object[] { ep.getID(), "platform/images/plugin_point.gif", ep.getClassName(), Boolean.valueOf(ep.getUIFlag()), 
        ep.getDescription(User.getLanguage()) });
    }
    dga.bindData(dt);
  }

  @Priv
  public void bindExtendServiceGrid(DataGridAction dga) {
    PluginConfig pc = PluginManager.getInstance().getPluginConfig($V("ID"));
    if (pc == null) {
      return;
    }
    DataTable dt = new DataTable();
    dt.insertColumn("ID");
    dt.insertColumn("Icon");
    dt.insertColumn("ClassName");
    dt.insertColumn("Description");
    for (ExtendServiceConfig ep : pc.getExtendServices().valueArray()) {
      dt.insertRow(new Object[] { ep.getID(), "platform/images/plugin_point.gif", ep.getClassName(), 
        ep.getDescription(User.getLanguage()) });
    }
    dga.bindData(dt);
  }

  @Priv
  public void bindExtendItemGrid(DataGridAction dga) {
    PluginConfig pc = PluginManager.getInstance().getPluginConfig($V("ID"));
    if (pc == null) {
      return;
    }
    DataTable dt = new DataTable();
    dt.insertColumn("ID");
    dt.insertColumn("Icon");
    dt.insertColumn("ClassName");
    dt.insertColumn("Description");
    for (ExtendItemConfig ep : pc.getExtendItems().valueArray()) {
      dt.insertRow(new Object[] { ep.getID(), "platform/images/plugin_point.gif", ep.getClassName(), 
        ep.getDescription(User.getLanguage()) });
    }
    dga.bindData(dt);
  }

  @Priv
  public void bindExtendActionGrid(DataGridAction dga) {
    PluginConfig pc = PluginManager.getInstance().getPluginConfig($V("ID"));
    if (pc == null) {
      return;
    }
    DataTable dt = new DataTable();
    dt.insertColumn("ID");
    dt.insertColumn("Icon");
    dt.insertColumn("ClassName");
    dt.insertColumn("ExtendPointID");
    dt.insertColumn("Description");

    for (ExtendActionConfig ep : pc.getExtendActions().valueArray()) {
      dt.insertRow(new Object[] { ep.getID(), "platform/images/plugin_action.gif", ep.getClassName(), ep.getExtendPointID(), 
        ep.getDescription(User.getLanguage()) });
    }
    dga.bindData(dt);
  }

  @Priv
  public void bindMenuGrid(DataGridAction dga) {
    String PluginID = $V("ID");
    PluginConfig pc = PluginManager.getInstance().getPluginConfig(PluginID);
    if (pc == null) {
      return;
    }
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
    for (Menu menu : pc.getMenus().valueArray())
      if ("Backend".equals(menu.getType()))
      {
        dt.insertRow(new Object[] { menu.getID(), menu.getParentID(), menu.getName(User.getLanguage()), menu.getIcon(), menu.getURL(), 
          menu.getOrder(), menu.getDescription(User.getLanguage()), menu.getPluginConfig().getID(), 
          menu.getPluginConfig().getName() });
      }
    dt.sort("Order", "asc");
    dt = DataGridAction.sortTreeDataTable(dt, "ID", "ParentID");
    dga.bindData(dt);
  }

  @Priv
  public void bindFileTree(TreeAction ta) {
    PluginConfig pc = PluginManager.getInstance().getPluginConfig($V("ID"));
    if (pc != null) {
      DataTable dt = new DataTable();
      dt.insertColumn("Icon");
      dt.insertColumn("ParentID");
      dt.insertColumn("ID");
      dt.insertColumn("Name");
      Mapx map = new Mapx();
      for (String path : pc.getPluginFiles()) {
        setParentAndName(dt, map, path);
      }
      ta.setRootText(Lang.get("Plugin.PluginFiles"));
      ta.bindData(dt);
      for (TreeItem ti : ta.getItemList())
        if (ti.getData() != null)
          ti.setIcon(ti.getData().getString("Icon"));
    }
  }

  private void setParentAndName(DataTable dt, Mapx<String, String> map, String path)
  {
    if (map.containsKey(path)) {
      return;
    }
    dt.insertRow(null);
    DataRow dr = dt.getDataRow(dt.getRowCount() - 1);
    if (path.startsWith("[D]")) {
      path = path.substring(3);
      if (!path.endsWith("/")) {
        path = path + "/";
      }
    }
    dr.set("Icon", PlatformUtil.getFileIcon(path));
    map.put(path, "");
    dr.set("ID", path);

    if (path.endsWith("/")) {
      path = path.substring(0, path.length() - 1);
    }
    if (path.indexOf("/") > 0) {
      String p = path.substring(0, path.lastIndexOf("/") + 1);
      dr.set("ParentID", p);
      dr.set("Name", path.substring(path.lastIndexOf("/") + 1));
      setParentAndName(dt, map, p);
    } else {
      dr.set("ParentID", "");
      dr.set("Name", path);
    }
  }

  @Priv
  public void setStatus()
  {
    String id = $V("ID");
    String status = $V("Status");
    PluginConfig pc = PluginManager.getInstance().getPluginConfig(id);
    String failed = Lang.get("Common.ExecuteFailed");
    if (pc == null) {
      fail(failed + ":Plugin not found!");
      return;
    }
    try {
      if ("true".equals(status)) {
        if (pc.isEnabling()) {
          fail(failed + ":" + Lang.get("Platform.PluginAlreadyEnable"));
          return;
        }
        ExtendManager.getInstance().enablePlugin(id);
      }
      else {
        if (!pc.isEnabling()) {
          fail(failed + ":" + Lang.get("Platform.PluginAlreadyDisabled"));
          return;
        }
        ExtendManager.getInstance().disablePlugin(id);
      }
    }
    catch (PluginException e) {
      e.printStackTrace();
      fail(failed + ":" + e.getMessage());
      return;
    }
    success(Lang.get("Common.ExecuteSuccess"));
  }
}
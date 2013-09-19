package com.zving.platform.log.ui;

import com.zving.framework.UIFacade;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.i18n.Lang;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.ui.control.TreeAction;
import com.zving.framework.ui.control.TreeItem;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.ILogMenu;
import com.zving.platform.ILogMenuGroup;
import com.zving.platform.ILogType;
import com.zving.platform.bl.LogBL;
import com.zving.platform.service.LogMenuGroupService;
import com.zving.platform.service.LogMenuService;
import com.zving.platform.service.LogTypeService;

@Alias("Log")
public class LogUI extends UIFacade
{
  @Priv
  public void bindTree(TreeAction ta)
  {
    DataTable dt = new DataTable();
    dt.insertColumn("ID");
    dt.insertColumn("ParentID");
    dt.insertColumn("Name");
    dt.insertColumn("Target");

    for (ILogMenuGroup group : LogMenuGroupService.getInstance().getAll()) {
      dt.insertRow(new Object[] { group.getID(), "", group.getName(), "" });
    }
    for (ILogMenu type : LogMenuService.getInstance().getAll()) {
      dt.insertRow(new Object[] { type.getID(), type.getGroupID(), type.getName(), type.getDetailURL() });
    }
    dt.insertColumn("Icon");
    for (DataRow dr : dt) {
      if (ObjectUtil.empty(dr.get("Target")))
        dr.set("Icon", "icons/extra/icon_folder_other.gif");
      else {
        dr.set("Icon", "icons/icon026a1.png");
      }
    }
    ta.setRootText(Lang.get("Logs.LogViewer"));
    ta.bindData(dt);
    for (TreeItem ti : ta.getItemList())
      if (ti.getData() != null)
        ti.setIcon(ti.getData().getString("Icon"));
  }

  @Priv
  public void bindSQLGrid(DataGridAction dga)
  {
    LogBL.bindGridAction(dga, "SQLLog");
  }

  @Priv
  public void bindCronGrid(DataGridAction dga) {
    LogBL.bindGridAction(dga, "CronLog");
  }

  @Priv
  public void bindSecurityGrid(DataGridAction dga) {
    LogBL.bindGridAction(dga, "SecurityLog");
  }

  @Priv
  public void bindUserGrid(DataGridAction dga) {
    if (StringUtil.isEmpty(dga.getParam("UserName"))) {
      Mapx mapx = new Mapx();
      mapx.putAll(dga.getParams());
      mapx.put("UserName", "admin");
      dga.setParams(mapx);
    }
    LogBL.bindGridAction(dga, "UserLog");
  }

  @Priv
  public void bindCommonGrid(DataGridAction dga) {
    String type = dga.getParam("TypeID");
    LogBL.bindGridAction(dga, type);
  }

  @Priv
  public DataTable getSubTypeData() {
    String typeID = $V("TypeID");
    ILogType type = (ILogType)LogTypeService.getInstance().get(typeID);
    if (type == null) {
      return new DataTable();
    }
    return type.getSubTypes().toDataTable();
  }
}
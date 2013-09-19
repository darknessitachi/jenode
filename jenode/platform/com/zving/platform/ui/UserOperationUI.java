package com.zving.platform.ui;

import com.zving.framework.UIFacade;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.service.MenuPrivService;
import com.zving.schema.ZDUser;

@Alias("UserOperation")
public class UserOperationUI extends UIFacade
{
  @Priv
  @Alias("bindData")
  public void dg1DataBind(DataGridAction dga)
  {
    DataTable dt = MenuPrivService.getAllMenus();

    String userName = $V("UserName");
    if (StringUtil.isNull(userName)) {
      userName = "admin";
    }
    dt.insertColumn("ClickCount");
    if ((dt != null) && (dt.getRowCount() > 0)) {
      for (DataRow dr : dt) {
        int count = new Q("select count(1) from ZDUserLog where UserName=? and LogType=? and SubType=?", new Object[] { userName, "UserLog", dr.getString("ID") }).executeInt();
        dr.set("ClickCount", Integer.valueOf(count));
      }
    }
    dt.sort("ClickCount", "DESC");
    dga.bindData(dt);
  }
  @Priv
  @Alias("getUsers")
  public DataTable getUserList() {
    DAOSet userSet = new ZDUser().query(new Q("where Status='Y'", new Object[0]));
    DataTable dt = new DataTable();
    dt.insertColumn("ID");
    dt.insertColumn("Name");
    if ((userSet != null) && (userSet.size() > 0)) {
      for (ZDUser user : userSet) {
        dt.insertRow(new Object[] { user.getUserName(), user.getUserName() });
      }
    }
    return dt;
  }
}
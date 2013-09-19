package com.zving.platform.bl;

import com.zving.framework.Config;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.schema.ZDUser;

public class RoleBL
{
  public static String getAdminRoleCode()
  {
    String role = Config.getValue("System.AdminRoleCode");
    if (ObjectUtil.empty(role)) {
      role = "admin";
    }
    return role;
  }
  public static String getRoleCode(String userName) {
    ZDUser user = new ZDUser();
    user.setUserName(userName);
    if (!user.fill()) {
      return "";
    }
    DataTable dt = new Q("select RoleCode from ZDUserRole where UserName=?", new Object[] { userName }).fetch();
    String roleCode = "";
    if ((dt != null) && (dt.getRowCount() > 0)) {
      for (DataRow dr : dt) {
        if (!roleCode.equals("")) {
          roleCode = roleCode + ",";
        }
        roleCode = roleCode + dr.getString("RoleCode");
      }
    }
    return roleCode;
  }

  public static DataTable getRoleTable(String userBranchInnerCode, boolean isadmin) {
    Q q = new Q("select * from ZDRole", new Object[0]);
    if (!isadmin) {
      q.append(" Where BranchInnerCode like ?", new Object[] { userBranchInnerCode + "%" });
    }
    DataTable dt = q.fetch();
    LangUtil.decode(dt, "RoleName");
    return dt;
  }
}
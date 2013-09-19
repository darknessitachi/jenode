package com.zving.platform.ui;

import com.zving.framework.RequestData;
import com.zving.framework.ResponseData;
import com.zving.framework.UIFacade;
import com.zving.framework.User;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.cache.CacheManager;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.data.Transaction;
import com.zving.framework.i18n.Lang;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.ui.control.TreeAction;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.bl.RoleBL;
import com.zving.platform.pub.PlatformCache;
import com.zving.platform.pub.PlatformUtil;
import com.zving.schema.ZDPrivilege;
import com.zving.schema.ZDRole;
import com.zving.schema.ZDUserRole;
import java.util.Date;

@Alias("Role")
public class RoleUI extends UIFacade
{
  public static final String EVERYONE = "everyone";

  @Priv("Platform.Role")
  public void bindTree(TreeAction ta)
  {
    Q qb = new Q("select RoleCode,'' as ParentID,'1' as TreeLevel,RoleName from ZDRole", new Object[0]);
    if (!User.isBranchAdministrator()) {
      qb.append(" where BranchInnerCode like ?", new Object[] { User.getBranchInnerCode() + "%" });
    }
    DataTable dt = qb.fetch();
    LangUtil.decode(dt, "RoleName");
    ta.setRootText(Lang.get("Role.Title"));
    ta.setIdentifierColumnName("RoleCode");
    ta.setBranchIcon("icons/icon025a1.png");
    ta.setLeafIcon("icons/icon025a1.png");
    ta.bindData(dt);
  }

  @Priv
  public void roleDataBind(DataGridAction dga)
  {
    DataTable dt = new Q("select RoleCode,RoleName from ZDRole", new Object[0]).fetch();
    LangUtil.decode(dt, "RoleName");
    dga.bindData(dt);
  }

  @Priv("Platform.Role")
  public void init()
  {
    String RoleCode = $V("ID");
    $S("RoleCode", RoleCode);
    if (ObjectUtil.notEmpty(RoleCode)) {
      ZDRole role = new ZDRole();
      role.setRoleCode(RoleCode);
      if (role.fill()) {
        Mapx map = role.toMapx();
        this.Response.putAll(map);

        String name = new Q("select name from zdbranch where branchInnercode=?", new Object[] { role.getBranchInnerCode() })
          .executeString();
        $S("BranchName", name);
        LangUtil.decode(this.Response);
      }
    }
  }

  @Priv("Platform.Role.Add")
  public void add()
  {
    ZDRole role = new ZDRole();
    role.setValue(this.Request);
    role.setRoleCode(role.getRoleCode().toLowerCase());
    if (role.fill()) {
      fail(Lang.get("Role.Code") + role.getRoleCode() + " " + Lang.get("Common.Exists"));
      return;
    }
    Date currentDate = new Date();
    String currentUserName = User.getUserName();
    role.setAddTime(currentDate);
    role.setAddUser(currentUserName);
    role.setRoleName(LangUtil.getI18nFieldValue("RoleName"));
    role.setMemo(LangUtil.getI18nFieldValue("Memo"));

    Transaction trans = new Transaction();
    trans.add(role, 1);

    ZDPrivilege priv = new ZDPrivilege();
    priv.setOwnerType("R");
    priv.setOwner(role.getRoleCode());
    priv.setAddTime(currentDate);
    priv.setAddUser(currentUserName);
    trans.add(priv, 1);

    if (trans.commit()) {
      CacheManager.set("Platform", "Role", role.getRoleCode(), role);
      success(Lang.get("Common.AddSuccess"));
    } else {
      fail(Lang.get("Common.AddFailed"));
    }
  }

  @Priv("Platform.Role.Edit")
  public void save()
  {
    ZDRole role = new ZDRole();
    role.setRoleCode($V("RoleCode"));
    role.fill();

    role.setValue(this.Request);
    role.setModifyTime(new Date());
    role.setModifyUser(User.getUserName());
    role.setRoleName(LangUtil.getI18nFieldValue("RoleName"));
    role.setMemo(LangUtil.getI18nFieldValue("Memo"));

    if (role.update()) {
      CacheManager.set("Platform", "Role", role.getRoleCode(), role);
      success(Lang.get("Common.ModifySuccess"));
    } else {
      fail(Lang.get("Common.ModifyFailed"));
    }
  }

  @Priv("Platform.Role.Delete")
  public void del()
  {
    String RoleCode = this.Request.getString("RoleCode");
    Transaction trans = new Transaction();
    ZDRole role = new ZDRole();
    role.setRoleCode(RoleCode);
    role.fill();
    if ("everyone".equalsIgnoreCase(RoleCode)) {
      fail("everyone " + Lang.get("Role.CannotDelete"));
      return;
    }
    if (RoleBL.getAdminRoleCode().equalsIgnoreCase(RoleCode)) {
      fail(RoleBL.getAdminRoleCode() + " " + Lang.get("Role.CannotDelete"));
      return;
    }

    trans.add(role, 5);

    ZDUserRole userRole = new ZDUserRole();
    DAOSet userRoleSet = userRole.query(new Q("where RoleCode =?", new Object[] { RoleCode }));

    trans.add(userRoleSet, 5);

    trans.add(new ZDPrivilege().query(new Q("where OwnerType=? and Owner=?", new Object[] { "R", RoleCode })), 
      5);

    if (trans.commit()) {
      PlatformCache.removeRole(role.getRoleCode());
      success(Lang.get("Common.DeleteSuccess"));
    } else {
      fail(Lang.get("Common.DeleteFailed"));
    }
  }

  @Priv("Platform.Role")
  public void bindGrid(DataGridAction dga)
  {
    String RoleCode = dga.getParam("ID");
    if ((RoleCode == null) || ("".equals(RoleCode))) {
      RoleCode = dga.getParams().getString("Cookie.Role.LastRoleCode");
      if ((RoleCode == null) || ("".equals(RoleCode))) {
        dga.bindData(new DataTable());
        return;
      }
    }
    Q qb = new Q(
      "select * from ZDUser where exists (select UserName from ZDUserRole b where b.UserName = ZDUser.UserName and b.RoleCode=?)", new Object[] { 
      RoleCode });
    DataTable dt = qb.fetch(dga.getPageSize(), dga.getPageIndex());
    dt.insertColumn("RoleNames");
    for (int i = 0; i < dt.getRowCount(); i++) {
      dt.set(i, "RoleNames", PlatformUtil.getRoleNames(PlatformUtil.getRoleCodesByUserName(dt.getString(i, "UserName"))));
    }
    LangUtil.decode(dt, "RealName");
    dga.setTotal(qb);
    dga.bindData(dt);
  }

  @Priv("Platform.Role")
  public void bindUserList(DataGridAction dga)
  {
    String roleCode = dga.getParam("RoleCode");
    String searchUserName = dga.getParam("SearchUserName");
    Q qb = new Q("select * from ZDUser a", new Object[0]);
    qb.append(" where BranchInnerCode like ?", new Object[] { User.getBranchInnerCode() + "%" });
    qb.append(" and not exists (select '' from zduserrole b where b.roleCode=? and b.userName=a.userName)", new Object[] { roleCode });
    if (StringUtil.isNotEmpty(searchUserName)) {
      qb.append(" and (UserName like ?", new Object[] { "%" + searchUserName.trim() + "%" });

      qb.append(" or realname like ?)", new Object[] { "%" + searchUserName.trim() + "%" });
    }
    dga.setTotal(qb);
    DataTable dt = qb.fetch(dga.getPageSize(), dga.getPageIndex());
    dt.decodeColumn("Status", PlatformUtil.getCodeMap("Enable"));
    LangUtil.decode(dt, "StatusName");
    LangUtil.decode(dt, "RealName");
    dga.bindData(dt);
  }

  @Priv("Platform.Role.AddUser")
  public void addUserToRole()
  {
    String RoleCode = $V("RoleCode");
    if (StringUtil.isEmpty(RoleCode)) {
      return;
    }
    String UserNameStr = $V("UserNames");
    String[] UserNames = UserNameStr.split(",");
    Date currentDate = new Date();
    String currentUserName = User.getUserName();
    Transaction trans = new Transaction();

    DAOSet set = new DAOSet();
    for (int i = 0; i < UserNames.length; i++)
      if (!StringUtil.isEmpty(UserNames[i]))
      {
        ZDUserRole userRole = new ZDUserRole();
        userRole.setUserName(UserNames[i]);
        userRole.setRoleCode(RoleCode);
        userRole.setAddTime(currentDate);
        userRole.setAddUser(currentUserName);
        set.add(userRole);
      }
    trans.add(set, 1);
    if (trans.commit()) {
      for (int i = 0; i < set.size(); i++) {
        PlatformCache.addUserRole(((ZDUserRole)set.get(i)).getUserName(), ((ZDUserRole)set.get(i)).getRoleCode());
      }
      success(Lang.get("Common.AddSuccess"));
    } else {
      fail(Lang.get("Common.AddFailed"));
    }
  }

  @Priv("Platform.Role.RemoveUser")
  public void delUserFromRole()
  {
    String RoleCode = $V("RoleCode");
    String UserNameStr = $V("UserNames");
    String[] UserNames = UserNameStr.split(",");
    Transaction trans = new Transaction();

    DAOSet set = new DAOSet();
    for (int i = 0; i < UserNames.length; i++) {
      DataTable dt = new Q("select RoleCode from ZDUserRole where UserName=? and RoleCode!=?", new Object[] { UserNames[i], RoleCode })
        .fetch();
      String[] RoleCodes = new String[dt.getRowCount()];
      for (int j = 0; j < dt.getRowCount(); j++) {
        RoleCodes[j] = dt.getString(j, 0);
      }
      ZDUserRole userRole = new ZDUserRole();
      userRole.setUserName(UserNames[i]);
      userRole.setRoleCode(RoleCode);
      userRole.fill();
      set.add(userRole);
    }
    trans.add(set, 5);
    if (trans.commit()) {
      for (int i = 0; i < set.size(); i++) {
        PlatformCache.removeUserRole(((ZDUserRole)set.get(i)).getUserName(), ((ZDUserRole)set.get(i)).getRoleCode());
      }
      success(Lang.get("Common.DeleteSuccess"));
    } else {
      fail(Lang.get("Common.DeleteFailed"));
    }
  }
}
package com.zving.platform.ui;

import com.zving.framework.Current;
import com.zving.framework.ResponseData;
import com.zving.framework.UIFacade;
import com.zving.framework.User;
import com.zving.framework.User.UserData;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.data.Transaction;
import com.zving.framework.i18n.Lang;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.security.LicenseInfo;
import com.zving.framework.security.PasswordUtil;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.ui.control.TreeAction;
import com.zving.framework.utility.Errorx;
import com.zving.framework.utility.JSONUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.bl.UserBL;
import com.zving.platform.code.YesOrNo;
import com.zving.platform.config.AdminUserName;
import com.zving.platform.pub.PlatformCache;
import com.zving.platform.pub.PlatformUtil;
import com.zving.platform.service.UserPreferencesService;
import com.zving.schema.ZDBranch;
import com.zving.schema.ZDUser;
import com.zving.schema.ZDUserPreferences;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Alias("User")
public class UserUI extends UIFacade
{
  private static Pattern idPattern = Pattern.compile("[\\w@\\.\\,\\_\\-]*", 34);

  @Priv("Platform.User")
  public void init()
  {
    String userName = $V("ID");
    $S("UserName", userName);
    if (ObjectUtil.notEmpty(userName)) {
      ZDUser user = new ZDUser();
      user.setUserName(userName);
      user.fill();
      this.Response.putAll(user.toMapx());
      if (StringUtil.isNotNull(user.getBranchInnerCode())) {
        ZDBranch branch = PlatformCache.getBranch(user.getBranchInnerCode());
        if (!ObjectUtil.empty(branch)) {
          $S("BranchName", branch.getName());
        }
      }
      $S("Password", "********");
    }
  }

  @Priv("Platform.User")
  public void bindGrid(DataGridAction dga) {
    String userName = dga.getParam("UserName");
    String realName = dga.getParam("RealName");
    String branchInnerCode = dga.getParam("BranchInnerCode");
    if (StringUtil.isEmpty(branchInnerCode)) {
      branchInnerCode = User.getBranchInnerCode();
    }
    Q qb = new Q("select * from ZDUser ", new Object[0]);
    qb.append(" where 1=1", new Object[0]);
    if (StringUtil.isNotEmpty(userName)) {
      qb.append(" and UserName like ? ", new Object[] { "%" + userName.trim() + "%" });
    }
    if (StringUtil.isNotEmpty(realName)) {
      qb.append(" and RealName like ? ", new Object[] { "%" + realName.trim() + "%" });
    }
    if ((StringUtil.isNotEmpty(branchInnerCode)) && (!User.isBranchAdministrator())) {
      qb.append(" and BranchInnerCode like ? ", new Object[] { branchInnerCode.trim() + "%" });
    }
    qb.append(" order by AddTime desc,UserName", new Object[0]);
    dga.setTotal(qb);
    DataTable dt = qb.fetch(dga.getPageSize(), dga.getPageIndex());
    dt.decodeColumn("BranchInnerCode", new Q("select BranchInnerCode,Name from ZDBranch", new Object[0]).fetch().toMapx(0, 1));
    dt.decodeColumn("Status", PlatformUtil.getCodeMap("Enable"));
    LangUtil.decode(dt, "BranchInnerCodeName");
    LangUtil.decode(dt, "RealName");
    dt.insertColumn("RoleNames");
    for (int i = 0; i < dt.getRowCount(); i++) {
      dt.set(i, "RoleNames", PlatformUtil.getRoleNames(PlatformUtil.getRoleCodesByUserName(dt.getString(i, "UserName"))));
    }
    YesOrNo.decodeYesOrNoIcon(dt, "Status", false);
    dga.bindData(dt);
  }

  @Priv("Platform.User")
  public void bindRoleTree(TreeAction ta)
  {
    DataTable dt = new Q(
      "select RoleCode,'' as ParentID,'1' as TreeLevel,RoleName,'' as Checked from zdrole where branchinnercode like ?", new Object[] { 
      User.getBranchInnerCode() + "%" }).fetch();
    String userName = $V("ID");
    List roles = null;
    if (ObjectUtil.notEmpty(userName)) {
      roles = PlatformUtil.getRoleCodesByUserName(userName);
    }
    for (int i = 0; i < dt.getRowCount(); i++) {
      String code = dt.getString(i, "RoleCode");
      if ((ObjectUtil.empty(userName)) && ("everyone".equalsIgnoreCase(code))) {
        dt.set(i, "Checked", "Checked");
      }
      if ((roles != null) && (roles.contains(code))) {
        dt.set(i, "Checked", "Checked");
      }
    }
    ta.setRootText(Lang.get("Platform.Plugin.Role"));
    ta.setIdentifierColumnName("RoleCode");
    LangUtil.decode(dt, "RoleName");
    ta.bindData(dt);
  }

  @Priv("Platform.User.Add")
  public void add() {
    if (new Q("select count(*) from ZDUser", new Object[0]).executeInt() >= LicenseInfo.getUserLimit()) {
      fail(Lang.get("Platform.LicenseWarning"));
      return;
    }
    Transaction trans = new Transaction();
    if (!UserBL.add(trans, this.Request)) {
      fail(Errorx.printString());
      return;
    }
    if (trans.commit())
      success(Lang.get("Common.AddSuccess"));
    else
      fail(Lang.get("Common.AddFailed"));
  }

  @Priv("Platform.User.Edit")
  public void save()
  {
    Transaction trans = new Transaction();
    if (!UserBL.save(trans, this.Request)) {
      fail(Errorx.getAllMessage());
      return;
    }
    if (trans.commit())
      success(Lang.get("Common.ModifySuccess"));
    else
      fail(Lang.get("Common.ModifyFailed"));
  }

  @Priv("Platform.User.Delete")
  public void del()
  {
    String UserNames = $V("UserNames");
    if (!idPattern.matcher(UserNames).matches()) {
      fail("Invalid UserNames:" + UserNames);
      return;
    }
    Transaction trans = new Transaction();
    if (!UserBL.delete(trans, this.Request)) {
      fail(Errorx.printString());
      return;
    }
    if (trans.commit())
      success(Lang.get("Common.DeleteSuccess"));
    else
      fail(Lang.get("Common.DeleteFailed"));
  }

  @Priv("Platform.User.Disable")
  public void disableUser()
  {
    String UserNames = $V("UserNames");
    if (!idPattern.matcher(UserNames).matches()) {
      fail("Invalid UserName!");
      return;
    }
    ZDUser user = new ZDUser();
    DAOSet userSet = user.query(new Q(" where UserName in ('" + UserNames.replaceAll(",", "','") + "')", new Object[0]));
    for (int i = 0; i < userSet.size(); i++) {
      if (AdminUserName.getValue().equalsIgnoreCase(((ZDUser)userSet.get(i)).getUserName())) {
        fail(AdminUserName.getValue() + Lang.get("User.CannotDisableAdmin"));
        return;
      }
      ((ZDUser)userSet.get(i)).setStatus("N");
    }
    if (userSet.update())
      success(Lang.get("Common.ExecuteSuccess"));
    else
      fail(Lang.get("Common.ExecuteFailed"));
  }

  @Priv("Platform.User.Enable")
  public void enableUser()
  {
    String UserNames = $V("UserNames");
    if (!idPattern.matcher(UserNames).matches()) {
      fail("Invalid UserName!");
      return;
    }
    ZDUser user = new ZDUser();
    DAOSet userSet = user.query(new Q(" where UserName in ('" + UserNames.replaceAll(",", "','") + "')", new Object[0]));
    for (int i = 0; i < userSet.size(); i++) {
      if (AdminUserName.getValue().equalsIgnoreCase(((ZDUser)userSet.get(i)).getUserName())) {
        fail(AdminUserName.getValue() + Lang.get("User.CannotDisableAdmin"));
        return;
      }
      ((ZDUser)userSet.get(i)).setStatus("Y");
    }
    if (userSet.update())
      success(Lang.get("Common.ExecuteSuccess"));
    else
      fail(Lang.get("Common.ExecuteFailed"));
  }

  @Priv("Platform.User.ChangePassword")
  public void changePassword()
  {
    String Password = $V("Password");
    Q qb = new Q("update ZDUser set Password=? where UserName=?", new Object[0]);
    qb.add(new Object[] { PasswordUtil.generate(Password) });
    qb.add(new Object[] { $V("UserName") });
    if (qb.executeNoQuery() > 0)
      success(Lang.get("Common.ModifySuccess"));
    else
      fail(Lang.get("Common.ModifyFailed"));
  }

  @Priv("Platform.User")
  public void initUserPreferences()
  {
    ZDUserPreferences up = new ZDUserPreferences();
    up.setUserName(User.getUserName());
    if (!up.fill()) {
      return;
    }
    String json = up.getConfigProps();
    Mapx values = JSONUtil.toMap(json);
    this.Response.putAll(values);
  }

  @Priv("Platform.User.Add||Platform.User.Edit")
  public void saveUserPreferences() {
    if (!UserPreferencesService.validate(this.Request)) {
      fail(Lang.get("Common.InvalidID"));
      return;
    }
    ZDUserPreferences up = new ZDUserPreferences();
    up.setUserName(User.getUserName());
    if (up.fill()) {
      up.setModifyTime(new Date());
      up.setModifyUser(User.getUserName());
      Current.getTransaction().update(up);
    } else {
      up.setAddTime(new Date());
      up.setAddUser(User.getUserName());
      Current.getTransaction().insert(up);
    }
    Mapx map = UserPreferencesService.process(this.Request);
    up.setConfigProps(JSONUtil.toJSON(map));
    if (Current.getTransaction().commit()) {
      User.getCurrent().putAll(map);
      success(Lang.get("Common.SaveSuccess"));
    } else {
      fail(Lang.get("Common.SaveFailed"));
    }
  }
}
package com.zving.platform.bl;

import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.User;
import com.zving.framework.User.UserData;
import com.zving.framework.cache.CacheManager;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataCollection;
import com.zving.framework.data.Q;
import com.zving.framework.data.Transaction;
import com.zving.framework.extend.ExtendManager;
import com.zving.framework.i18n.Lang;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.security.PasswordUtil;
import com.zving.framework.security.Privilege;
import com.zving.framework.utility.Errorx;
import com.zving.framework.utility.JSONUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.code.Enable;
import com.zving.platform.code.YesOrNo;
import com.zving.platform.config.AdminUserName;
import com.zving.platform.privilege.AbstractMenuPriv;
import com.zving.platform.pub.PlatformCache;
import com.zving.platform.service.MenuPrivService;
import com.zving.schema.ZDPrivilege;
import com.zving.schema.ZDRole;
import com.zving.schema.ZDUser;
import com.zving.schema.ZDUserLog;
import com.zving.schema.ZDUserPreferences;
import com.zving.schema.ZDUserRole;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserBL {
	public static Pattern UserPattern = Pattern.compile("[\\w@\\.一-龥]{1,20}",
			34);

	public static boolean delete(Transaction trans, DataCollection dc) {
		String UserNames = dc.getString("UserNames");
		ZDUser user = new ZDUser();
		DAOSet userSet = user.query(new Q(" where UserName in ('"
				+ UserNames.replaceAll(",", "','") + "')", new Object[0]));
		trans.add(userSet, 5);

		for (int i = 0; i < userSet.size(); i++) {
			user = (ZDUser) userSet.get(i);
			if (User.getUserName().equals(user.getUserName())) {
				Errorx.addError(Lang.get("User.CanDeleteSelf"));
				return false;
			}
			if (AdminUserName.getValue().equalsIgnoreCase(user.getUserName())) {
				Errorx.addError(AdminUserName.getValue()
						+ Lang.get("User.CannotDeleteAdmin"));
				return false;
			}

			CacheManager.remove("Platform", "User", user.getUserName());
			CacheManager.remove("Platform", "UserRole", user.getUserName());

			ZDUserRole userRole = new ZDUserRole();
			userRole.setUserName(user.getUserName());
			DAOSet userRoleSet = userRole.query();
			trans.add(userRoleSet, 5);

			trans.add(new ZDPrivilege().query(new Q(
					"where OwnerType=? and Owner=?", new Object[] { "U",
							user.getUserName() })), 5);

			DAOSet logSet = new ZDUserLog().query(new Q("where UserName=?",
					new Object[] { user.getUserName() }));
			trans.add(logSet, 5);
		}
		return true;
	}

	public static boolean save(Transaction trans, DataCollection dc) {
		ZDUser user = new ZDUser();
		user.setUserName(dc.getString("UserName"));
		if (!user.fill()) {
			Errorx.addError(Lang.get("Common.UserName") + " "
					+ user.getUserName() + " " + Lang.get("Common.NotFound"));
			return false;
		}
		String oldPassword = user.getPassword();
		String oldBranch = user.getBranchInnerCode();
		user.setValue(dc);
		if ((AdminUserName.getValue().equalsIgnoreCase(user.getUserName()))
				&& (Enable.isDisable(user.getStatus()))) {
			Errorx.addError(AdminUserName.getValue() + " "
					+ Lang.get("User.CannotDisableAdmin"));
			return false;
		}
		user.setModifyTime(new Date());
		user.setModifyUser(User.getUserName());
		user.setRealName(LangUtil.getI18nFieldValue("RealName"));
		user.setPassword(oldPassword);
		trans.add(user, 2);

		CacheManager.set("Platform", "User", user.getUserName(), user);

		ZDUserRole userRole = new ZDUserRole();
		userRole.setUserName(user.getUserName());
		trans.add(userRole.query(), 5);
		CacheManager.set("Platform", "UserRole", user.getUserName(), "");

		String roleCodes = dc.getString("RoleCode");
		if (StringUtil.isEmpty(roleCodes)) {
			return true;
		}

		String currentUserName = User.getUserName();

		if ((StringUtil.isNotNull(oldBranch))
				&& (!oldBranch.equals(user.getBranchInnerCode()))
				&& (StringUtil.isNotNull(roleCodes))) {
			String[] codes = roleCodes.split(",");
			StringBuffer code = new StringBuffer();
			for (String s : codes) {
				code.append("'");
				code.append(s);
				code.append("'");
				code.append(",");
			}
			DAOSet<ZDRole> roleSet = new ZDRole()
					.query(new Q(" where RoleCode in ( "
							+ code.substring(0, code.length() - 1) + ")",
							new Object[0]));
			roleCodes = "";
			for (ZDRole role : roleSet)
				if (!oldBranch.equals(role.getBranchInnerCode())) {
					roleCodes = roleCodes + role.getRoleCode() + ",";
				}
		}
		String[] RoleCodes = roleCodes.split(",");
		for (int i = 0; i < RoleCodes.length; i++) {
			if ((!StringUtil.isEmpty(RoleCodes[i]))
					&& (!StringUtil.isEmpty(user.getUserName()))) {
				userRole = new ZDUserRole();
				userRole.setUserName(user.getUserName());
				userRole.setRoleCode(RoleCodes[i]);
				userRole.setAddTime(new Date());
				userRole.setAddUser(currentUserName);
				trans.add(userRole, 1);
			}
		}
		CacheManager.set("Platform", "UserRole", user.getUserName(), roleCodes);
		return true;
	}

	public static boolean add(Transaction trans, DataCollection dc) {
		String userName = dc.getString("UserName");
		if (!UserPattern.matcher(userName).matches()) {
			Errorx.addError(Lang.get("User.UserNameVerify"));
			return false;
		}
		ZDUser user = new ZDUser();
		user.setValue(dc);
		user.setUserName(user.getUserName().toLowerCase());
		if (user.fill()) {
			Errorx.addError(dc.getString("UserName")
					+ Lang.get("Common.Exists"));
			return false;
		}

		user.setPassword(PasswordUtil.generate(dc.getString("Password")));
		user.setType(dc.getString("Type"));
		user.setProp1(dc.getString("Prop1"));
		user.setAddTime(new Date());
		user.setAddUser(User.getUserName());
		user.setRealName(LangUtil.getI18nFieldValue("RealName"));
		trans.add(user, 1);

		ZDPrivilege priv = new ZDPrivilege();
		priv.setOwnerType("U");
		priv.setOwner(user.getUserName());
		priv.setAddTime(user.getAddTime());
		priv.setAddUser(user.getAddUser());
		trans.add(priv, 1);

		String roleCodes = dc.getString("RoleCode");
		if (StringUtil.isEmpty(roleCodes)) {
			return true;
		}
		String[] RoleCodes = roleCodes.split(",");
		String currentUserName = User.getUserName();

		CacheManager.set("Platform", "User", user.getUserName(), user);
		CacheManager.set("Platform", "UserRole", user.getUserName(), roleCodes);

		for (int i = 0; i < RoleCodes.length; i++)
			if ((!StringUtil.isEmpty(RoleCodes[i]))
					&& (!StringUtil.isEmpty(user.getUserName()))) {
				ZDUserRole userRole = new ZDUserRole();
				userRole.setUserName(user.getUserName());
				userRole.setRoleCode(RoleCodes[i]);
				userRole.setAddTime(new Date());
				userRole.setAddUser(currentUserName);
				trans.add(userRole, 1);
			}
		return true;
	}

	public static Privilege getUserPriv(String userName) {
		Privilege p = new Privilege();
		if (AdminUserName.getValue().equals(userName)) {
			for (AbstractMenuPriv priv : MenuPrivService.getInstance()
					.getAll()) {
				p.put(1, priv.getID());
				for (String privID : priv.getPrivItems().keyArray()) {
					p.put(1, privID);	
				}
			}
		} else {
			ZDPrivilege schema = new ZDPrivilege();
			schema.setOwnerType("U");
			schema.setOwner(userName);
			if (schema.fill()) {
				String priv = schema.getPrivs();
				p.parse(priv);
			}
			String roleCodes = PlatformCache.getUserRole(userName);
			for (String roleCode : StringUtil.splitEx(roleCodes, ","))
				if (!ObjectUtil.empty(roleCode)) {
					Privilege p2 = PlatformCache.getRolePriv(roleCode);
					p.union(p2);
				}
		}
		return p;
	}

	public static void login(ZDUser user) {
		String language = User.getLanguage();
		User.destory();
		User.setLanguage(language);
		User.setUserName(user.getUserName());
		User.setRealName(user.getRealName());
		User.setBranchInnerCode(user.getBranchInnerCode());
		User.setBranchAdministrator(YesOrNo.isYes(user.getIsBranchAdmin()));
		User.setType(user.getType());

		Mapx map = user.toMapx();
		map.remove("Password");
		User.getCurrent().putAll(map);

		ZDUserPreferences up = new ZDUserPreferences();
		up.setUserName(user.getUserName());
		if (up.fill()) {
			User.getCurrent().putAll(JSONUtil.toMap(up.getConfigProps()));
		}

		User.setLogin(true);
		User.setPrivilege(getUserPriv(User.getUserName()));

		user.setLastLoginIP(Current.getRequest().getClientIP());
		user.setLastLoginTime(new Date());
		user.update();

		ExtendManager.invoke("com.zving.platform.AfterLogin", new Object[0]);
	}
}
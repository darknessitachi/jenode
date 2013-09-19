package com.zving.platform.ui;

import com.zving.framework.Config;
import com.zving.framework.CookieData;
import com.zving.framework.Current;
import com.zving.framework.UIFacade;
import com.zving.framework.User;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.collection.Filter;
import com.zving.framework.collection.Mapx;
import com.zving.framework.core.handler.ZAction;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.i18n.Lang;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.security.PasswordUtil;
import com.zving.framework.security.PrivCheck;
import com.zving.framework.security.Privilege;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.ui.tag.ListAction;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.bl.EntrustBL;
import com.zving.platform.bl.LogBL;
import com.zving.platform.bl.UserBL;
import com.zving.platform.config.AdminUserName;
import com.zving.platform.service.MenuPrivService;
import com.zving.schema.ZDUser;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Alias("Application")
public class ApplicationUI extends UIFacade {
	@Priv
	public void init() {
		$S("Privs", StringUtil.javaEncode(UserBL
				.getUserPriv(User.getUserName()).toString()));
		$S("AdminUserName", AdminUserName.getValue());
		if (LangUtil.getSupportedLanguages().size() > 1) {
			$S("MultiLanguage", Boolean.valueOf(true));
		}

		if (ObjectUtil
				.notIn(new Object[] { User.getLanguage(), "en", "zh-cn" })) {
			if (User.getLanguage().equals("zh-tw"))
				$S("Language", "zh-cn");
			else
				$S("Language", "en");
		} else {
			$S("Language", User.getLanguage());
		}
		$S("AppName", Config.getAppName());
		$S("SessionID", User.getSessionID());
		if (EntrustBL.isAgent(User.getUserName())) {
			$S("Agent", Boolean.valueOf(true));
		}
		if (EntrustBL.isEntrust(User.getUserName()))
			$S("Entrust", Boolean.valueOf(true));
	}

	private static String getImgSpirite(Object oImgSrc) {
		String imgSrc = oImgSrc.toString();
		Matcher matcher = Pattern
				.compile("[^\"]*icons\\/([^\"\\/]+)\\.png", 34).matcher(imgSrc);
		String imgTag;
		if (matcher.find()) {
			String fileName = matcher.group(1);
			imgSrc = imgSrc.replaceAll(fileName, "icon000");
			imgTag = "<img src=\"" + imgSrc + "\" class=\"" + fileName
					+ "\" />";
		} else {
			imgTag = "<img src=\"" + imgSrc + "\" />";
		}
		return imgTag;
	}

	@Priv
	public void bindMainMenus(ListAction la) {
		DataTable dt = MenuPrivService.getMainMenus();
		dt.setWebMode(false);
		dt = dt.filter(new Filter<DataRow>() {
			public boolean filter(DataRow dr) {
				boolean flag = PrivCheck.check(dr.getString("id"));
				return flag;
			}
		});
		for (int i = 0; i < dt.getRowCount(); i++) {
			DataRow dr = dt.get(i);
			dr.set("icon", getImgSpirite(dr.get("icon")));
		}

		la.bindData(dt);
	}

	@Priv
	public void bindChildMenus(ListAction la) {
		String parentID = la.getParentCurrentDataRow().getString("ID");
		DataTable dt = MenuPrivService.getChildMenus(parentID);
		dt.setWebMode(false);
		dt = dt.filter(new Filter<DataRow>() {
			public boolean filter(DataRow dr) {
				return PrivCheck.check(dr.getString("id"));
			}
		});
		for (int i = 0; i < dt.getRowCount(); i++) {
			DataRow dr = dt.get(i);
			dr.set("icon", getImgSpirite(dr.get("icon")));
		}
		la.bindData(dt);
	}

	@Priv(login = false)
	public DataTable getLanguages() {
		return LangUtil.getSupportedLanguages().toDataTable();
	}

	@Priv(login = false)
	public void changeLanguage() {
		String lang = $V("Language");
		if (!LangUtil.getSupportedLanguages().containsKey(lang)) {
			return;
		}
		Current.getCookies().setCookie("_ZVING_LANGUAGE", lang,
				Config.getContextPath());
		User.setLanguage(lang);
	}

	@Priv
	public void changePassword() {
		if (!$V("Password").equals($V("ConfirmPassword"))) {
			fail("两次输入的密码不一致");
			return;
		}
		String userName = User.getUserName();
		ZDUser user = new ZDUser();
		user.setUserName(userName);
		user.fill();
		if (!PasswordUtil.verify($V("OldPassword"), user.getPassword())) {
			fail("原始密码输入错误");
			return;
		}
		user.setPassword(PasswordUtil.generate($V("Password")));
		if (user.update())
			success(Lang.get("Common.ExecuteSuccess"));
		else
			fail(Lang.get("Common.ExecuteFailed"));
	}

	@Priv(login = false)
	@Alias(value = "logout", alone = true)
	public void logout(ZAction za) {
		za.getRequest().getSession().invalidate();
		redirect(Config.getContextPath() + Config.getLoginPage());
	}

	@Priv
	public void changeMenuClickCount() {
		String menuID = $V("MenuID");
		if (StringUtil.isEmpty(menuID)) {
			return;
		}
		String message = "Visit " + menuID;
		LogBL.addLog(User.getUserName(), "UserLog", menuID, message);
	}

	@Priv
	public void addEntrust() {
		String agentUser = $V("UserName");
		if (StringUtil.isEmpty(agentUser)) {
			fail(Lang.get("Platform.HasNoUser"));
			return;
		}
		if (agentUser.equals(User.getUserName())) {
			fail(Lang.get("Platform.CannotEntrustSelf"));
			return;
		}
		EntrustBL.entrust(User.getUserName(), agentUser, new Date(), null);
		success(Lang.get("Common.ExecuteSuccess"));
	}

	@Priv
	public void cancelEntrust() {
		if (EntrustBL.cancel(User.getUserName()))
			success(Lang.get("Platform.CancelEntrustSuccess"));
		else
			fail(Lang.get("Common.ExecuteFailed"));
	}

	@Priv
	@Alias(value = "changeaccount", alone = true)
	public void changeLoginAccount(ZAction za) {
		String userName = $V("UserName").toLowerCase();
		String agentUser = User.getUserName();

		if (!EntrustBL.login(userName, agentUser)) {
			za.writeHTML("Change to entrusted account failed!");
			return;
		}
		LogBL.addLog(agentUser, "UserLog", "Entrust", "User " + agentUser
				+ " is now agent of " + userName);
		za.writeHTML("<script>window.location='" + Config.getContextPath()
				+ "application.zhtml';</script>");
	}

	@Priv
	public void bindEntrustUsers(DataGridAction dga) {
		List list = EntrustBL.getEntrustUsers(User.getUserName());
		DataTable dt = new DataTable();
		if (list != null) {
			String userNames = StringUtil.join(list, "','");
			dt = new Q(
					"select ZDUser.*,(select Name from ZDBranch where BranchInnerCode=ZDUser.BranchInnerCode) as BranchName from ZDUser where UserName in ('"
							+ userNames + "')", new Object[0]).fetch();
		}
		dga.setTotal(dt.getRowCount());
		dga.bindData(dt);
	}
}
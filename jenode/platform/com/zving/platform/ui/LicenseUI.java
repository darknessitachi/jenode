package com.zving.platform.ui;

import com.zving.framework.Config;
import com.zving.framework.UIFacade;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.i18n.Lang;
import com.zving.framework.security.LicenseInfo;
import com.zving.framework.security.PasswordUtil;
import com.zving.framework.utility.DateUtil;
import com.zving.framework.utility.FileUtil;
import com.zving.platform.config.AdminUserName;
import com.zving.schema.ZDUser;
import java.util.Date;

@Alias("License")
public class LicenseUI extends UIFacade
{
  @Priv(login=false)
  public void init()
  {
    $S("Customer", LicenseInfo.getName());
  }

  @Priv(login=false)
  public void getRequest() {
    String customer = $V("Customer");
    $S("Request", LicenseInfo.getLicenseRequest(customer));
  }

  @Priv(login=false)
  public void saveLicense() {
    String license = $V("License");
    String password = $V("Password");
    ZDUser user = new ZDUser();
    user.setUserName(AdminUserName.getValue());
    user.fill();
    if (!PasswordUtil.verify(password, user.getPassword())) {
      fail(Lang.get("Platform.AdminPasswordInvalid"));
      return;
    }
    if (LicenseInfo.verifyLicense(license)) {
      FileUtil.writeText(Config.getContextRealPath() + "WEB-INF/plugins/classes/license.dat", license);
      LicenseInfo.update();
      success(Lang.get("Common.SaveSuccess"));
    } else {
      fail(Lang.get("Platform.InvalidLicense"));
    }
  }

  public static boolean needWarning() {
    Date endDate = LicenseInfo.getEndDate();
    if (DateUtil.addDay(endDate, -30).getTime() < System.currentTimeMillis()) {
      return true;
    }
    return false;
  }

  public static boolean nearEndDateWarning() {
    Date endDate = LicenseInfo.getEndDate();
    if (DateUtil.addDay(endDate, -7).getTime() < System.currentTimeMillis()) {
      return true;
    }
    return false;
  }
}
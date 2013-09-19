package com.zving.platform.bl;

import com.zving.framework.Config;
import com.zving.framework.User;
import com.zving.framework.i18n.Lang;
import com.zving.framework.json.JSONObject;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.utility.DateUtil;
import com.zving.framework.utility.Errorx;
import com.zving.platform.code.Enable;
import com.zving.platform.config.AdminUserName;
import com.zving.schema.ZDUser;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EntrustBL
{
  public static final String ID = "com.zving.platform.Entrust";

  public static void entrust(String fromUser, String toUser, Date start, Date end)
  {
    JSONObject jo = new JSONObject();
    jo.put("User", toUser);
    jo.put("StartTime", DateUtil.toDateTimeString(start));
    jo.put("EndTime", end == null ? null : DateUtil.toDateTimeString(end));
    PreferenceBL.save(fromUser, "com.zving.platform.Entrust", "To", jo);

    JSONObject toMap = (JSONObject)PreferenceBL.get(toUser, "com.zving.platform.Entrust", "From");
    if (toMap == null) {
      toMap = new JSONObject();
    }
    jo.put("StartTime", DateUtil.toDateTimeString(start));
    jo.put("EndTime", end == null ? null : DateUtil.toDateTimeString(end));
    toMap.put(fromUser, jo);
    PreferenceBL.save(toUser, "com.zving.platform.Entrust", "From", toMap);
  }

  public static boolean login(String userName, String agentUser)
  {
    JSONObject jo = (JSONObject)PreferenceBL.get(userName, "com.zving.platform.Entrust", "To");
    if ((jo == null) || (!agentUser.equals(jo.getString("User")))) {
      return false;
    }
    String endTime = jo.getString("EndTime");
    if ((endTime != null) && (DateUtil.compare(DateUtil.getCurrentDateTime(), endTime) >= 0)) {
      return false;
    }
    userName = userName.toLowerCase();
    ZDUser user = new ZDUser();
    user.setUserName(userName);
    DAOSet userSet = user.query();
    if ((!Config.isAllowLogin) && (!user.getUserName().equalsIgnoreCase(AdminUserName.getValue()))) {
      Errorx.addError(Lang.get("User.DenyLoginTemp"));
      return false;
    }

    if ((userSet != null) && (userSet.size() > 0)) {
      user = (ZDUser)userSet.get(0);
      if ((!AdminUserName.getValue().equalsIgnoreCase(user.getUserName())) && (Enable.isDisable(user.getStatus()))) {
        Errorx.addError(Lang.get("User.UserStopped"));
        return false;
      }
      User.destory();
      UserBL.login(user);
      User.setValue("_ZVING_ENTRUSTING", "Y");
      return true;
    }
    return false;
  }

  public static boolean isEntrust(String userName) {
    Object obj = PreferenceBL.get(userName, "com.zving.platform.Entrust", "To");
    if (obj == null) {
      return false;
    }
    JSONObject jo = (JSONObject)obj;
    return jo.size() != 0;
  }

  public static boolean isAgent(String userName) {
    Object obj = PreferenceBL.get(userName, "com.zving.platform.Entrust", "From");
    if (obj == null) {
      return false;
    }
    JSONObject jo = (JSONObject)obj;
    return jo.size() != 0;
  }

  public static List<String> getEntrustUsers(String userName) {
    Object obj = PreferenceBL.get(userName, "com.zving.platform.Entrust", "From");
    if (obj == null) {
      return null;
    }
    JSONObject jo = (JSONObject)obj;
    List list = new ArrayList();
    for (String user : jo.keyArray()) {
      JSONObject times = (JSONObject)jo.get(user);
      String endTime = times.getString("EndTime");
      if ((endTime == null) || (DateUtil.compare(DateUtil.getCurrentDateTime(), endTime) < 0))
      {
        list.add(user);
      }
    }
    return list;
  }

  public static boolean cancel(String userName) {
    Object obj = PreferenceBL.get(userName, "com.zving.platform.Entrust", "To");
    if (obj == null) {
      return true;
    }
    JSONObject jo = (JSONObject)obj;
    PreferenceBL.remove(userName, "com.zving.platform.Entrust", "To");

    String toUser = jo.getString("User");
    obj = PreferenceBL.get(toUser, "com.zving.platform.Entrust", "From");
    if (obj == null) {
      return true;
    }
    jo = (JSONObject)obj;
    jo.remove(userName);
    if (jo.size() == 0)
      PreferenceBL.remove(toUser, "com.zving.platform.Entrust", "From");
    else {
      PreferenceBL.save(toUser, "com.zving.platform.Entrust", "From", jo);
    }
    return true;
  }
}
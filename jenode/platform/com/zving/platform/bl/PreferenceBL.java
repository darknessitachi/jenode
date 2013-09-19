package com.zving.platform.bl;

import com.zving.framework.User;
import com.zving.framework.json.JSON;
import com.zving.framework.json.JSONObject;
import com.zving.schema.ZDUserPreferences;
import java.util.Date;

public class PreferenceBL
{
  public static final String KEY = "_ZVING_USERPREFERCES";

  public static void updateCurrent()
  {
    ZDUserPreferences up = new ZDUserPreferences();
    up.setUserName(User.getUserName());
    up.fill();
    JSONObject jo = (JSONObject)JSON.parse(up.getConfigProps());
    User.setValue("_ZVING_USERPREFERCES", jo);
  }

  public static String toString(String userName) {
    return getAll(userName).toJSONString();
  }

  public static JSONObject getAll(String userName) {
    ZDUserPreferences up = new ZDUserPreferences();
    up.setUserName(userName);
    if ((User.getUserName() != null) && (User.getUserName().equals(userName))) {
      Object obj = User.getValue("_ZVING_USERPREFERCES");
      if (obj != null) {
        return (JSONObject)obj;
      }
      up.fill();
      JSONObject jo = (JSONObject)JSON.parse(up.getConfigProps());
      User.setValue("_ZVING_USERPREFERCES", jo);
      return jo;
    }

    up.fill();
    JSONObject jo = (JSONObject)JSON.parse(up.getConfigProps());
    return jo;
  }

  public static JSONObject get(String userName, String typeID)
  {
    JSONObject ps = getAll(userName);
    if (ps == null) {
      return null;
    }
    return (JSONObject)ps.get(typeID);
  }

  public static Object remove(String userName, String typeID, String keyID) {
    JSONObject obj = get(userName, typeID);
    if (obj == null) {
      return null;
    }
    Object r = obj.remove(keyID);
    save(userName, typeID, obj);
    return r;
  }

  public static Object get(String userName, String typeID, String keyID) {
    JSONObject obj = get(userName, typeID);
    if (obj == null) {
      return null;
    }
    return obj.get(keyID);
  }

  public static boolean save(String userName, String typeID, String keyID, Object value) {
    JSONObject all = getAll(userName);
    if (all == null) {
      all = new JSONObject();
    }
    JSONObject jo = (JSONObject)all.get(typeID);
    if (jo == null) {
      jo = new JSONObject();
      all.put(typeID, jo);
    }
    jo.put(keyID, value);
    ZDUserPreferences up = new ZDUserPreferences();
    up.setUserName(userName);
    if (up.fill()) {
      up.setConfigProps(JSON.toJSONString(all));
      up.setModifyUser(userName);
      up.setModifyTime(new Date());
      return up.update();
    }
    up.setConfigProps(JSON.toJSONString(all));
    up.setAddUser(userName);
    up.setAddTime(new Date());
    return up.insert();
  }

  public static boolean save(String userName, String typeID, JSONObject jo)
  {
    JSONObject all = getAll(userName);
    if (all == null) {
      all = new JSONObject();
    }
    all.put(typeID, jo);
    ZDUserPreferences up = new ZDUserPreferences();
    up.setUserName(userName);
    if (up.fill()) {
      up.setConfigProps(JSON.toJSONString(all));
      return up.update();
    }
    up.setConfigProps(JSON.toJSONString(all));
    return up.insert();
  }
}
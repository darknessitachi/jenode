package com.zving.platform.pub;

import com.zving.framework.cache.CacheDataProvider;
import com.zving.framework.cache.CacheManager;
import com.zving.framework.collection.Mapx;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.security.Privilege;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.meta.MetaModel;
import com.zving.schema.ZDBranch;
import com.zving.schema.ZDPrivilege;
import com.zving.schema.ZDRole;
import com.zving.schema.ZDUser;
import com.zving.schema.ZDUserRole;

public class PlatformCache extends CacheDataProvider
{
  public static final String ProviderID = "Platform";
  public static final String Type_UserRole = "UserRole";
  public static final String Type_User = "User";
  public static final String Type_Role = "Role";
  public static final String Type_Branch = "Branch";
  public static final String Type_MetaModel = "MetaModel";
  public static final String Type_MetaModel_Code = "MetaModel_Code";
  public static final String Type_RolePriv = "RolePriv";
  public static final String Type_BranchPriv = "BranchPriv";

  public String getID()
  {
    return "Platform";
  }

  public String getName() {
    return "平台缓存";
  }

  public void onKeyNotFound(String type, String key) {
    if (type.equals("UserRole")) {
      ZDUserRole schema = new ZDUserRole();
      schema.setUserName(key.toString());
      DAOSet set = schema.query();
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < set.size(); i++) {
        if (i != 0) {
          sb.append(",");
        }
        sb.append(((ZDUserRole)set.get(i)).getRoleCode());
      }
      if (set.size() > 0)
        CacheManager.set("Platform", type, key, sb.toString());
      else {
        CacheManager.set("Platform", type, key, "");
      }
    }
    if (type.equals("User")) {
      ZDUser schema = new ZDUser();
      schema.setUserName(key.toString());
      if (schema.fill()) {
        CacheManager.set("Platform", type, key, schema);
      }
    }
    if (type.equals("Branch")) {
      ZDBranch schema = new ZDBranch();
      schema.setBranchInnerCode(key.toString());
      if (schema.fill()) {
        CacheManager.set("Platform", type, key, schema);
      }
    }
    if (type.equals("MetaModel")) {
      MetaModel mm = MetaModel.load(Long.parseLong(key));
      if (mm != null) {
        CacheManager.set("Platform", type, Long.parseLong(key), mm);
      }
    }
    if (type.equals("MetaModel_Code")) {
      MetaModel mm = MetaModel.load(key);
      if (mm != null) {
        CacheManager.set("Platform", type, key, mm);
      }
    }
    if (type.equals("RolePriv")) {
      ZDPrivilege schema = new ZDPrivilege();
      schema.setOwnerType("R");
      schema.setOwner(key);
      if (schema.fill()) {
        Privilege p = new Privilege();
        p.parse(schema.getPrivs());
        CacheManager.set("Platform", type, key, p);
      }
    }
    if (type.equals("BranchPriv")) {
      ZDPrivilege schema = new ZDPrivilege();
      schema.setOwnerType("B");
      schema.setOwner(key);
      if (schema.fill()) {
        Privilege p = new Privilege();
        p.parse(schema.getPrivs());
        CacheManager.set("Platform", type, key, p);
      }
    }
  }

  public void onTypeNotFound(String type)
  {
    if (type.equals("User")) {
      CacheManager.setMapx("Platform", type, new Mapx(10000));
    }
    if (type.equals("MetaModel")) {
      CacheManager.setMapx("Platform", type, new Mapx(10000));
    }
    if (type.equals("MetaModel_Code")) {
      CacheManager.setMapx("Platform", type, new Mapx(10000));
    }
    if (type.equals("Role")) {
      DAOSet set = new ZDRole().query();
      for (int i = 0; i < set.size(); i++) {
        CacheManager.set("Platform", type, ((ZDRole)set.get(i)).getRoleCode(), set.get(i));
      }
    }

    if (type.equals("UserRole")) {
      CacheManager.setMapx("Platform", type, new Mapx(10000));
    }
    if (type.equals("Branch")) {
      DAOSet set = new ZDBranch().query();
      for (int i = 0; i < set.size(); i++) {
        CacheManager.set("Platform", type, ((ZDBranch)set.get(i)).getBranchInnerCode(), set.get(i));
      }
    }
    if (type.equals("RolePriv")) {
      ZDPrivilege schema = new ZDPrivilege();
      schema.setOwnerType("R");
      DAOSet set = schema.query();
      for (ZDPrivilege schema2 : set) {
        Privilege p = new Privilege();
        p.parse(schema2.getPrivs());
        CacheManager.set("Platform", type, schema2.getOwner(), p);
      }
    }
    if (type.equals("BranchPriv")) {
      ZDPrivilege schema = new ZDPrivilege();
      schema.setOwnerType("B");
      DAOSet set = schema.query();
      for (ZDPrivilege schema2 : set) {
        Privilege p = new Privilege();
        p.parse(schema2.getPrivs());
        CacheManager.set("Platform", type, schema2.getOwner(), p);
      }
    }
  }

  public static MetaModel getMetaModel(long id) {
    return (MetaModel)CacheManager.get("Platform", "MetaModel", id);
  }

  public static MetaModel getMetaModel(String modelCode) {
    return (MetaModel)CacheManager.get("Platform", "MetaModel_Code", modelCode);
  }

  public static Privilege getRolePriv(String roleCode) {
    return (Privilege)CacheManager.get("Platform", "RolePriv", roleCode);
  }

  public static Privilege getBranchPriv(String branchInnerCode) {
    return (Privilege)CacheManager.get("Platform", "BranchPriv", branchInnerCode);
  }

  public static ZDUser getUser(String userName) {
    return (ZDUser)CacheManager.get("Platform", "User", userName);
  }

  public static ZDRole getRole(String roleCode) {
    return (ZDRole)CacheManager.get("Platform", "Role", roleCode);
  }

  public static String getUserRole(String userName) {
    return (String)CacheManager.get("Platform", "UserRole", userName);
  }

  public static ZDBranch getBranch(String innerCode) {
    if (StringUtil.isEmpty(innerCode)) {
      innerCode = "0001";
    }
    return (ZDBranch)CacheManager.get("Platform", "Branch", innerCode);
  }

  public static void removeRole(String roleCode) {
    CacheManager.remove("Platform", "Role", roleCode);
    Mapx map = CacheManager.getMapx("Platform", "UserRole");
    synchronized (map) {
      roleCode = "," + roleCode + ",";
      for (String key : map.keyArray()) {
        String ur = "," + map.get(key) + ",";
        if (ur.indexOf(roleCode) >= 0) {
          ur = StringUtil.replaceEx(ur, roleCode, ",");
        }
        ur = ur.substring(0, ur.length() - 1);
        map.put(key, ur);
      }
    }
  }

  public static void addUserRole(String userName, String roleCode) {
    String roles = (String)CacheManager.get("Platform", "UserRole", userName);
    if (StringUtil.isEmpty(roles))
      CacheManager.set("Platform", "UserRole", userName, roleCode);
    else
      CacheManager.set("Platform", "UserRole", userName, roles + "," + roleCode);
  }

  public static void removeUserRole(String userName, String roleCode)
  {
    String roles = (String)CacheManager.get("Platform", "UserRole", userName);
    if (StringUtil.isEmpty(roles)) {
      return;
    }
    String ur = "," + roles + ",";
    if (ur.indexOf(roleCode) >= 0) {
      ur = StringUtil.replaceEx(ur, roleCode, ",");
    }
    ur = ur.substring(0, ur.length() - 1);
    CacheManager.set("Platform", "UserRole", userName, ur);
  }
}
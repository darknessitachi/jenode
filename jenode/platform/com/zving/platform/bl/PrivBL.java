package com.zving.platform.bl;

import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.ResponseData;
import com.zving.framework.User;
import com.zving.framework.cache.CacheManager;
import com.zving.framework.collection.Filter;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.Q;
import com.zving.framework.data.Transaction;
import com.zving.framework.i18n.Lang;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.security.Privilege;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.config.AdminUserName;
import com.zving.platform.pub.PlatformCache;
import com.zving.schema.ZDBranch;
import com.zving.schema.ZDPrivilege;
import com.zving.schema.ZDRole;
import com.zving.schema.ZDUser;
import java.util.Date;

public class PrivBL
{
  public static boolean hasBranchPriv(String type, String id, String priv)
  {
    return false;
  }

  public static Privilege getPrivilege(String type, String id)
  {
    Privilege p = new Privilege();
    if (("R".equals(type)) && (ObjectUtil.notEmpty(id))) {
      p = PlatformCache.getRolePriv(id);
    }
    if (("B".equals(type)) && (ObjectUtil.notEmpty(id))) {
      p = PlatformCache.getBranchPriv(id);
    }
    if (("U".equals(type)) && (ObjectUtil.notEmpty(id))) {
      ZDPrivilege schema = new ZDPrivilege();
      schema.setOwnerType("U");
      schema.setOwner(id);
      if (schema.fill()) {
        String priv = schema.getPrivs();
        p.parse(priv);
      }
      String roleCodes = PlatformCache.getUserRole(id);
      for (String code : StringUtil.splitEx(roleCodes, ",")) {
        if (!ObjectUtil.empty(code))
        {
          Privilege p2 = PlatformCache.getRolePriv(code);
          if (p2 != null)
            p.union(p2);
        }
      }
    }
    if (p == null) {
      p = new Privilege();
    }
    return p;
  }

  public static String getInitScript(String type, String id)
  {
    Privilege p = getPrivilege(type, id);
    StringBuilder sb = new StringBuilder();
    sb.append(getPrivilegeScript(p));
    sb.append(getUncheckableScript(type, id));
    if (getFullPrivFlag(type, id)) {
      sb.append("_FullPrivFlag = true;\n");
    }
    return sb.toString();
  }

  public static String getPrivilegeScript(Privilege p)
  {
    StringBuilder sb = new StringBuilder();
    for (String key : p.keySet()) {
      if (1 == ((Integer)p.get(key)).intValue()) {
        sb.append("PrivMap[\"" + key + "\"] = 1;");
      }
    }
    return sb.toString();
  }

  public static String getUncheckableScript(String type, String id)
  {
    StringBuilder sb = new StringBuilder();
    if (("U".equals(type)) && (ObjectUtil.notEmpty(id)))
    {
      String roleCodes = PlatformCache.getUserRole(id);
      for (String code : StringUtil.splitEx(roleCodes, ",")) {
        Privilege p2 = PlatformCache.getRolePriv(code);
        if (p2 != null) {
          for (String key : p2.keySet()) {
            if (1 == ((Integer)p2.get(key)).intValue()) {
              sb.append("UncheckableMap[\"" + key + "\"] = 1;");
            }
          }
        }
      }
    }
    return sb.toString();
  }

  public static boolean getFullPrivFlag(String type, String id)
  {
    boolean fullPrivFlag = false;
    if ("U".equals(type)) {
      fullPrivFlag = (fullPrivFlag) || (AdminUserName.getValue().equals(id));
    }
    if ("B".equals(type)) {
      fullPrivFlag = (fullPrivFlag) || (PlatformCache.getBranch(id).getTreeLevel() == 1L);
    }
    return fullPrivFlag;
  }

  public static ZDPrivilege getDAO(String type, String id)
  {
    ZDPrivilege schema = new ZDPrivilege();
    schema.setOwnerType(type);
    schema.setOwner(id);
    return schema;
  }

  public static ZDBranch getBranch(String type, String id)
  {
    String branchInnerCode = null;
    if (("R".equals(type)) && (ObjectUtil.notEmpty(id))) {
      branchInnerCode = PlatformCache.getRole(id).getBranchInnerCode();
    }
    if (("B".equals(type)) && (ObjectUtil.notEmpty(id))) {
      branchInnerCode = id.substring(0, id.length() - 4);
    }
    if (("U".equals(type)) && (ObjectUtil.notEmpty(id))) {
      ZDUser user = new ZDUser();
      user.setUserName(id);
      if (user.fill()) {
        branchInnerCode = user.getBranchInnerCode();
      }
    }
    return PlatformCache.getBranch(branchInnerCode);
  }

  public static Privilege getBranchPrivilege(String type, String id)
  {
    Privilege p = new Privilege();
    ZDBranch b = getBranch(type, id);
    if ((b != null) && (b.getTreeLevel() > 1L)) {
      p = PlatformCache.getBranchPriv(b.getBranchInnerCode());
    }
    if (p == null) {
      p = new Privilege();
    }
    return p;
  }

  private static void initCurrent(String type, String id)
  {
    Privilege p = getPrivilege(type, id);
    Current.put("_CurrentPriv", p);
    Current.put("_CurrentBranchPriv", getBranchPrivilege(type, id));
    Current.put("_FullPrivFlag", Boolean.valueOf(getFullPrivFlag(type, id)));
    ZDBranch branch = getBranch(type, id);
    boolean flag = false;
    if (branch != null) {
      flag = getFullPrivFlag("B", branch.getBranchInnerCode());
    }
    Current.put("_BranchFullPrivFlag", Boolean.valueOf(flag));
  }

  public static boolean isInBranchPrivRange(String type, String id, String item)
  {
    Privilege p = (Privilege)Current.get("_CurrentBranchPriv");
    if (p == null) {
      initCurrent(type, id);
    }
    if (((Boolean)Current.get("_BranchFullPrivFlag")).booleanValue()) {
      return true;
    }
    if ((p != null) && (p.hasPriv(item))) {
      return true;
    }
    return false;
  }

  public static Privilege getCurrentPrivilege(String type, String id)
  {
    Privilege p = (Privilege)Current.get("_CurrentPriv");
    if (p == null) {
      initCurrent(type, id);
    }
    p = (Privilege)Current.get("_CurrentPriv");
    if (p == null) {
      p = new Privilege();
    }
    return p;
  }

  public static void save(RequestData request, ResponseData response, Filter<String> filter)
  {
    String id = request.getString("ID");
    String type = request.getString("Type");

    Mapx map = (Mapx)request.get("Data");
    Transaction trans = new Transaction();
    Privilege p = new Privilege();
    for (String key : map.keySet()) {
      if (map.getInt(key) == 1)
      {
        p.put(1, key);
      }
    }
    ZDPrivilege schema = getDAO(type, id);
    DAOSet set = new DAOSet();
    Q wherePart;
    if (schema.fill()) {
      Privilege p2 = new Privilege();
      p2.parse(schema.getPrivs());

      for (String key : p2.keyArray()) {
        if ((filter.filter(key)) || (!isInBranchPrivRange(type, id, key))) {
          p2.remove(key);
        }
        if ((map.containsKey(key)) && (map.getInt(key) == 0)) {
          p2.remove(key);
        }
      }
      p.union(p2);
      schema.setPrivs(p.toString());
      schema.setModifyTime(new Date());
      schema.setModifyUser(User.getUserName());
      trans.backup(schema);
      trans.update(schema);
      set.add(schema);

      if ("B".equals(type)) {
        wherePart = new Q("where ", new Object[0]);

        wherePart.append(" (OwnerType=? ", new Object[] { type });
        wherePart.append(" and Owner like ?  and Owner<>?)", new Object[] { id + "%", id });

        wherePart.append("or exists (select 1 from ZDRole where BranchInnerCode like ? and ZDPrivilege.OwnerType=? and ZDPrivilege.Owner=RoleCode)", new Object[] { 
          id + "%", "R" });

        wherePart.append("or exists (select 1 from ZDUser where BranchInnerCode like ? and ZDPrivilege.OwnerType=? and ZDPrivilege.Owner=UserName)", new Object[] { 
          id + "%", "U" });
        DAOSet childSet = new ZDPrivilege().query(wherePart);
        for (ZDPrivilege child : childSet) {
          Privilege priv = new Privilege();
          priv.parse(child.getPrivs());
          for (String key : priv.keyArray()) {
            if ((map.containsKey(key)) && (map.getInt(key) == 0)) {
              priv.remove(key);
            }
          }
          child.setPrivs(priv.toString());
          child.setModifyTime(new Date());
          child.setModifyUser(User.getUserName());
        }
        trans.backup(childSet);
        trans.update(childSet);
        set.add(childSet);
      }
    } else {
      trans.insert(schema);
      schema.setAddTime(new Date());
      schema.setPrivs(p.toString());
      schema.setAddUser(User.getUserName());
      set.add(schema);
    }
    if (trans.commit()) {
      for (ZDPrivilege zdDAO : set) {
        if ("R".equals(zdDAO.getOwnerType()))
          CacheManager.remove("Platform", "RolePriv", zdDAO.getOwner());
        else if ("B".equals(zdDAO.getOwnerType())) {
          CacheManager.remove("Platform", "BranchPriv", zdDAO.getOwner());
        }
      }
      response.setSuccessMessage(Lang.get("Common.SaveSuccess"));
    } else {
      response.setFailedMessage(Lang.get("Common.SaveFailed") + trans.getExceptionMessage());
    }
  }
}
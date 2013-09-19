package com.zving.platform.pub;

import com.zving.framework.Config;
import com.zving.framework.cache.CacheManager;
import com.zving.framework.collection.Filter;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.utility.HtmlUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.config.AdminUserName;
import com.zving.schema.ZDBranch;
import com.zving.schema.ZDRole;
import com.zving.schema.ZDUser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PlatformUtil
{
  public static final String INDENT = "　";
  public static final String SEPARATE = "|";
  public static DataTable District = null;

  private static Mapx<String, String> DistrictMap = null;

  private static Object mutex = new Object();

  public static Mapx<String, Object> getCodeMap(String codeType)
  {
    return HtmlUtil.codeToMapx(codeType);
  }

  public static String getFileIcon(String path) {
    if (path.endsWith("/")) {
      return "framework/images/filetype/folder.gif";
    }
    if (path.endsWith(".template.html")) {
      return "icons/extra/icon_template.gif";
    }
    if (path.indexOf(".") > 0) {
      String ext = path.substring(path.lastIndexOf(".") + 1);
      if ((ext.equals("htm")) || (ext.equals("shtml"))) {
        ext = "html";
      }
      if (ext.equals("jpeg")) {
        ext = "jpg";
      }
      if (ext.equals("pptx")) {
        ext = "ppt";
      }
      if (ext.equals("xlsx")) {
        ext = "xls";
      }
      if (ext.equals("jar")) {
        ext = "zip";
      }
      if (ext.equals("rar")) {
        ext = "zip";
      }
      if (ObjectUtil.in(new Object[] { ext, "", "asp", "aspx", "avi", "bmp", "doc", "docx", "exe", "fla", "flv", "folder", "gif", "html", "jpg", 
        "js", "jsp", "mdb", "mov", "mp3", "mp4", "pdf", "php", "png", "ppt", "rar", "rm", "swf", "txt", "wmp", "wmv", 
        "xls", "zip" })) {
        return "framework/images/filetype/" + ext + ".gif";
      }
    }

    return "framework/images/filetype/unknown.gif";
  }

  public static String getFileIconBySuffix(String suffix) {
    String ext = suffix;
    if (StringUtil.isNull(ext)) {
      return "framework/images/filetype/unknown.gif";
    }
    if ((ext.equals("htm")) || (ext.equals("shtml"))) {
      ext = "html";
    }
    if (ext.equals("jpeg")) {
      ext = "jpg";
    }
    if (ext.equals("pptx")) {
      ext = "ppt";
    }
    if (ext.equals("xlsx")) {
      ext = "xls";
    }
    if (ext.equals("jar")) {
      ext = "zip";
    }
    if (ext.equals("rar")) {
      ext = "zip";
    }
    if (ObjectUtil.in(new Object[] { ext, "", "asp", "aspx", "avi", "bmp", "doc", "docx", "exe", "fla", "flv", "folder", "gif", "html", "jpg", "js", 
      "jsp", "mdb", "mov", "mp3", "mp4", "pdf", "php", "png", "ppt", "rar", "rm", "swf", "txt", "wmp", "wmv", "xls", "zip" })) {
      return "framework/images/filetype/" + ext + ".gif";
    }
    return "framework/images/filetype/unknown.gif";
  }

  public static String getUserRealName(String userName) {
    if (ObjectUtil.empty(userName)) {
      return "";
    }
    ZDUser user = PlatformCache.getUser(userName);
    if (user == null) {
      return "";
    }
    return user.getRealName();
  }

  public static List<String> getRoleCodesByUserName(String userName)
  {
    String roles = (String)CacheManager.get("Platform", "UserRole", userName);
    if (roles == null) {
      return null;
    }
    String[] arr = roles.split(",");
    Set set = new TreeSet();
    for (int i = 0; i < arr.length; i++) {
      set.add(arr[i]);
    }
    ArrayList list = new ArrayList();
    if (set.size() > 0) {
      Iterator iterator = set.iterator();
      while (iterator.hasNext()) {
        String code = (String)iterator.next();
        if (StringUtil.isNotEmpty(code)) {
          list.add(code);
        }
      }
      return list;
    }
    return null;
  }

  public static String getRoleName(String roleCode) {
    ZDRole role = (ZDRole)CacheManager.get("Platform", "Role", roleCode);
    if (role == null) {
      return null;
    }
    return LangUtil.decode(role.getRoleName());
  }

  public static ZDBranch getBranch(String innerCode) {
    return PlatformCache.getBranch(innerCode);
  }

  public static String getBranchName(String innerCode) {
    ZDBranch branch = PlatformCache.getBranch(innerCode);
    if (branch == null) {
      branch = PlatformCache.getBranch("0001");
    }
    if (branch != null) {
      return branch.getName();
    }
    return null;
  }

  public static String getRoleNames(List<String> roleCodes) {
    if ((roleCodes == null) || (roleCodes.size() == 0)) {
      return "";
    }
    StringBuffer sb = new StringBuffer();
    boolean first = false;
    for (int i = 0; i < roleCodes.size(); i++) {
      String roleName = getRoleName((String)roleCodes.get(i));
      if (StringUtil.isNotEmpty(roleName)) {
        if (first) {
          sb.append(",");
        }
        sb.append(roleName);
        first = true;
      }
    }
    return sb.toString();
  }

  public static void indentDataTable(DataTable dt)
  {
    indentDataTable(dt, 0, 2, 1);
  }

  public static void indentDataTable(DataTable dt, int n, int m, int firstLevel)
  {
    for (int i = 0; i < dt.getRowCount(); i++) {
      int level = Integer.parseInt(dt.getString(i, m));
      StringBuffer sb = new StringBuffer();
      for (int j = firstLevel; j < level; j++) {
        sb.append("　");
      }
      dt.set(i, n, sb.toString() + dt.getString(i, n));
    }
  }

  public static void initDistrict()
  {
    District = new Q("select Name,Code,TreeLevel,Type from zddistrict", new Object[0]).fetch();
    Mapx map = new Mapx();
    for (int i = 0; i < District.getRowCount(); i++) {
      map.put(District.getString(i, 1), District.getString(i, 0));
    }
    DistrictMap = map;
  }

  public static Mapx<String, String> getDistrictMap() {
    if (DistrictMap == null) {
      synchronized (mutex) {
        if (DistrictMap == null) {
          initDistrict();
        }
      }
    }
    return DistrictMap;
  }

  public static DataTable getProvince() {
    if (District == null) {
      initDistrict();
    }
    return District.filter(new Filter() {
      public boolean filter(DataRow dr) {
        if ("1".equals(dr.get("TreeLevel"))) {
          return true;
        }
        return false;
      }
    });
  }

  public static void loadDBConfig()
  {
    synchronized (mutex) {
      if ((Config.getMapx().containsKey("Database.Default.Type")) && 
        ("true".equals(Config.getMapx().get("App.ExistPlatformDB"))))
        try {
          DataTable dt = new Q("select code,value from zdconfig", new Object[0]).fetch();
          for (int i = 0; (dt != null) && (i < dt.getRowCount()); i++) {
            Config.getMapx().put(dt.getString(i, 0), dt.getString(i, 1));
          }
          Config.getMapx().put("AdminUserName", AdminUserName.getValue());
        } catch (Exception e) {
          e.printStackTrace();
        }
    }
  }

  public static void refresh()
  {
    loadDBConfig();
  }
}
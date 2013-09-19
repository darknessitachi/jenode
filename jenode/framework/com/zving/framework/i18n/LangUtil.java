package com.zving.framework.i18n;

import com.zving.framework.Config;
import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.User;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataColumn;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class LangUtil
{
  public static final String LangFieldPrefix = "@Lang\n";
  private static String[] LangIDArr = { "ar-", "be-", "bg-", "ca-", "cs-", "da-", "de-", "el-", "en-", "es-", "et-", "fi-", 
    "fr-", "hr-", "hu-", "is-", "it-", "iw-", "ja-", "ko-", "lt-", "lv-", "mk-", "nl-", "no-", "pl-", "pt-", "ro-", "ru-", "sh-", 
    "sk-", "sl-", "sq-", "sr-", "sv-", "th-", "tr-", "uk-" };

  public static String getLanguage(HttpServletRequest request)
  {
    String path = Config.getContextPath();
    if (path != null) {
      if (path.startsWith("/")) {
        path = path.substring(1);
      }
      if (path.endsWith("/")) {
        path = path.substring(0, path.length() - 1);
      }
    }
    if (request.getCookies() != null) {
      for (Cookie c : request.getCookies()) {
        if (c.getName().equals("_ZVING_LANGUAGE"))
        {
          String path2 = c.getPath();
          if (path2 != null) {
            if (path2.startsWith("/")) {
              path2 = path2.substring(1);
            }
            if (path2.endsWith("/")) {
              path2 = path2.substring(0, path2.length() - 1);
            }
            if (path2.equals(path)) {
              return c.getValue();
            }
          }
          else if (ObjectUtil.empty(path2)) {
            return c.getValue();
          }
        }
      }
    }
    String lang = request.getHeader("Accept-Language");
    lang = getLanguage(lang);
    return lang;
  }

  public static String getLanguage(String lang)
  {
    if ((lang != null) && (lang.indexOf(",") > 0)) {
      lang = lang.substring(0, lang.indexOf(",")).trim();
    }
    if (lang == null) {
      return LangMapping.getInstance().DefaultLanguage;
    }
    lang = lang.replace('_', '-').toLowerCase();
    if (!lang.endsWith("-")) {
      lang = lang + "-";
    }
    if (lang.startsWith("zh-")) {
      if ((lang.equals("zh-tw")) || (lang.equals("zh-sg"))) {
        return "zh-tw";
      }
      return "zh-cn";
    }

    for (String id : LangIDArr) {
      if (lang.startsWith(id)) {
        return id.substring(0, id.length() - 1);
      }
    }

    return LangMapping.getInstance().DefaultLanguage;
  }

  public static void decode(DataTable dt, String column)
  {
    decode(dt, column, User.getLanguage());
  }

  public static void decode(DataTable dt, String column, String lang)
  {
    if (dt == null) {
      return;
    }
    for (DataRow dr : dt) {
      String str = decode(dr.getString(column), lang);
      dr.set(column, str);
    }
  }

  public static void decode(Mapx<String, Object> map)
  {
    decode(map, User.getLanguage());
  }

  public static void decode(Mapx<String, Object> map, String lang)
  {
    if (map == null) {
      return;
    }
    for (String key : map.keyArray())
      map.put(key, decode(map.getString(key), lang));
  }

  public static String decode(String src)
  {
    return decode(src, User.getLanguage());
  }

  public static String get(String str)
  {
    return get(str, User.getLanguage());
  }

  public static String get(String str, String lang)
  {
    if (ObjectUtil.empty(str)) {
      return str;
    }
    if (ObjectUtil.empty(lang)) {
      lang = LangMapping.getInstance().DefaultLanguage;
    }
    return decode(str, lang);
  }

  public static String decode(String str, String lang)
  {
    if (str == null) {
      return str;
    }
    if (ObjectUtil.empty(lang)) {
      return str;
    }
    str = StringUtil.replaceEx(str, "\r\n", "\n");
    if ((str.startsWith("@{")) && (str.endsWith("}"))) {
      return LangMapping.get(lang, str);
    }
    if (!str.startsWith("@Lang\n")) {
      return str;
    }
    str = str.substring("@Lang\n".length());
    Mapx map = StringUtil.splitToMapx(str, "\n", "=", '\\');
    String r = (String)map.get(lang);
    if (r == null) {
      r = (String)map.get(LangMapping.getInstance().DefaultLanguage);
    }
    if ((r == null) && (map.size() > 0)) {
      r = String.valueOf(map.values().toArray()[0]);
    }
    return r;
  }

  public static String modify(String src, String value)
  {
    return modify(src, User.getLanguage(), value);
  }

  public static String modify(String src, String lang, String value)
  {
    Mapx<String, String> map = null;
    if (src == null) {
      return src;
    }
    if (ObjectUtil.empty(lang)) {
      return src;
    }
    src = StringUtil.replaceEx(src, "\r\n", "\n");
    if (src.startsWith("@Lang\n")) {
      src = src.substring("@Lang\n".length());
      map = StringUtil.splitToMapx(src, "\n", "=", '\\');
    } else {
      map = new Mapx();
    }
    map.put(lang, value);
    StringBuilder sb = new StringBuilder();
    sb.append("@Lang\n");
    for (String k : map.keySet()) {
      String v = (String)map.get(k);
      sb.append(k);
      sb.append("=");
      sb.append(StringUtil.javaEncode(v));
      sb.append("\n");
    }
    return sb.toString();
  }

  public static String getDefaultLanguage() {
    return LangMapping.getInstance().DefaultLanguage;
  }

  public static Mapx<String, String> getSupportedLanguages()
  {
    return LangMapping.getInstance().getLanguageMap();
  }

  public static String getI18nFieldValue(String name) {
    RequestData dc = Current.getRequest();
    if (dc == null) {
      throw new RuntimeException(" Current.getRequest() failed!");
    }
    String currenStr = dc.getString(name);
    String k = name + "_I18N";
    if (!dc.containsKey(k)) {
      return currenStr;
    }
    String langStr = dc.getString(k);
    langStr = StringUtil.unescape(langStr);
    langStr = modify(langStr, currenStr);
    return langStr;
  }

  public static void convertDataTable(DataTable dt, String lang)
  {
    for (int j = 0; j < dt.getColumnCount(); j++) {
      int columnType = dt.getDataColumn(j).getColumnType();
      if ((columnType == 1) || (columnType == 10))
        for (int i = 0; i < dt.getRowCount(); i++) {
          DataRow dr = dt.getDataRow(i);
          String str = dr.getString(j);
          dr.set(j, get(str, lang));
        }
    }
  }
}
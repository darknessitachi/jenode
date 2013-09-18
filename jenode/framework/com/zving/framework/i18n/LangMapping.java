package com.zving.framework.i18n;

import com.zving.framework.Config;
import com.zving.framework.User;
import com.zving.framework.collection.Mapx;
import com.zving.framework.utility.ObjectUtil;

public class LangMapping
{
  protected Mapx<String, Mapx<String, String>> mapping = null;
  protected String DefaultLanguage = "zh-cn";
  protected Mapx<String, String> languageMap;
  private static LangMapping instance = null;
  private static Object mutex = new Object();
  private static long lastTime = 0L;

  public static LangMapping getInstance() {
    if ((lastTime == 0L) || ((Config.isDebugMode()) && (System.currentTimeMillis() - lastTime > 3000L))) {
      synchronized (mutex) {
        if ((lastTime == 0L) || ((Config.isDebugMode()) && (System.currentTimeMillis() - lastTime > 3000L))) {
          instance = LangLoader.load();
          lastTime = System.currentTimeMillis();
        }
      }
    }
    return instance;
  }

  public Mapx<String, String> getLanguageMap() {
    return this.languageMap;
  }

  public String getDefaultLanguage() {
    return this.DefaultLanguage;
  }

  public String getValue(String lang, String key) {
    key = filterKey(key);
    Mapx map = (Mapx)this.mapping.get(lang);
    if (map == null) {
      return null;
    }
    String str = (String)map.get(key);
    if ((str == null) && (!lang.equals(this.DefaultLanguage))) {
      map = (Mapx)this.mapping.get(this.DefaultLanguage);
      if (map != null) {
        str = (String)map.get(key);
      }
    }
    return str;
  }

  public static String get(String lang, String key) {
    return getInstance().getValue(lang, key);
  }

  public static String get(String key) {
    return getInstance().getValue(key);
  }

  public String getValue(String key) {
    String lang = User.getLanguage();
    if (ObjectUtil.empty(lang)) {
      lang = this.DefaultLanguage;
    }
    return getValue(lang, key);
  }

  public void put(String lang, String key, String value) {
    key = filterKey(key);
    Mapx map = (Mapx)this.mapping.get(lang);
    if (map == null) {
      synchronized (this.mapping) {
        map = new Mapx();
        this.mapping.put(lang, map);
      }
    }
    map.put(key, value);
  }

  private String filterKey(String key)
  {
    if (key == null) {
      return key;
    }
    if ((key.startsWith("@{")) && (key.endsWith("}"))) {
      return key.substring(2, key.length() - 1);
    }
    return key;
  }
}
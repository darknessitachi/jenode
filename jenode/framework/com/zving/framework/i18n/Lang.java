package com.zving.framework.i18n;

public class Lang
{
  public static String get(String key)
  {
    return LangMapping.get(key);
  }

  public static String get(String lang, String key) {
    return LangMapping.get(lang, key);
  }
}
package com.zving.framework.annotation.util;

import com.zving.framework.collection.Mapx;

public class SQLMapping
{
  private Mapx<String, String> mapping = new Mapx();
  private static SQLMapping singleton = new SQLMapping();

  public static String get(String name) {
    AnnotationVisitor.load();
    return singleton.mapping.getString(name);
  }

  protected static void put(String key, String str) {
    singleton.mapping.put(key, str);
  }
}
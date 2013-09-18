package com.zving.framework.annotation.util;

import com.zving.framework.Config;
import com.zving.framework.collection.CaseIgnoreMapx;

public class AliasMapping
{
  private CaseIgnoreMapx<String, String> fixed = new CaseIgnoreMapx();
  private CaseIgnoreMapx<String, String> rules = new CaseIgnoreMapx();
  private static AliasMapping singleton = new AliasMapping();

  public static void clear()
  {
    singleton.fixed = new CaseIgnoreMapx();
    singleton.rules = new CaseIgnoreMapx();
  }

  public static String get(String name) {
    AnnotationVisitor.load();
    name = normalize(name);
    if ((name.indexOf("{") >= 0) && (name.indexOf("}") > 0)) {
      for (String rule : singleton.rules.keyArray())
        if (match(name, rule))
          return (String)singleton.rules.get(rule);
    }
    else
    {
      String m = (String)singleton.fixed.get(name);
      if ((m == null) && (Config.isDebugMode())) {
        AnnotationVisitor.load();
        m = (String)singleton.fixed.get(name);
      }
      if (m != null) {
        return m;
      }
    }
    return name;
  }

  private static boolean match(String name, String rule) {
    return false;
  }

  private static String normalize(String name) {
    if (name == null) {
      return null;
    }
    name = name.replace('/', '.');
    if (name.endsWith(".")) {
      name = name.substring(0, name.length() - 1);
    }
    if (name.startsWith(".")) {
      name = name.substring(1);
    }
    return name;
  }

  public static void put(String name, String method) {
    if (name == null) {
      return;
    }
    name = normalize(name);
    method = normalize(method);

    if ((name.indexOf("{") >= 0) && (name.indexOf("}") > 0))
      singleton.rules.put(name, method);
    else
      singleton.fixed.put(name, method);
  }
}
package com.zving.framework;

import com.zving.framework.collection.Mapx;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.framework.xml.XMLElement;
import com.zving.framework.xml.XMLMultiLoader;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

public class ConfigLoader
{
  private static boolean Loaded = false;

  private static Object mutex = new Object();

  private static XMLMultiLoader loader = new XMLMultiLoader();

  public static void load()
  {
    try
    {
      String path = Config.getPluginPath() + "classes/";
      load(path);
    } catch (RuntimeException e) {
      System.err.println(e.getMessage());
    }
  }

  public static void load(String path) {
    if (!Loaded)
      synchronized (mutex) {
        if (!Loaded) {
          loader.clear();
          File f = new File(path);
          if (!f.exists()) {
            return;
          }
          String file = path + "charset.config";
          if (new File(file).exists()) {
            String txt = FileUtil.readText(file, "UTF-8");
            Mapx map = StringUtil.splitToMapx(txt, "\n", "=");
            Config.GlobalCharset = "GBK".equalsIgnoreCase(map.getString("global")) ? "GBK" : "UTF-8";
          }

          loader.load(path);
          XMLElement data = loader.getElement("framework.application.config", "name", "ComplexDeployMode");
          Config.ComplexDepolyMode = (data != null) && ("true".equals(data.getText()));
        }
        Loaded = true;
      }
  }

  public static void reload()
  {
    Loaded = false;
    load();
  }

  public static XMLElement getElement(String path, String attrName, String attrValue) {
    return loader.getElement(path, attrName, attrValue);
  }

  public static List<XMLElement> getElements(String path) {
    return loader.getElements(path);
  }
}
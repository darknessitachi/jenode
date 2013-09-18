package com.zving.framework.ui.resource;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.extend.plugin.PluginManager;
import com.zving.framework.utility.StringUtil;
import com.zving.framework.utility.ZipUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JarResourceRoot
{
  private static Mapx<String, Object> all = null;

  public static JarResourceEntry getFile(String fullFileName) {
    init();
    if ((fullFileName.equals("/")) || (fullFileName.equals(""))) {
      return new JarResourceEntry(null, 0L, "", true, 0L);
    }
    int index = fullFileName.lastIndexOf("/");
    if (index < 0) {
      Object obj = all.get(fullFileName);
      if (obj == null) {
        return null;
      }
      if ((obj instanceof JarResourceEntry)) {
        return (JarResourceEntry)obj;
      }
      return new JarResourceEntry(null, 0L, fullFileName, true, 0L);
    }

    String path = fullFileName.substring(0, index);
    String fileName = fullFileName.substring(index + 1);
    String[] arr = StringUtil.splitEx(path, "/");
    Mapx current = all;
    for (String seg : arr)
      if (!StringUtil.isEmpty(seg))
      {
        Mapx map = (Mapx)current.get(seg);
        if (map == null) {
          return null;
        }
        current = map;
      }
    Object obj = current.get(fileName);
    if (obj == null) {
      return null;
    }
    if ((obj instanceof JarResourceEntry)) {
      return (JarResourceEntry)obj;
    }
    return new JarResourceEntry(null, 0L, fullFileName, true, 0L);
  }

  public static List<JarResourceEntry> listFiles(String path)
  {
    init();
    if (path.endsWith("/")) {
      path = path.substring(0, path.length() - 1);
    }
    String[] arr = StringUtil.splitEx(path, "/");
    Mapx current = all;
    Mapx map;
    for (int i = 0; i < arr.length; i++) {
      Object obj = current.get(arr[i]);
      if (obj == null) {
        return null;
      }
      if ((obj instanceof JarResourceEntry)) {
        return null;
      }

      map = (Mapx)current.get(arr[i]);
      current = map;
    }

    ArrayList res = new ArrayList();
    for (String k : current.keyArray()) {
      Object obj = current.get(k);
      if ((obj instanceof JarResourceEntry))
        res.add((JarResourceEntry)obj);
      else {
        res.add(new JarResourceEntry(null, 0L, path + "/" + k, true, 0L));
      }
    }
    return res;
  }

  private static void init() {
    if (all == null)
      synchronized (JarResourceRoot.class) {
        if (all == null) {
          all = new Mapx();
          String path = Config.getPluginPath() + "lib/";
          if (!new File(path).exists()) {
            return;
          }
          for (PluginConfig pc : PluginManager.getInstance().getAllPluginConfig()) {
            loadFromJar(new File(path + pc.getID() + ".ui.jar"));
            loadFromJar(new File(path + pc.getID() + ".resource.jar"));
          }
        }
      }
  }

  private static void loadFromJar(File f)
  {
    if (!f.exists())
      return;
    try
    {
      JarFile jar = new JarFile(f);
      Mapx files = ZipUtil.getFileListInZip(f.getAbsolutePath());
      for (String k : files.keyArray()) {
        JarResourceEntry re = new JarResourceEntry(f.getAbsolutePath(), jar.getEntry(k).getTime(), k, false, ((Long)files.get(k)).longValue());
        String[] arr = StringUtil.splitEx(k, "/");
        Mapx current = all;
        for (int i = 0; i < arr.length - 1; i++)
        {
          Mapx map = (Mapx)current.get(arr[i]);
          if (map == null) {
            map = new Mapx();
            current.put(arr[i], map);
          }
          current = map;
        }
        current.put(arr[(arr.length - 1)], re);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
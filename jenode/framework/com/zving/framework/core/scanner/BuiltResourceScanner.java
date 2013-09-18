package com.zving.framework.core.scanner;

import com.zving.framework.Config;
import com.zving.framework.ConfigLoader;
import com.zving.framework.collection.Mapx;
import com.zving.framework.config.ExcludeClassScan;
import com.zving.framework.core.asm.objectweb.ClassReader;
import com.zving.framework.core.asm.objectweb.tree.ClassNode;
import com.zving.framework.core.asm.objectweb.tree.InnerClassNode;
import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.extend.plugin.PluginManager;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.framework.utility.ZipUtil;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BuiltResourceScanner
{
  protected static long lastTime = 0L;
  private static Object mutex = new Object();

  public static void load() {
    if ((lastTime == 0L) || ((Config.isDebugMode()) && (System.currentTimeMillis() - lastTime > 3000L)))
      synchronized (mutex) {
        if ((lastTime == 0L) || ((Config.isDebugMode()) && (System.currentTimeMillis() - lastTime > 3000L))) {
          scanAll(BuiltResourceVisitorService.getInstance().getAll(), null);
          ConfigLoader.reload();
          lastTime = System.currentTimeMillis();
        }
      }
  }

  public static void load(String webinfPath)
  {
  }

  public static void scanAll(List<? extends IBuiltResourceVisitor> visitors, String webinfPath)
  {
    long t = System.currentTimeMillis();
    PluginManager pm = new PluginManager();
    String pluginPath = Config.getPluginPath();
    if (webinfPath == null) {
      pm = PluginManager.getInstance();
    } else {
      pm.init(webinfPath);
      pluginPath = webinfPath + "/plugins/";
    }
    List list = pm.getAllPluginConfig();
    Iterator localIterator2;
    for (Iterator localIterator1 = list.iterator(); localIterator1.hasNext(); 
      localIterator2.hasNext())
    {
      PluginConfig pc = (PluginConfig)localIterator1.next();
      scanJar(new File(pluginPath + "lib/" + pc.getID() + ".plugin.jar"), visitors);
      localIterator2 = pc.getPluginFiles().iterator(); continue; String path = (String)localIterator2.next();
      if (path.startsWith("[D]")) {
        path = path.substring(3);
        if (path.startsWith("JAVA")) {
          path = pluginPath + "classes/" + path.substring(5);
          path = FileUtil.normalizePath(path);
          try {
            scanOneDir(new File(path), pluginPath + "classes/", visitors);
          } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error("Load class directory failed:" + e.getMessage());
          }
        }
      }
      else if (path.endsWith(".java"))
      {
        path = path.substring(5);
        path = path.substring(0, path.lastIndexOf("."));
        path = pluginPath + "classes/" + path + ".class";
        if (new File(path).exists())
        {
          try
          {
            BuiltResource br = new BuiltResource();
            br.FileName = path;
            scanOneResource(br, visitors);
          } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error("Load single class failed:" + path);
          }
        }
      }
    }
    if (lastTime == 0L) {
      LogUtil.info("----" + Config.getAppCode() + "(" + LangUtil.get(Config.getAppName()) + "): Scan class and resource used " + (
        System.currentTimeMillis() - t) + " ms----");
    }
    lastTime = System.currentTimeMillis();
  }

  private static void scanJar(File f, List<? extends IBuiltResourceVisitor> visitors) {
    try {
      if (!f.exists()) {
        return;
      }
      Mapx files = ZipUtil.getFileListInZip(f.getAbsolutePath());
      for (String fileName : files.keyArray()) {
        BuiltResource br = new BuiltResource();
        br.isInJar = true;
        br.JarEntryName = fileName;
        if (fileName.indexOf("$") <= 0)
        {
          br.FileName = f.getAbsolutePath();
          scanOneResource(br, visitors);
        }
      }
    } catch (Exception e) { e.printStackTrace(); }
  }

  private static void scanOneDir(File p, String prefix, List<? extends IBuiltResourceVisitor> visitors) throws Exception
  {
    if (!p.exists()) {
      return;
    }
    String path = FileUtil.normalizePath(p.getAbsolutePath());
    if (!path.endsWith("/")) {
      path = path + "/";
    }
    path = path.substring(prefix.length());
    String exclude = ExcludeClassScan.getValue();
    if (ObjectUtil.notEmpty(exclude)) {
      for (String str : StringUtil.splitEx(exclude, ",")) {
        if ((ObjectUtil.notEmpty(str)) && (path.startsWith(str))) {
          return;
        }
      }
    }
    File[] fs = p.listFiles();
    for (int i = 0; i < fs.length; i++) {
      File f = fs[i];
      if (f.isFile()) {
        BuiltResource br = new BuiltResource();
        br.FileName = f.getAbsolutePath();
        if (br.FileName.indexOf("$") <= 0)
        {
          scanOneResource(br, visitors);
        }
      } else { scanOneDir(f, prefix, visitors); }
    }
  }

  public static void scanOneResource(BuiltResource br, List<? extends IBuiltResourceVisitor> vistors) throws Exception
  {
    if (br.getFullName().indexOf("com/zving/framework/") >= 0) {
      return;
    }
    ClassNode cn = null;
    for (IBuiltResourceVisitor cv : vistors)
      if ((cv.match(br)) && 
        (cn == null))
        if (br.isClass()) {
          InputStream is = br.getInputStream();
          try {
            ClassReader cr = new ClassReader(is);
            cn = new ClassNode();
            cr.accept(cn, 0);
          } finally {
            is.close();
          }
          cv.visitClass(br, cn);
          for (InnerClassNode icn : cn.innerClasses)
            if ((icn.outerName != null) && (icn.name.startsWith(cn.name)))
            {
              InputStream iis = br.getInnerInputStream(icn.name);
              try {
                ClassReader cr = new ClassReader(iis);
                ClassNode cn2 = new ClassNode();
                cr.accept(cn2, 0);
                cv.visitInnerClass(br, cn, cn2);
              } finally {
                iis.close();
              }
            }
        } else {
          cv.visitResource(br);
        }
  }
}
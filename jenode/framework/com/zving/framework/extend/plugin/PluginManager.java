package com.zving.framework.extend.plugin;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.PropertiesUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.framework.utility.ZipUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PluginManager
{
  private ArrayList<PluginConfig> configList = new ArrayList();
  private Mapx<String, String> statusMap = new Mapx();
  private Object mutex = new Object();
  private static PluginManager instance = new PluginManager();

  public static PluginManager getInstance() {
    return instance;
  }

  public void init(String path) {
    if ((this.configList == null) || (this.configList.size() == 0))
      synchronized (this.mutex) {
        if ((this.configList == null) || (this.configList.size() == 0))
          this.configList = loadAllConfig(path);
      }
  }

  public void destory()
  {
    this.configList.clear();
    this.statusMap.clear();
    this.configList = null;
    this.statusMap = null;
  }

  public Mapx<String, String> getStatusMap() {
    return this.statusMap;
  }

  public ArrayList<PluginConfig> loadAllConfig(String path)
  {
    ArrayList list = new ArrayList();
    Mapx map = new Mapx();

    File statusFile = new File(path + "/plugins/classes/plugins/status.config");
    if (statusFile.exists()) {
      this.statusMap = PropertiesUtil.read(statusFile);
    }
InputStream is = PluginManager.class.getResourceAsStream("/plugins/com.zving.framework.plugin");
    PluginConfig pc;
    try {
      if (is != null) {
        byte[] bs = FileUtil.readByte(is);
        pc = new PluginConfig();
        pc.parse(new String(bs, "UTF-8"));
        if (!map.containsKey(pc.getID())) {
          list.add(pc);
        }
        map.put(pc.getID(), "");
      }
    } catch (Exception e) {
      e.printStackTrace();
      try
      {
        if (is != null)
          is.close();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    finally
    {
      try
      {
        if (is != null)
          is.close();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }

    if (!Config.isPluginContext) {
      return list;
    }

    loadFromLib(path + "/lib", list, map);
    loadFromLib(path + "/plugins/lib", list, map);

    loadFromClasses(path + "/classes/plugins", list, map);
    loadFromClasses(path + "/plugins/classes/plugins", list, map);

    for (PluginConfig pc : list) {
      if ("false".equals(this.statusMap.get(pc.getID()))) {
        pc.setEnabling(false);
      }
    }
    computeRela(list);

    ArrayList result = new ArrayList();
    ArrayList tmp = new ArrayList();
    tmp.addAll(list);
    for (PluginConfig pc : tmp) {
      sort(result, list, pc);
    }
    return result;
  }

  private void loadFromLib(String path, ArrayList<PluginConfig> list, Mapx<String, String> map) {
    if (new File(path).exists()) {
      File[] fs = new File(path).listFiles();
      for (File f : fs)
        if (f.getName().endsWith(".plugin.jar"))
          try {
            Mapx files = ZipUtil.getFileListInZip(f.getAbsolutePath());
            for (String fileName : files.keyArray())
              if (fileName.endsWith(".plugin")) {
                byte[] bs = ZipUtil.readFileInZip(f.getAbsolutePath(), fileName);
                PluginConfig pc = new PluginConfig();
                pc.parse(new String(bs, "UTF-8"));
                if (!map.containsKey(pc.getID()))
                  list.add(pc);
                else {
                  LogUtil.warn("PluginConfig is duplication:" + (String)map.get(pc.getID()) + " & " + f.getAbsolutePath() + "!" + 
                    fileName);
                }
                map.put(pc.getID(), f.getAbsolutePath() + "!" + fileName);
              }
          }
          catch (Exception e) {
            e.printStackTrace();
          }
    }
  }

  private void loadFromClasses(String path, ArrayList<PluginConfig> list, Mapx<String, String> map)
  {
    File langDir = new File(path);
    if (langDir.exists())
      try {
        File[] fs = langDir.listFiles();
        for (File f : fs)
          if ((f.isFile()) && (f.getName().toLowerCase().endsWith(".plugin"))) {
            PluginConfig pc = new PluginConfig();
            pc.parse(FileUtil.readText(f, "UTF-8"));
            if (!pc.getID().equals("com.zving.framework"))
            {
              if (!map.containsKey(pc.getID())) {
                list.add(pc);
                map.put(pc.getID(), f.getAbsolutePath());
              }
              else {
                int i = 0;
                for (PluginConfig pc2 : list) {
                  if (pc2.getID().equals(pc.getID())) {
                    list.set(i, pc);
                    break;
                  }
                  i++;
                }
                LogUtil.warn("PluginConfig is duplication:" + (String)map.get(pc.getID()) + " & " + f.getAbsolutePath());
              }
            }
          }
      } catch (Exception e) {
        e.printStackTrace();
      }
  }

  private void sort(ArrayList<PluginConfig> result, ArrayList<PluginConfig> list, PluginConfig pc)
  {
    if (getPluginConfig(result, pc.getID()) != null) {
      return;
    }
    list.remove(pc);
    for (String pluginID : pc.getRequiredPlugins().keySet()) {
      PluginConfig c = getPluginConfig(list, pluginID);
      if (c != null)
      {
        sort(result, list, c);
      }
    }
    if (getPluginConfig(result, pc.getID()) == null)
      result.add(pc);
  }

  private void computeRela(ArrayList<PluginConfig> list)
  {
    for (PluginConfig pc : list)
      if (pc.isEnabling())
      {
        boolean requiredFlag = true;
        String need;
        for (String pluginID : pc.getRequiredPlugins().keySet()) {
          PluginConfig c = getPluginConfig(list, pluginID);
          if ((c == null) || (!c.isEnabling())) {
            if (c == null) {
              LogUtil.error("Plugin " + pluginID + " needed by " + pc.getID() + " is not found!");
            }
            requiredFlag = false;
            break;
          }

          String v = c.getVersion();
          need = (String)pc.getRequiredPlugins().get(pluginID);
          if (!isVersionCompatible(need, v)) {
            LogUtil.error("Plugin " + pluginID + "'s version is " + v + ", but " + need + " is needed by " + pc.getID() + "!");
          }
        }
        if (!requiredFlag) {
          setDisable(pc);
        }
        else
        {
          requiredFlag = true;
          for (String extendPointID : pc.getRequiredExtendPoints().keyArray()) {
            boolean flag = false;
            for (PluginConfig c : list) {
              if (c.getExtendPoints().containsKey(extendPointID)) {
                flag = true;
                break;
              }
            }
            if (!flag) {
              LogUtil.error("ExtendPoint " + extendPointID + " needed by " + pc.getID() + " is not found!");
              requiredFlag = false;
              break;
            }
          }
          if (!requiredFlag)
            setDisable(pc);
        }
      }
  }

  private boolean isVersionCompatible(String need, String version)
  {
    if (need.indexOf("-") > 0) {
      String[] arr = StringUtil.splitEx(need, "-");
      if (arr.length != 2) {
        return false;
      }
      String start = arr[0];
      String end = arr[1];
      if (start.endsWith(".x")) {
        start = start.substring(0, start.length() - 2);
      }
      if (end.endsWith(".x")) {
        end = end.substring(0, end.length() - 2);
      }
      double s = Double.parseDouble(start);
      double e = Double.parseDouble(end);
      double v = Double.parseDouble(version);
      return (s <= v) && (e >= v);
    }
    if (need.endsWith(".x")) {
      return version.startsWith(need.substring(0, need.length() - 1));
    }
    return version.equals(need);
  }

  private void setDisable(PluginConfig pc)
  {
    if (!pc.isEnabling()) {
      return;
    }
    pc.setEnabling(true);
    for (PluginConfig c : this.configList) {
      for (String pluginID : c.getRequiredPlugins().keyArray()) {
        if (pc.getID().equals(pluginID)) {
          setDisable(c);
          break;
        }
      }
      for (String extendPointID : c.getRequiredExtendPoints().keyArray())
        if (pc.getExtendPoints().containsKey(extendPointID)) {
          setDisable(c);
          break;
        }
    }
  }

  public PluginConfig getPluginConfig(List<PluginConfig> list, String pluginID)
  {
    for (PluginConfig c : list) {
      if (c.getID().equals(pluginID)) {
        return c;
      }
    }
    return null;
  }

  public PluginConfig getPluginConfig(String pluginID) {
    return getPluginConfig(this.configList, pluginID);
  }

  public PluginConfig getPluginConfig(Class<? extends AbstractPlugin> clazz)
  {
    for (PluginConfig c : this.configList) {
      if (c.getClassName().equals(clazz.getName())) {
        return c;
      }
    }
    return null;
  }

  public ArrayList<PluginConfig> getAllPluginConfig() {
    return this.configList;
  }
}
package com.zving.framework.extend.menu;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.extend.plugin.PluginException;
import com.zving.framework.extend.plugin.PluginManager;
import com.zving.framework.json.JSON;
import com.zving.framework.json.JSONObject;
import com.zving.framework.utility.Errorx;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.JSONUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.ZipUtil;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;

public class MenuManager
{
  private static Mapx<String, Menu> menus;
  private static long lastTime = 0L;
  private static Object mutex = new Object();

  public static Mapx<String, Menu> getMenus() {
    synchronized (mutex)
    {
      String dir;
      Object menuCfg;
      if ((Config.isDebugMode()) && (System.currentTimeMillis() - lastTime > 5000L))
      {
        String path = Config.getContextRealPath() + "WEB-INF/plugins";
        menus = new Mapx();
        PluginConfig pc;
        if (new File(path + "/lib/").exists()) {
          File[] fs = new File(path + "/lib/").listFiles();
          for (File f : fs) {
            if (f.getName().endsWith(".plugin.jar")) {
              try {
                Mapx files = ZipUtil.getFileListInZip(f.getAbsolutePath());
                for (String fileName : files.keyArray()) {
                  if (fileName.endsWith(".plugin")) {
                    byte[] bs = ZipUtil.readFileInZip(f.getAbsolutePath(), fileName);
                    PluginConfig pluginConfig = new PluginConfig();
                    pluginConfig.parse(new String(bs, "UTF-8"));

                    pc = PluginManager.getInstance().getPluginConfig(pluginConfig.getID());
                    if ((pc == null) || (pc.isEnabling()))
                      for (Menu m : pluginConfig.getMenus().values())
                        menus.put(m.getID(), m);
                  }
                }
              }
              catch (Exception e)
              {
                e.printStackTrace();
              }
            }
          }

        }

        dir = Config.getContextRealPath() + "WEB-INF/plugins/classes/plugins/";
        File p = new File(dir);
        PluginConfig pluginConfig;
        if (p.exists()) {
          for (File f : p.listFiles()) {
            if (f.getName().endsWith(".plugin"))
            {
              pluginConfig = new PluginConfig();
              String xml = FileUtil.readText(f, "UTF-8");
              try {
                pluginConfig.parse(xml);

                PluginConfig pc = PluginManager.getInstance().getPluginConfig(pluginConfig.getID());
                if ((pc == null) || (pc.isEnabling()))
                  for (Menu m : pluginConfig.getMenus().values())
                    menus.put(m.getID(), m);
              }
              catch (PluginException e)
              {
                e.printStackTrace();
              }
            }
          }
        }
        menuCfg = Config.getContextRealPath() + "WEB-INF/plugins/classes/plugins/menu.config";
        if (FileUtil.exists((String)menuCfg)) {
          String json = FileUtil.readText((String)menuCfg);

          Mapx menux = (Mapx)JSON.parse(json);
          for (String mid : menux.keyArray()) {
            JSONObject jo = (JSONObject)menux.get(mid);
            PluginConfig pc = PluginManager.getInstance().getPluginConfig(menux.getString("PluginID"));
            if ((pc == null) || (pc.isEnabling())) {
              Menu m = (Menu)menus.get(mid);
              if (ObjectUtil.empty(m)) {
                m = new Menu();
                m.setID(mid);
                menus.put(mid, m);
              }
              m.setParentID(jo.getString("PID"));
              m.setOrder(jo.getString("Order"));
              m.setIcon(jo.getString("Icon"));
              m.setDescription(jo.getString("Memo"));
              m.setName(jo.getString("Name"));
              m.setType(jo.getString("Type"));
            } else {
              Menu m = (Menu)menus.get(mid);
              m.setName(jo.getString("Name"));
              m.setParentID(jo.getString("PID"));
              m.setOrder(jo.getString("Order"));
            }
          }
        }

        lastTime = System.currentTimeMillis();
      }
      if (menus == null) {
        menus = new Mapx();
        for (PluginConfig pc : PluginManager.getInstance().getAllPluginConfig())
          if (pc.isEnabling())
          {
            for (menuCfg = pc.getMenus().values().iterator(); ((Iterator)menuCfg).hasNext(); ) { Menu m = (Menu)((Iterator)menuCfg).next();
              menus.put(m.getID(), m);
            }
          }
      }
      return menus;
    }
  }

  public static void reloadMenus() {
    synchronized (mutex) {
      menus = null;
    }
  }

  public static Menu getMenu(String id) {
    return (Menu)menus.get(id);
  }
  public static boolean addMenu(Menu m) {
    if (ObjectUtil.notEmpty(menus.get(m.getID()))) {
      Errorx.addError("menuID is existed!");
      return false;
    }
    menus.put(m.getID(), m);
    writeMenuCfgFile();
    return true;
  }
  private static void writeMenuCfgFile() {
    synchronized (mutex) {
      Mapx menux = menus;
      Mapx out = new Mapx();
      for (String mid : menux.keyArray()) {
        Menu e = (Menu)menux.get(mid);
        JSONObject jo = new JSONObject();
        jo.put("PID", e.getParentID());
        jo.put("Name", e.getName());
        jo.put("Order", e.getOrder());
        jo.put("Icon", e.getIcon());
        jo.put("Memo", e.getDescription());
        jo.put("Type", e.getType());
        jo.put("PluginID", e.getParentID());
        out.put(e.getID(), jo);
      }
      String json = JSONUtil.toJSON(out);
      String menuCfg = Config.getContextRealPath() + "WEB-INF/plugins/classes/plugins/menu.config";
      if (!FileUtil.exists(menuCfg.substring(0, menuCfg.lastIndexOf("/")))) {
        FileUtil.mkdir(menuCfg.substring(0, menuCfg.lastIndexOf("/")));
      }
      FileUtil.writeText(menuCfg, json);
    }
  }

  public static boolean deleteMenu(String mid) { if (ObjectUtil.empty(menus.get(mid))) {
      Errorx.addError("menuID is not existed!");
      return false;
    }
    menus.remove(menus.get(mid));
    writeMenuCfgFile();
    return true; }

  public static boolean editMenu(Menu m) {
    if (ObjectUtil.empty(menus.get(m.getID()))) {
      Errorx.addError("menuID is not existed!");
      return false;
    }
    menus.put(m.getID(), m);
    writeMenuCfgFile();
    return true;
  }

  public static boolean delMenu(Menu m) {
    if (ObjectUtil.empty(menus.get(m.getID()))) {
      Errorx.addError("menuID is not existed!");
      return false;
    }

    String ID = m.getID();
    for (String mID : menus.keyArray()) {
      Menu menu = (Menu)menus.get(mID);
      if ((menu.getParentID() != null) && (menu.getParentID().equals(ID))) {
        PluginConfig pc = PluginManager.getInstance().getPluginConfig(menu.getPluginConfig().getID());
        menu.setParentID(((Menu)pc.getMenus().get(mID)).getParentID());
      }
    }

    menus.remove(m.getID());
    writeMenuCfgFile();
    return true;
  }
}
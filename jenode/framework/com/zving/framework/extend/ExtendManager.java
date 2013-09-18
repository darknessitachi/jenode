package com.zving.framework.extend;

import com.zving.framework.Config;
import com.zving.framework.SessionListener;
import com.zving.framework.collection.Mapx;
import com.zving.framework.extend.exception.CreateExtendActionInstanceException;
import com.zving.framework.extend.menu.MenuManager;
import com.zving.framework.extend.plugin.IPlugin;
import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.extend.plugin.PluginException;
import com.zving.framework.extend.plugin.PluginManager;
import com.zving.framework.schedule.CronManager;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.PropertiesUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExtendManager
{
  private Mapx<String, ArrayList<ExtendActionConfig>> extendActionMap;
  private Mapx<String, ArrayList<ExtendItemConfig>> extendItemMap;
  private Mapx<String, ArrayList<OptionalItemConfig>> optionalItemMap;
  private Mapx<String, ExtendPointConfig> extendPointMap;
  private Mapx<String, ExtendServiceConfig> extendServiceMap;
  private Mapx<String, ExtendServiceConfig> extendServiceClassMap;
  private Mapx<String, ReplaceableItemConfig> replaceableItemMap;
  private Object mutex = new Object();
  private static ExtendManager instance = new ExtendManager();

  public static ExtendManager getInstance() {
    return instance;
  }

  public void start()
  {
    synchronized (this.mutex) {
      if (this.extendActionMap == null) {
        this.extendActionMap = new Mapx();
        this.extendItemMap = new Mapx();
        this.extendPointMap = new Mapx();
        this.extendServiceMap = new Mapx();
        this.extendServiceClassMap = new Mapx();
        this.replaceableItemMap = new Mapx();

        long t = System.currentTimeMillis();

        PluginManager.getInstance().init(Config.getWEBINFPath());
        ArrayList configList = PluginManager.getInstance().getAllPluginConfig();
        for (PluginConfig pc : configList)
          if ((pc.isEnabling()) && (!pc.isRunning()))
          {
            try
            {
              startPlugin(pc);
            } catch (PluginException e) {
              e.printStackTrace();
            }
          }
        LogUtil.info("All plugins started,cost " + (System.currentTimeMillis() - t) + " ms");
        invoke("com.zving.framework.AfterAllPluginStarted", new Object[0]);
        invoke("com.zving.framework.AfterPluginInitAction", new Object[0]);
      }
    }
  }

  public void startPlugin(PluginConfig pc)
    throws PluginException
  {
    if ((pc == null) || (pc.isRunning())) {
      return;
    }
    if (ObjectUtil.notEmpty(pc.getClassName()))
      synchronized (this.mutex) {
        try {
          LogUtil.debug("Starting plugin:" + pc.getID());
          pc.setRunning(true);
          pc.setEnabling(true);
          for (String id : pc.getRequiredPlugins().keyArray()) {
            startPlugin(PluginManager.getInstance().getPluginConfig(id));
          }
          Class c = Class.forName(pc.getClassName());
          if (!IPlugin.class.isAssignableFrom(c)) {
            LogUtil.error("Plugin class '" + pc.getClassName() + "' isn't inherit from IPlugin");
            return;
          }

          this.extendPointMap.putAll(pc.getExtendPoints());

          for (ExtendServiceConfig es : pc.getExtendServices().valueArray()) {
            this.extendServiceMap.put(es.getID(), es);
            this.extendServiceClassMap.put(es.getClassName(), es);
          }

          Collection actions = pc.getExtendActions().values();
          ArrayList list;
          for (ExtendActionConfig action : actions)
          {
            if (!this.extendPointMap.containsKey(action.getExtendPointID())) {
              LogUtil.error("ExtendAction " + action.getID() + "'s ExtendPoint not found");
            }
            else {
              list = (ArrayList)this.extendActionMap.get(action.getExtendPointID());
              if (list == null) {
                list = new ArrayList();
                this.extendActionMap.put(action.getExtendPointID(), list);
              }
              list.add(action);
            }
          }

          Collection items = pc.getExtendItems().values();
          ExtendServiceConfig es;
          ArrayList list;
          for (ExtendItemConfig item : items)
          {
            if (!this.extendServiceMap.containsKey(item.getExtendServiceID())) {
              LogUtil.error("ExtendItem " + item.getID() + "'s ExtendService not found");
            }
            else
            {
              es = (ExtendServiceConfig)this.extendServiceMap.get(item.getExtendServiceID());
              try {
                es.getInstance().register(item.getInstance());
              } catch (Throwable e) {
                e.printStackTrace();
                continue;
              }

              list = (ArrayList)this.extendItemMap.get(item.getExtendServiceID());
              if (list == null) {
                list = new ArrayList();
                this.extendItemMap.put(item.getExtendServiceID(), list);
              }
              list.add(item);
            }
          }

          Collection rs = pc.getReplaceableItems().values();
          for (ReplaceableItemConfig r : rs) {
            this.replaceableItemMap.put(r.getID(), r);
          }

          Collection os = pc.getOptionalItems().values();
          for (OptionalItemConfig oic : os)
          {
            ArrayList list = (ArrayList)this.optionalItemMap.get(oic.getTargetID());
            if (list == null) {
              list = new ArrayList();
              this.optionalItemMap.put(oic.getTargetID(), list);
            }
            list.add(oic);
          }
          IPlugin plugin = (IPlugin)c.newInstance();
          plugin.start();
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        } catch (InstantiationException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
  }

  public List<PluginConfig> getRequiredPlugins(PluginConfig pc)
    throws PluginException
  {
    ArrayList list = new ArrayList();
    for (PluginConfig pc2 : PluginManager.getInstance().getAllPluginConfig()) {
      if ((!pc2.getID().equals(pc.getID())) && (pc2.getRequiredPlugins().containsKey(pc.getID()))) {
        list.add(pc2);
      }
    }
    return list;
  }

  public void stopPlugin(PluginConfig pc)
    throws PluginException
  {
    if ((!pc.isEnabling()) || (!pc.isRunning())) {
      return;
    }
    if (ObjectUtil.notEmpty(pc.getClassName()))
      synchronized (this.mutex) {
        try {
          Class c = Class.forName(pc.getClassName());
          if (!IPlugin.class.isAssignableFrom(c)) {
            throw new PluginException("Plugin class '" + pc.getClassName() + "' isn't inherit from IPlugin");
          }
          IPlugin plugin = (IPlugin)c.newInstance();
          plugin.stop();

          for (PluginConfig pc2 : getRequiredPlugins(pc)) {
            stopPlugin(pc2);
          }
          pc.setRunning(false);
          pc.setEnabling(false);

          for (ExtendActionConfig ea : pc.getExtendActions().valueArray()) {
            ((ArrayList)this.extendActionMap.get(ea.getExtendPointID())).remove(ea);
          }

          for (String id : pc.getExtendPoints().keyArray()) {
            this.extendPointMap.remove(id);
          }

          for (ExtendItemConfig ei : pc.getExtendItems().valueArray()) {
            ExtendServiceConfig es = (ExtendServiceConfig)this.extendServiceMap.get(ei.getExtendServiceID());
            if (es != null) {
              es.getInstance().remove(ei.getInstance().getID());
            }
            ((ArrayList)this.extendItemMap.get(ei.getExtendServiceID())).remove(ei);
          }

          for (ExtendServiceConfig es : pc.getExtendServices().valueArray()) {
            this.extendServiceMap.remove(es.getID());
            this.extendServiceClassMap.remove(es.getID());
          }
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        } catch (InstantiationException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
  }

  public void destory()
  {
    for (PluginConfig pc : PluginManager.getInstance().getAllPluginConfig()) {
      try {
        Class c = Class.forName(pc.getClassName());
        if (!IPlugin.class.isAssignableFrom(c)) {
          throw new PluginException("Plugin class '" + pc.getClassName() + "' isn't inherit from IPlugin");
        }
        IPlugin plugin = (IPlugin)c.newInstance();
        plugin.destory();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (this.extendServiceMap != null) {
      for (ExtendServiceConfig es : this.extendServiceMap.valueArray()) {
        es.getInstance().destory();
      }
      this.extendServiceMap.clear();
      this.extendServiceMap = null;
    }
    if (this.extendActionMap != null) {
      this.extendActionMap.clear();
      this.extendActionMap = null;
    }
    if (this.extendItemMap != null) {
      this.extendItemMap.clear();
      this.extendItemMap = null;
    }
    if (this.extendPointMap != null) {
      this.extendPointMap.clear();
      this.extendPointMap = null;
    }
    if (this.extendServiceClassMap != null) {
      this.extendServiceClassMap.clear();
      this.extendServiceClassMap = null;
    }
    PluginManager.getInstance().destory();
  }

  public boolean hasAction(String targetPoint)
  {
    start();
    return this.extendActionMap.get(targetPoint) != null;
  }

  public ArrayList<ExtendActionConfig> findActionsByPointID(String extendPointID)
  {
    if (!Config.isPluginContext()) {
      return null;
    }
    start();
    return (ArrayList)this.extendActionMap.get(extendPointID);
  }

  public ArrayList<ExtendItemConfig> findItems(String extendServiceID)
  {
    if (!Config.isPluginContext()) {
      return null;
    }
    start();
    return (ArrayList)this.extendItemMap.get(extendServiceID);
  }

  public ExtendPointConfig findExtendPoint(String extendPointID)
  {
    start();
    return (ExtendPointConfig)this.extendPointMap.get(extendPointID);
  }

  public ExtendServiceConfig findExtendService(String extendServiceID)
  {
    start();
    return (ExtendServiceConfig)this.extendServiceMap.get(extendServiceID);
  }

  public ReplaceableItemConfig findReplaceableItem(String id)
  {
    start();
    return (ReplaceableItemConfig)this.replaceableItemMap.get(id);
  }

  public ExtendServiceConfig findExtendServiceByClass(String className)
  {
    start();
    return (ExtendServiceConfig)this.extendServiceClassMap.get(className);
  }

  public static Object[] invoke(String extendPointID, Object[] args)
  {
    return instance.invokePoint(extendPointID, args);
  }

  public Object[] invokePoint(String extendPointID, Object[] args) {
    try {
      if (!Config.isPluginContext()) {
        return new Object[0];
      }
      start();
      if (!this.extendPointMap.containsKey(extendPointID)) {
        LogUtil.warn("ExtendPoint is not found:" + extendPointID);
        return new Object[0];
      }
      ArrayList actions = findActionsByPointID(extendPointID);
      if (actions == null) {
        return null;
      }
      List r = new ArrayList();
      for (int i = 0; i < actions.size(); i++) {
        try {
          IExtendAction ea = ((ExtendActionConfig)actions.get(i)).getInstance();
          if (ea.isUsable())
          {
            r.add(ea.execute(args));
          }
        } catch (CreateExtendActionInstanceException e) { e.printStackTrace();
          actions.remove(i);
          i--;
        } catch (Throwable e) {
          e.printStackTrace();
        }
      }
      return r.toArray();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public void enablePlugin(String pluginID)
    throws PluginException
  {
    startPlugin(PluginManager.getInstance().getPluginConfig(pluginID));
    setStatusValue(pluginID, "true");
    MenuManager.reloadMenus();
  }

  public void disablePlugin(String pluginID)
    throws PluginException
  {
    stopPlugin(PluginManager.getInstance().getPluginConfig(pluginID));
    MenuManager.reloadMenus();
    setStatusValue(pluginID, "false");
  }

  public void enableMenu(String menuID)
  {
    setStatusValue("MENU." + menuID, "true");
  }

  public void disableMenu(String menuID)
  {
    setStatusValue("MENU." + menuID, "false");
  }

  public boolean isPluginEnable(String pluginID)
  {
    return !"false".equals(PluginManager.getInstance().getStatusMap().get(pluginID));
  }

  public boolean isMenuEnable(String menuID)
  {
    return !"false".equals(PluginManager.getInstance().getStatusMap().get("MENU." + menuID));
  }

  private void setStatusValue(String key, String value)
  {
    File f = new File(Config.getPluginPath() + "classes/plugins/status.config");
    PluginManager.getInstance().getStatusMap().put(key, value);
    PropertiesUtil.write(f, PluginManager.getInstance().getStatusMap());
  }

  public void restart()
  {
    SessionListener.forceExit();
    Config.isAllowLogin = false;
    CronManager.getInstance().destory();

    this.extendActionMap = null;
    start();
  }
}
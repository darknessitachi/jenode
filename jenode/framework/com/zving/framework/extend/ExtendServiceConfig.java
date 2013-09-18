package com.zving.framework.extend;

import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.extend.plugin.PluginException;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.xml.XMLElement;
import java.util.List;

public class ExtendServiceConfig
{
  private boolean enable;
  private PluginConfig pluginConfig;
  private String id;
  private String description;
  private String className;
  private String itemClassName;
  private IExtendService<?> instance = null;
  private static Object mutex = new Object();

  public void init(PluginConfig pc, XMLElement parent) throws PluginException {
    this.pluginConfig = pc;
    for (XMLElement nd : parent.elements()) {
      if (nd.getQName().equalsIgnoreCase("id")) {
        this.id = nd.getText().trim();
      }
      if (nd.getQName().equalsIgnoreCase("description")) {
        this.description = nd.getText().trim();
      }
      if (nd.getQName().equalsIgnoreCase("class")) {
        this.className = nd.getText().trim();
      }
      if (nd.getQName().equalsIgnoreCase("itemClass")) {
        this.itemClassName = nd.getText().trim();
      }
    }
    if (ObjectUtil.empty(this.id))
      throw new PluginException("extendPoint's id is empty!");
  }

  public String getID()
  {
    return this.id;
  }

  public String getDescription() {
    return this.description;
  }

  public String getDescription(String language) {
    if (this.description == null) {
      return null;
    }
    return LangUtil.get(this.description, language);
  }

  public String getClassName() {
    return this.className;
  }

  public void setID(String id) {
    this.id = id;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public boolean isEnable() {
    return this.enable;
  }

  public void setEnable(boolean enable) {
    this.enable = enable;
  }

  public PluginConfig getPluginConfig() {
    return this.pluginConfig;
  }

  public String getItemClassName() {
    return this.itemClassName;
  }

  public void setItemClassName(String itemClassName) {
    this.itemClassName = itemClassName;
  }

  public IExtendService<?> getInstance() {
    try {
      if (this.instance == null) {
        synchronized (mutex) {
          if (this.instance == null) {
            Class clazz = Class.forName(this.className);
            try {
              IExtendService tmp = (IExtendService)clazz.newInstance();
              List list = ExtendManager.getInstance().findItems(this.id);
              if (ObjectUtil.notEmpty(list)) {
                for (ExtendItemConfig item : list) {
                  try {
                    tmp.register(item.getInstance());
                  } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Load ExtendItem " + item.getClassName() + " failed!");
                  }
                }
              }
              this.instance = tmp;
            } catch (Exception e) {
              e.printStackTrace();
              throw new RuntimeException("ExtendService " + this.className + " must extends IExtendService");
            }
          }
        }
      }
      return this.instance;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
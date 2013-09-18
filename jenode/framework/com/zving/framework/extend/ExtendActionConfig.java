package com.zving.framework.extend;

import com.zving.framework.extend.exception.CreateExtendActionInstanceException;
import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.extend.plugin.PluginException;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.xml.XMLElement;

public class ExtendActionConfig
{
  private boolean enable;
  private PluginConfig pluginConfig;
  private String id;
  private String description;
  private String extendPointID;
  private String className;
  private IExtendAction instance = null;
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
      if (nd.getQName().equalsIgnoreCase("extendPoint")) {
        this.extendPointID = nd.getText().trim();
      }
      if (nd.getQName().equalsIgnoreCase("class")) {
        this.className = nd.getText().trim();
      }
    }
    if (ObjectUtil.empty(this.id))
      throw new PluginException("extendAction's id is empty!");
  }

  public void setEnable(boolean enable)
  {
    this.enable = enable;
  }

  public boolean isEnable() {
    return this.enable;
  }

  public PluginConfig getPluginConfig() {
    return this.pluginConfig;
  }

  public String getID() {
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

  public String getExtendPointID() {
    return this.extendPointID;
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

  public void setExtendPointID(String extendPointID) {
    this.extendPointID = extendPointID;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public IExtendAction getInstance() {
    try {
      if (this.instance == null) {
        synchronized (mutex) {
          if (this.instance == null) {
            Class clazz = Class.forName(this.className);
            ExtendPointConfig ep = ExtendManager.getInstance().findExtendPoint(this.extendPointID);
            if (ep.isChild(clazz)) {
              throw new CreateExtendActionInstanceException("ExtendAction " + this.className + " must extends " + 
                ep.getClassName());
            }
            this.instance = ((IExtendAction)clazz.newInstance());
          }
        }
      }
      return this.instance;
    } catch (Exception e) {
      throw new CreateExtendActionInstanceException(e);
    }
  }
}
package com.zving.framework.extend;

import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.extend.plugin.PluginException;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.xml.XMLElement;

public class ExtendPointConfig
{
  private boolean enable;
  private PluginConfig pluginConfig;
  private String id;
  private String description;
  private String className;
  private boolean UIFlag;
  private Class<?> clazz;

  public void init(PluginConfig pc, XMLElement parent)
    throws PluginException
  {
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
      if (nd.getQName().equalsIgnoreCase("UIFlag")) {
        this.UIFlag = "true".equals(nd.getText().trim());
      }
    }
    if (ObjectUtil.empty(this.id))
      throw new PluginException("extendPoint's id is empty!");
  }

  public String getID()
  {
    return this.id;
  }

  public boolean getUIFlag() {
    return this.UIFlag;
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

  public void setUIFlag(boolean uIFlag) {
    this.UIFlag = uIFlag;
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

  public boolean isChild(Class<?> cls) {
    if (this.className == null) {
      return false;
    }
    if (this.clazz == null) {
      try {
        this.clazz = Class.forName(this.className);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    return cls.isAssignableFrom(this.clazz);
  }

  public Class<?> getParentClass() throws PluginException {
    if (this.className == null) {
      return null;
    }
    if (this.clazz == null) {
      try {
        this.clazz = Class.forName(this.className);
      } catch (ClassNotFoundException e) {
        throw new PluginException("ExtendPoint's class not found:" + this.className);
      }
    }
    return this.clazz;
  }
}
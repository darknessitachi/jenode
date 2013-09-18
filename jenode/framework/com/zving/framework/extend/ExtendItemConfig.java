package com.zving.framework.extend;

import com.zving.framework.extend.exception.CreateExtendItemInstanceException;
import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.extend.plugin.PluginException;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.xml.XMLElement;

public class ExtendItemConfig
{
  private boolean enable;
  private PluginConfig pluginConfig;
  private String id;
  private String description;
  private String extendServiceID;
  private String className;
  private IExtendItem instance = null;

  public void init(PluginConfig pc, XMLElement parent) throws PluginException {
    this.pluginConfig = pc;
    for (XMLElement nd : parent.elements()) {
      if (nd.getQName().equalsIgnoreCase("id")) {
        this.id = nd.getText().trim();
      }
      if (nd.getQName().equalsIgnoreCase("description")) {
        this.description = nd.getText().trim();
      }
      if (nd.getQName().equalsIgnoreCase("extendService")) {
        this.extendServiceID = nd.getText().trim();
      }
      if (nd.getQName().equalsIgnoreCase("class")) {
        this.className = nd.getText().trim();
      }
    }
    if (ObjectUtil.empty(this.id))
      throw new PluginException("extendItem's id is empty!");
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

  public String getExtendServiceID() {
    return this.extendServiceID;
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

  public void setExtendServiceID(String extendServiceID) {
    this.extendServiceID = extendServiceID;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public IExtendItem getInstance() {
    try {
      if (this.instance == null) {
        Class clazz = Class.forName(this.className);
        try {
          this.instance = ((IExtendItem)clazz.newInstance());
        } catch (Exception e) {
          e.printStackTrace();
          throw new CreateExtendItemInstanceException("ExtendItem " + this.className + " must implements IExtendItem");
        }
      }
      return this.instance;
    } catch (Exception e) {
      throw new CreateExtendItemInstanceException(e);
    }
  }
}
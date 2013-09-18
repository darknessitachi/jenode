package com.zving.framework.extend.menu;

import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.extend.plugin.PluginException;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.xml.XMLElement;

public class Menu
{
  public static final String Type_Backend = "Backend";
  public static final String Type_Frontend = "Frontend";
  private String ID;
  private String parentID;
  private String description;
  private String name;
  private String icon;
  private String order;
  private String URL;
  private String type;
  private PluginConfig config;

  public void parse(PluginConfig pc, XMLElement parent)
    throws PluginException
  {
    this.config = pc;
    for (XMLElement nd : parent.elements()) {
      if (nd.getQName().equalsIgnoreCase("id")) {
        this.ID = nd.getText().trim();
      }
      if (nd.getQName().equalsIgnoreCase("parentId")) {
        this.parentID = nd.getText().trim();
      }
      if (nd.getQName().equalsIgnoreCase("description")) {
        this.description = nd.getText().trim();
      }
      if (nd.getQName().equalsIgnoreCase("name")) {
        this.name = nd.getText().trim();
      }
      if (nd.getQName().equalsIgnoreCase("icon")) {
        this.icon = nd.getText().trim();
      }
      if (nd.getQName().equalsIgnoreCase("URL")) {
        this.URL = nd.getText().trim();
      }
      if (nd.getQName().equalsIgnoreCase("order")) {
        this.order = nd.getText().trim();
      }
      if (nd.getQName().equalsIgnoreCase("type")) {
        this.type = nd.getText().trim();
      }
    }
    if (ObjectUtil.empty(this.ID)) {
      throw new PluginException("menu's id is empty!");
    }
    if (ObjectUtil.empty(this.name))
      throw new PluginException("menu's name is empty!");
  }

  public PluginConfig getPluginConfig()
  {
    return this.config;
  }

  public String getID() {
    return this.ID;
  }

  public String getParentID() {
    return this.parentID;
  }

  public String getName() {
    return this.name;
  }

  public String getName(String language) {
    if (this.name == null) {
      return null;
    }
    return LangUtil.get(this.name, language);
  }

  public String getURL() {
    return this.URL;
  }

  public String getType() {
    return this.type;
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

  public String getIcon() {
    return this.icon;
  }

  public String getOrder() {
    return this.order;
  }

  public void setPluginConfig(PluginConfig pc) {
    this.config = pc;
  }

  public void setID(String id) {
    this.ID = id;
  }

  public void setParentID(String parentId) {
    this.parentID = parentId;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public void setOrder(String order) {
    this.order = order;
  }

  public void setURL(String uRL) {
    this.URL = uRL;
  }

  public void setType(String type) {
    this.type = type;
  }
}
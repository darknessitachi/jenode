package com.zving.framework.extend;

import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.extend.plugin.PluginException;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.xml.XMLElement;

public class OptionalItemConfig
{
  private String ID;
  private String targetID;
  private String className;
  private String description;
  private PluginConfig pluginConfig;

  public PluginConfig getPluginConfig()
  {
    return this.pluginConfig;
  }

  public void init(PluginConfig pc, XMLElement parent) throws PluginException {
    this.pluginConfig = pc;
    for (XMLElement nd : parent.elements()) {
      if (nd.getQName().equalsIgnoreCase("id")) {
        this.ID = nd.getText().trim();
      }
      if (nd.getQName().equalsIgnoreCase("description")) {
        this.description = nd.getText().trim();
      }
      if (nd.getQName().equalsIgnoreCase("class")) {
        this.className = nd.getText().trim();
      }
      if (nd.getQName().equalsIgnoreCase("targetID")) {
        this.targetID = nd.getText().trim();
      }
    }
    if (ObjectUtil.empty(this.ID))
      throw new PluginException("optionalItem's id is empty!");
  }

  public String getID()
  {
    return this.ID;
  }

  public String getClassName() {
    return this.className;
  }

  public String getDescription() {
    return this.description;
  }

  public String getTargetID() {
    return this.targetID;
  }
}
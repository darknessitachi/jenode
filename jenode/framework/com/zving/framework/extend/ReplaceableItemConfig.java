package com.zving.framework.extend;

import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.extend.plugin.PluginException;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.xml.XMLElement;

public class ReplaceableItemConfig
{
  public static final int SCOPE_SINGLETON = 1;
  public static final int SCOPE_REQUEST = 2;
  public static final int SCOPE_THREADLOCAL = 3;
  public static final int SCOPE_SESSION = 4;
  public static final int SCOPE_ALWAYSCREATE = 4;
  private String ID;
  private String className;
  private String description;
  private PluginConfig pluginConfig;
  private int scope = 1;

  public PluginConfig getPluginConfig() {
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
      if (nd.getQName().equalsIgnoreCase("scope")) {
        this.scope = Integer.parseInt(nd.getText().trim());
      }
    }
    if (ObjectUtil.empty(this.ID))
      throw new PluginException("replaceableItem's id is empty!");
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

  public int getScope() {
    return this.scope;
  }

  public boolean isSingleton() {
    return this.scope == 1;
  }

  public boolean isSessionScope() {
    return this.scope == 4;
  }

  public boolean isThreadLocalScope() {
    return this.scope == 3;
  }

  public boolean isRequestScope() {
    return this.scope == 2;
  }

  public boolean isAlwaysCreate() {
    return this.scope == 4;
  }
}
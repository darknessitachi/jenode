package com.zving.framework.core.asm;

import java.util.HashMap;
import java.util.Map;

public class AbstractAccess
{
  protected HashMap<String, Object> alias = new HashMap();

  public void addAlias(String name, Object value) {
    this.alias.put(name, value);
  }

  public boolean hasAlias(String name) {
    return this.alias.containsKey(this.alias);
  }

  public Object getAliasValue(String name) {
    return this.alias.get(name);
  }

  public Map<String, Object> getAllAlias() {
    return this.alias;
  }
}
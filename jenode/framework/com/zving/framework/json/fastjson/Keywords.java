package com.zving.framework.json.fastjson;

import java.util.HashMap;
import java.util.Map;

public class Keywords
{
  private final Map<String, Integer> keywords;
  public static Keywords DEFAULT_KEYWORDS = new Keywords(map);

  static
  {
    Map map = new HashMap();
    map.put("null", Integer.valueOf(8));
    map.put("new", Integer.valueOf(9));
    map.put("true", Integer.valueOf(6));
    map.put("false", Integer.valueOf(7));
  }

  public Keywords(Map<String, Integer> keywords)
  {
    this.keywords = keywords;
  }

  public Integer getKeyword(String key) {
    return (Integer)this.keywords.get(key);
  }
}
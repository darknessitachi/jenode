package com.zving.framework.json;

import com.zving.framework.json.convert.IJSONConvertor;
import com.zving.framework.json.convert.JSONConvertorService;
import com.zving.framework.json.fastjson.DefaultJSONParser;
import com.zving.framework.utility.FastStringBuilder;
import com.zving.framework.utility.ObjectUtil;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class JSON
{
  public static String toJSONString(Object value)
  {
    FastStringBuilder sb = new FastStringBuilder();
    toJSONString(value, sb);
    String json = sb.toString();
    sb.close();
    return json;
  }

  public static void toJSONString(Object value, FastStringBuilder sb) {
    if (value == null) {
      sb.append("null");
      return;
    }
    if ((value instanceof String)) {
      sb.append("\"");
      escape((String)value, sb);
      sb.append("\"");
      return;
    }
    if ((value instanceof Integer)) {
      sb.append(((Integer)value).intValue());
      return;
    }
    if ((value instanceof Long)) {
      sb.append(((Long)value).longValue());
      return;
    }
    if ((value instanceof Double)) {
      if ((((Double)value).isInfinite()) || (((Double)value).isNaN()))
        sb.append("null");
      else {
        sb.append(value.toString());
      }
      return;
    }
    if ((value instanceof Float)) {
      if ((((Float)value).isInfinite()) || (((Float)value).isNaN()))
        sb.append("null");
      else {
        sb.append(value.toString());
      }
      return;
    }
    if ((value instanceof Number)) {
      sb.append(value.toString());
      return;
    }
    if ((value instanceof Boolean)) {
      sb.append(value.toString());
      return;
    }
    if ((value instanceof Date)) {
      sb.append(String.valueOf(((Date)value).getTime()));
      return;
    }
    if (value.getClass().isArray()) {
      Object[] arr = (Object[])value;
      toJSONString(ObjectUtil.toList(arr), sb);
      return;
    }
    if ((value instanceof Map)) {
      toJSONString((Map)value, sb);
      return;
    }
    if ((value instanceof List)) {
      List list = (List)value;
      toJSONString(list, sb);
      return;
    }
    if ((value instanceof JSONAware)) {
      sb.append(((JSONAware)value).toJSONString());
      return;
    }
    for (IJSONConvertor rev : JSONConvertorService.getInstance().getAll()) {
      if (rev.match(value)) {
        JSONObject obj = rev.toJSON(value);
        obj.put("@type", rev.getTypeID());
        sb.append(obj.toJSONString());
        return;
      }
    }
    sb.append(value.toString());
  }

  private static void toJSONString(List<?> list, FastStringBuilder sb) {
    if (list == null) {
      sb.append("null");
    }
    boolean first = true;
    sb.append('[');
    for (Iterator localIterator = list.iterator(); localIterator.hasNext(); ) { Object value = localIterator.next();
      if (first)
        first = false;
      else {
        sb.append(',');
      }
      if (value == null) {
        sb.append("null");
      }
      else
        toJSONString(value, sb);
    }
    sb.append(']');
  }

  private static void toJSONString(Map<?, ?> map, FastStringBuilder sb) {
    if (map == null) {
      sb.append("null");
      return;
    }

    boolean first = true;
    sb.append('{');
    for (Map.Entry entry : map.entrySet()) {
      if (first)
        first = false;
      else
        sb.append(',');
      toJSONString(String.valueOf(entry.getKey()), entry.getValue(), sb);
    }
    sb.append('}');
  }

  private static void toJSONString(String key, Object value, FastStringBuilder sb) {
    sb.append('"');
    if (key == null)
      sb.append("null");
    else
      escape(key, sb);
    sb.append('"').append(':');
    toJSONString(value, sb);
  }

  static void escape(String s, FastStringBuilder sb) {
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      switch (ch) {
      case '"':
        sb.append("\\\"");
        break;
      case '\\':
        sb.append("\\\\");
        break;
      case '\b':
        sb.append("\\b");
        break;
      case '\f':
        sb.append("\\f");
        break;
      case '\n':
        sb.append("\\n");
        break;
      case '\r':
        sb.append("\\r");
        break;
      case '\t':
        sb.append("\\t");
        break;
      default:
        if (((ch >= 0) && (ch <= '\037')) || ((ch >= '') && (ch <= '')) || ((ch >= ' ') && (ch <= '⃿'))) {
          String ss = Integer.toHexString(ch);
          sb.append("\\u");
          for (int k = 0; k < 4 - ss.length(); k++) {
            sb.append('0');
          }
          sb.append(ss.toUpperCase());
        } else {
          sb.append(ch);
        }break;
      }
    }
  }

  public static Object parse(String json) {
    if (json == null) {
      return json;
    }
    DefaultJSONParser parser = new DefaultJSONParser(json);
    Object v = parser.parse();
    parser.close();
    return v;
  }

  public static Object tryReverse(JSONObject obj) {
    String type = obj.getString("@type");
    if (type == null) {
      return obj;
    }
    IJSONConvertor rev = (IJSONConvertor)JSONConvertorService.getInstance().get(type);
    if (rev == null) {
      return obj;
    }
    return rev.fromJSON(obj);
  }
}
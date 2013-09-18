package com.zving.framework.data;

import com.zving.framework.collection.Mapx;
import com.zving.framework.json.JSON;
import com.zving.framework.orm.DAO;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.orm.DAOUtil;
import com.zving.framework.utility.DateUtil;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.framework.xml.XMLDocument;
import com.zving.framework.xml.XMLElement;
import com.zving.framework.xml.XMLParser;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataCollection extends Mapx<String, Object>
{
  private static final long serialVersionUID = 1L;
  private static Pattern scriptPattern = Pattern.compile("script", 34);

  public Object get(Object id)
  {
    return super.get(id);
  }

  public int[] getIntArray(String id) {
    Object o = super.get(id);
    if ((o != null) && ((o instanceof int[]))) {
      try {
        return (int[])o;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public long[] getLongArray(String id) {
    Object o = super.get(id);
    if ((o != null) && ((o instanceof long[]))) {
      try {
        return (long[])o;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public float[] getFloatArray(String id) {
    Object o = super.get(id);
    if ((o != null) && ((o instanceof float[]))) {
      try {
        return (float[])o;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public double[] getDoubleArray(String id) {
    Object o = super.get(id);
    if ((o != null) && ((o instanceof double[]))) {
      try {
        return (double[])o;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public int getInt(String id) {
    return super.getInt(id);
  }

  public long getLong(String id) {
    return super.getLong(id);
  }

  public double getDouble(String id) {
    Object o = super.get(id);
    if (Double.class.isInstance(o)) {
      return ((Double)o).doubleValue();
    }
    throw new RuntimeException("Date type is not double，id=" + id);
  }

  public float getFloat(String id) {
    Object o = super.get(id);
    if (Float.class.isInstance(o)) {
      return ((Float)o).floatValue();
    }
    throw new RuntimeException("Date type is not float，id=" + id);
  }

  public String getString(String id) {
    return super.getString(id);
  }

  public DataTable getDataTable(String id) {
    Object o = super.get(id);
    if (DataTable.class.isInstance(o)) {
      return (DataTable)o;
    }
    return null;
  }

  public DAO<?> getDAO(String id) {
    Object o = super.get(id);
    if (DAO.class.isInstance(o)) {
      return (DAO)o;
    }
    return null;
  }

  public DAOSet<?> getDAOSet(String id) {
    Object o = super.get(id);
    if (DAOSet.class.isInstance(o)) {
      return (DAOSet)o;
    }
    return null;
  }

  public String toXML()
  {
    XMLDocument doc = new XMLDocument();
    doc.setEncoding("UTF-8");
    XMLElement root = doc.createRoot("collection");
    for (String id : keySet()) {
      Object value = get(id);
      XMLElement ele = root.addElement("element");
      ele.addAttribute("id", id);
      if ((value == null) || (value.equals(""))) {
        ele.addAttribute("type", "String");
        ele.addCDATA("_ZVING_NULL");
      }
      else if ((value instanceof String)) {
        ele.addAttribute("type", "String");
        ele.addCDATA((String)value);
      } else if ((value instanceof Integer)) {
        ele.addAttribute("type", "Int");
        ele.addAttribute("value", String.valueOf(value));
      } else if ((value instanceof Date)) {
        ele.addAttribute("type", "String");
        ele.addAttribute("value", DateUtil.toDateTimeString((Date)value));
      } else if ((value instanceof Long)) {
        ele.addAttribute("type", "Long");
        ele.addAttribute("value", String.valueOf(value));
      } else if ((value instanceof Float)) {
        ele.addAttribute("type", "Float");
        ele.addAttribute("value", String.valueOf(value));
      } else if ((value instanceof Double)) {
        ele.addAttribute("type", "Double");
        ele.addAttribute("value", String.valueOf(value));
      } else if ((value instanceof int[])) {
        int[] t = (int[])value;
        StringBuilder sb = new StringBuilder();
        sb.append(t[0]);
        for (int j = 1; j < t.length; j++) {
          sb.append(",");
          sb.append(t[j]);
        }
        ele.addAttribute("type", "IntArray");
        ele.addAttribute("value", sb.toString());
      } else if ((value instanceof long[])) {
        long[] t = (long[])value;
        StringBuilder sb = new StringBuilder();
        sb.append(t[0]);
        for (int j = 1; j < t.length; j++) {
          sb.append(",");
          sb.append(t[j]);
        }
        ele.addAttribute("type", "LongArray");
        ele.addAttribute("value", sb.toString());
      } else if ((value instanceof float[])) {
        float[] t = (float[])value;
        StringBuilder sb = new StringBuilder();
        sb.append(t[0]);
        for (int j = 1; j < t.length; j++) {
          sb.append(",");
          sb.append(t[j]);
        }
        ele.addAttribute("type", "FloatArray");
        ele.addAttribute("value", sb.toString());
      } else if ((value instanceof double[])) {
        double[] t = (double[])value;
        StringBuilder sb = new StringBuilder();
        sb.append(t[0]);
        for (int j = 1; j < t.length; j++) {
          sb.append(",");
          sb.append(t[j]);
        }
        ele.addAttribute("type", "DoubleArray");
        ele.addAttribute("value", sb.toString());
      } else if ((value instanceof String[])) {
        String[] t = (String[])value;
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < t.length; j++) {
          if (j != 0) {
            sb.append(",");
          }
          sb.append("\"");
          if (t[j] == null) {
            t[j] = "_ZVING_NULL";
          }
          sb.append(StringUtil.javaEncode(t[j]));
          sb.append("\"");
        }
        ele.addAttribute("type", "StringArray");
        ele.addCDATA(sb.toString());
      } else if ((value instanceof DataTable)) {
        dataTableToXML((DataTable)value, ele);
      } else if ((value instanceof DAO)) {
        daoToXML((DAO)value, ele);
      } else if ((value instanceof DAOSet)) {
        daoSetToXML((DAOSet)value, ele);
      } else if ((value instanceof Mapx)) {
        mapToXML((Mapx)value, ele);
      }
    }
    return doc.asXML();
  }

  public String toJSON() {
    return JSON.toJSONString(this);
  }

  private void dataTableToXML(DataTable dt, XMLElement ele) {
    translateDataTableToXML(dt, ele, "DataTable");
  }

  private void translateDataTableToXML(DataTable dt, XMLElement ele, String type)
  {
    if (dt == null) {
      throw new RuntimeException("DataTable can't be null!");
    }
    ele.addAttribute("type", type);
    DataColumn[] dcs = dt.getDataColumns();
    XMLElement cols = ele.addElement("columns");
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < dcs.length; i++) {
      if (i != 0) {
        sb.append(",");
      }
      sb.append("[\"");
      sb.append(dcs[i].getColumnName());
      sb.append("\"");
      sb.append(",");
      sb.append(dcs[i].getColumnType());
      sb.append("]");
    }
    sb.append("]");
    cols.addCDATA(sb.toString());
    sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < dt.getRowCount(); i++) {
      sb.append("[");
      for (int j = 0; j < dcs.length; j++) {
        String v = dt.getString(i, j);
        if (j == 0) {
          if (v != null)
          {
            sb.append("\"");
            sb.append(StringUtil.javaEncode(v));
            sb.append("\"");
          }
        } else if (v == null) {
          sb.append(",");
        }
        else {
          sb.append(",\"");
          sb.append(StringUtil.javaEncode(v));
          sb.append("\"");
        }
      }
      if (i == dt.getRowCount() - 1)
        sb.append("]");
      else {
        sb.append("],\n");
      }
    }
    sb.append("]");
    XMLElement value = ele.addElement("values");
    value.addCDATA(sb.toString());
  }

  public static String dataTableToJS(DataTable dt) {
    if (dt == null) {
      throw new RuntimeException("DataTable cann't be null!");
    }
    boolean webModeFlag = dt.isWebMode();
    dt.setWebMode(false);
    DataColumn[] dcs = dt.getDataColumns();
    StringBuilder sb = new StringBuilder();

    sb.append("var _Zving_Cols = [");
    for (int i = 0; i < dcs.length; i++) {
      if (i != 0) {
        sb.append(",");
      }
      sb.append("[\"");
      sb.append(dcs[i].getColumnName());
      sb.append("\"");
      sb.append(",");
      sb.append(dcs[i].getColumnType());
      sb.append("]");
    }
    sb.append("];\n");
    sb.append("var _Zving_Values = [");
    for (int i = 0; i < dt.getRowCount(); i++) {
      sb.append("[");
      for (int j = 0; j < dcs.length; j++) {
        String v = dt.getString(i, j);
        int ct = dcs[j].ColumnType;
        if (j != 0) {
          sb.append(",");
        }
        if (v == null) {
          sb.append("null");
        }
        else if (v.trim().length() == 0) {
          sb.append("\"\"");
        }
        else if ((ct == 1) || (ct == 10)) {
          String txt = StringUtil.javaEncode(v);
          txt = StringUtil.replaceEx(txt, "<", "\\<");
          txt = StringUtil.replaceEx(txt, ">", "\\>");
          txt = scriptPattern.matcher(txt).replaceAll("scr\"+\"ipt");
          sb.append("\"");
          sb.append(txt);
          sb.append("\"");
        } else if (ct == 12) {
          sb.append("\"");
          sb.append(v);
          sb.append("\"");
        } else {
          sb.append(v);
        }
      }
      if (i == dt.getRowCount() - 1)
        sb.append("]");
      else {
        sb.append("],\n");
      }
    }
    dt.setWebMode(webModeFlag);
    sb.append("];\n");
    return sb.toString();
  }

  private <T extends DAO<T>> void daoToXML(T DAO, XMLElement ele) {
    try {
      DAOSet set = DAO.newSet();
      set.add(DAO);
      DataTable dt = set.toDataTable();
      ele.addAttribute("tablecode", DAOUtil.getTableCode(DAO));
      translateDataTableToXML(dt, ele, "DAO");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void daoSetToXML(DAOSet<?> set, XMLElement ele) {
    if ((set != null) && (set.size() != 0)) {
      DataTable dt = set.toDataTable();
      ele.addAttribute("tablecode", DAOUtil.getTableCode(set.get(0)));
      translateDataTableToXML(dt, ele, "DAOSet");
    }
  }

  public void write(String fileName) {
    FileUtil.writeText(fileName, toXML(), "UTF-8");
  }

  public void parseXML(String xml)
  {
    if ((xml == null) || (xml.length() == 0)) {
      throw new RuntimeException("XML string can't be null!");
    }
    xml = StringUtil.replaceEx(xml, StringUtil.urlDecode("%C2%A0", "UTF-8"), " ");
    xml = StringUtil.clearForXML(xml);
    XMLParser parser = new XMLParser(xml);
    try {
      XMLElement root = parser.getDocument().getRoot();
      List elements = root.elements();
      for (int i = 0; i < elements.size(); i++) {
        XMLElement ele = (XMLElement)elements.get(i);
        String id = ele.attributeValue("id");
        String type = ele.attributeValue("type");
        if (type.equals("String")) {
          String str = ele.getText();
          if ("_ZVING_NULL".equals(str)) {
            str = null;
          }
          put(id, str);
        } else if (type.equals("Int")) {
          String str = ele.attributeValue("value");
          put(id, Integer.valueOf(Integer.parseInt(str)));
        } else if (type.equals("Long")) {
          String str = ele.attributeValue("value");
          put(id, Long.valueOf(Long.parseLong(str)));
        } else if (type.equals("Float")) {
          String str = ele.attributeValue("value");
          put(id, Float.valueOf(Float.parseFloat(str)));
        } else if (type.equals("Double")) {
          String str = ele.attributeValue("value");
          put(id, Double.valueOf(Double.parseDouble(str)));
        } else if (type.equals("StringArray")) {
          String str = ele.getText();
          String[] t = StringUtil.splitEx(str, "\",\"");
          for (int j = 0; j < t.length; j++) {
            t[j] = StringUtil.javaDecode(t[j]);
          }
          put(id, t);
        } else if (type.equals("IntArray")) {
          String str = ele.attributeValue("value");
          String[] t = str.split(",");
          int[] a = new int[t.length];
          for (int j = 0; j < t.length; j++) {
            a[j] = Integer.parseInt(t[j]);
          }
          put(id, a);
        } else if (type.equals("LongArray")) {
          String str = ele.attributeValue("value");
          String[] t = str.split(",");
          long[] a = new long[t.length];
          for (int j = 0; j < t.length; j++) {
            a[j] = Long.parseLong(t[j]);
          }
          put(id, a);
        } else if (type.equals("FloatArray")) {
          String str = ele.attributeValue("value");
          String[] t = str.split(",");
          float[] a = new float[t.length];
          for (int j = 0; j < t.length; j++) {
            a[j] = Float.parseFloat(t[j]);
          }
          put(id, a);
        } else if (type.equals("DoubleArray")) {
          String str = ele.attributeValue("value");
          String[] t = str.split(",");
          double[] a = new double[t.length];
          for (int j = 0; j < t.length; j++) {
            a[j] = Double.parseDouble(t[j]);
          }
          put(id, a);
        } else if (type.equals("DataTable")) {
          DataTable dt = parseDataTable(ele);
          put(id, dt);
        } else if (type.equals("Map")) {
          Mapx map = parseMap(ele);
          put(id, map);
        } else if (type.equals("DAO")) {
          Class c = Class.forName(ele.attributeValue("ClassName"));
          DAO DAO = (DAO)c.newInstance();
          DataTable dt = parseDataTable(ele);
          for (int j = 0; j < DAOUtil.getColumns(DAO).length; j++) {
            DAO.setV(i, dt.get(0, j));
          }
          put(id, DAO);
        } else if (type.equals("DAOSet")) {
          Class c = Class.forName(ele.attributeValue("ClassName"));
          DataTable dt = parseDataTable(ele);
          DAOSet set = ((DAO)c.newInstance()).newSet();
          for (int j = 0; j < dt.getRowCount(); j++) {
            DAO dao = (DAO)c.newInstance();
            for (int k = 0; k < DAOUtil.getColumns(dao).length; k++) {
              dao.setV(i, dt.get(j, k));
            }
            set.addDAO(dao);
          }
          put(id, set);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void parseJSON(String json) {
    if ((json == null) || (json.length() == 0)) {
      throw new RuntimeException("JSON string can't be null!");
    }
    json = StringUtil.replaceEx(json, StringUtil.urlDecode("%C2%A0", "UTF-8"), " ");
    json = StringUtil.clearForXML(json);
    try {
      Object obj = JSON.parse(json);

      Map map = (Map)obj;
      for (Iterator localIterator = map.keySet().iterator(); localIterator.hasNext(); ) { Object k = localIterator.next();
        put((String)k, map.get(k)); }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Mapx<String, String> parseMap(XMLElement ele) {
    Mapx map = new Mapx();
    String str = ele.getText();
    str = str.trim().substring(1, str.length() - 1);
    String[] arr = StringUtil.splitEx(str, ",\"");
    for (int i = 0; i < arr.length; i++) {
      String[] arr2 = StringUtil.splitEx(arr[i], "\":");
      if (arr2.length == 2)
      {
        String k = arr2[0];
        String v = arr2[1];
        if (k.startsWith("\"")) {
          k = k.substring(1);
        }
        if (k.endsWith("\"")) {
          k = k.substring(0, k.length() - 1);
        }
        if (v.startsWith("\"")) {
          v = v.substring(1);
        }
        if (v.endsWith("\"")) {
          v = v.substring(0, v.length() - 1);
        }
        map.put(k, v);
      }
    }
    return map;
  }

  private void mapToXML(Mapx<?, ?> map, XMLElement ele) {
    ele.addAttribute("type", "Map");
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    int i = 0;
    for (Iterator localIterator = map.keySet().iterator(); localIterator.hasNext(); ) { Object key = localIterator.next();
      if (i != 0) {
        sb.append(",");
      }
      sb.append(key);
      sb.append(":\"");
      sb.append(StringUtil.javaEncode(String.valueOf(map.get(key))));
      sb.append("\"");
      i++;
    }
    sb.append("}");
    ele.addCDATA(sb.toString());
  }

  private DataTable parseDataTable(XMLElement ele) {
    String str = ele.element("columns").getText();
    String[] t = StringUtil.splitEx(str, "],[");
    DataColumn[] dcs = new DataColumn[t.length];
    t[0] = t[0].substring(2);
    int length = t.length;
    t[(length - 1)] = t[(length - 1)].substring(0, t[(length - 1)].length() - 2);
    for (int i = 0; i < t.length; i++) {
      dcs[i] = new DataColumn();
      int index = t[i].lastIndexOf(",");
      dcs[i].setColumnName(t[i].substring(1, index - 1));
      dcs[i].setColumnType(Integer.parseInt(t[i].substring(index + 1)));
    }
    String value = ele.element("values").getText();
    t = value.split("\\\"\\]\\,\\s*?\\[\\\"");
    if (t[0].equals("[]")) {
      return new DataTable(dcs, null);
    }
    t[0] = t[0].substring(3);
    length = t.length;
    t[(length - 1)] = t[(length - 1)].substring(0, t[(length - 1)].length() - 3);

    Object[][] values = new Object[length][dcs.length];
    for (int i = 0; i < t.length; i++) {
      String[] r = t[i].split("\",\"");
      for (int j = 0; j < r.length; j++) {
        if ((dcs[j].getColumnType() == 1) || (dcs[j].getColumnType() == 10)) {
          values[i][j] = StringUtil.javaDecode(r[j]);
          if ((r[j].equals("_ZVING_NULL")) || (StringUtil.isEmpty(r[j]))) {
            values[i][j] = null;
          }
        }
        else if ((r[j].equals("_ZVING_NULL")) || (StringUtil.isEmpty(r[j]))) {
          values[i][j] = null;
        } else if (dcs[j].getColumnType() == 3) {
          values[i][j] = new Double(Double.parseDouble(r[j]));
        } else if (dcs[j].getColumnType() == 12) {
          values[i][j] = DateUtil.parse(r[j]);
        } else if (dcs[j].getColumnType() == 4) {
          values[i][j] = new Double(Double.parseDouble(r[j]));
        } else if (dcs[j].getColumnType() == 6) {
          values[i][j] = new Double(Double.parseDouble(r[j]));
        } else if (dcs[j].getColumnType() == 5) {
          values[i][j] = new Float(Float.parseFloat(r[j]));
        } else if (dcs[j].getColumnType() == 8) {
          values[i][j] = new Integer(Integer.parseInt(r[j]));
        } else if (dcs[j].getColumnType() == 7) {
          values[i][j] = new Long(Long.parseLong(r[j]));
        } else if (dcs[j].getColumnType() == 9) {
          values[i][j] = new Integer(Integer.parseInt(r[j]));
        }
      }
    }

    return new DataTable(dcs, values);
  }
}
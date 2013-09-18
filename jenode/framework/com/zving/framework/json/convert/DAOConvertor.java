package com.zving.framework.json.convert;

import com.zving.framework.json.JSONObject;
import com.zving.framework.orm.DAO;
import com.zving.framework.orm.DAOColumn;

public class DAOConvertor
  implements IJSONConvertor
{
  public String getTypeID()
  {
    return "DAO";
  }

  public String getID() {
    return getTypeID();
  }

  public String getName() {
    return getID();
  }

  public boolean match(Object obj) {
    return obj instanceof DAO;
  }

  public JSONObject toJSON(Object obj) {
    JSONObject jo = new JSONObject();
    DAO dao = (DAO)obj;
    for (DAOColumn dc : dao.columns()) {
      jo.put(dc.getColumnName(), dao.getV(dc.getColumnName()));
    }
    jo.put("_DAOClass", obj.getClass().getName());
    return jo;
  }

  public Object fromJSON(JSONObject map) {
    String className = map.getString("_DAOClass");
    try {
      DAO dao = (DAO)Class.forName(className).newInstance();
      for (String k : map.keyArray()) {
        dao.setV(k, map.get(k));
      }
      return dao;
    } catch (Exception e) {
      throw new JSONConvertException(e);
    }
  }
}
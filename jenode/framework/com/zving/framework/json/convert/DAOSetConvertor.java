package com.zving.framework.json.convert;

import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.json.JSONObject;
import com.zving.framework.orm.DAO;
import com.zving.framework.orm.DAOSet;

public class DAOSetConvertor
  implements IJSONConvertor
{
  public String getTypeID()
  {
    return "DAOSet";
  }

  public String getID() {
    return getTypeID();
  }

  public String getName() {
    return getID();
  }

  public boolean match(Object obj) {
    return obj instanceof DAOSet;
  }

  public JSONObject toJSON(Object obj) {
    JSONObject jo = new JSONObject();
    DAOSet set = (DAOSet)obj;
    DataTable dt = set.toDataTable();
    jo.put("_DAOClass", obj.getClass().getName());
    jo.put("Data", new DataTableConvertor().toJSON(dt));
    return jo;
  }

  public Object fromJSON(JSONObject map)
  {
    String className = map.getString("_DAOClass");
    try {
      DAO dao = (DAO)Class.forName(className).newInstance();
      DAOSet set = dao.newSet();
      DataTable dt = DataTableConvertor.reverse((JSONObject)map.get("Data"));
      for (DataRow dr : dt) {
        dao = dao.newInstance();
        dao.setValue(dr);
        set.add(dao);
      }
      return set;
    } catch (Exception e) {
      throw new JSONConvertException(e);
    }
  }
}
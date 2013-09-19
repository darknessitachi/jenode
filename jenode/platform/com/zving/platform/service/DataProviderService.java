package com.zving.platform.service;

import com.zving.framework.data.DataColumn;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.extend.AbstractExtendService;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.IDataProvider;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;

public class DataProviderService extends AbstractExtendService<IDataProvider>
{
  public static DataProviderService getInstance()
  {
    DataProviderService dts = (DataProviderService)findInstance(DataProviderService.class);
    return dts;
  }

  public static String dataTableToJSON(String columns, DataTable dt) {
    if (StringUtil.isNotNull(columns))
    {
      String cols = "," + columns.toLowerCase() + ",";
      for (int i = dt.getColumnCount() - 1; i >= 0; i--) {
        if (cols.indexOf("," + dt.getDataColumn(i).getColumnName().toLowerCase() + ",") <= -1)
        {
          dt.deleteColumn(i);
        }
      }
    }
    List list = new ArrayList();
    for (DataRow dr : dt) {
      list.add(dr.toCaseIgnoreMapx());
    }
    return JSONArray.toJSONString(list);
  }
}
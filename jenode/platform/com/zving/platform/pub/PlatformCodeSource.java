package com.zving.platform.pub;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.ui.CodeSource;
import com.zving.framework.utility.StringUtil;

public class PlatformCodeSource extends CodeSource
{
  public DataTable getCodeData(String codeType, Mapx<String, Object> params)
  {
    DataTable dt = null;
    String conditionField = params.getString("ConditionField");
    String conditionValue = params.getString("ConditionValue");
    if ("District".equals(codeType)) {
      Q qb = new Q("select code,name from ZDDistrict where " + conditionField + "=?", new Object[] { conditionValue });
      String parentCode = params.getString("ParentCode");
      if (StringUtil.isNotEmpty(parentCode)) {
        qb.append(" and Code like ?", new Object[0]);

        if ((parentCode.startsWith("11")) || (parentCode.startsWith("12")) || (parentCode.startsWith("31")) || 
          (parentCode.startsWith("50"))) {
          qb.add(new Object[] { parentCode.substring(0, 2) + "%" });
          qb.append(" and TreeLevel=3", new Object[0]);
        } else if (parentCode.endsWith("0000")) {
          qb.add(new Object[] { parentCode.substring(0, 2) + "%" });
          qb.append(" and TreeLevel=2", new Object[0]);
        } else if (parentCode.endsWith("00")) {
          qb.add(new Object[] { parentCode.substring(0, 4) + "%" });
          qb.append(" and TreeLevel=3", new Object[0]);
        } else {
          qb.add(new Object[] { "#" });
        }
      } else if (conditionField.equals("1")) {
        return new DataTable();
      }
      dt = qb.fetch();
    } else if ("User".equals(codeType)) {
      Q qb = new Q("select UserName,UserName as 'Name',RealName,isBranchAdmin from ZDUser where " + 
        conditionField + "=?", new Object[] { conditionValue });
      dt = qb.fetch();
    } else {
      Mapx map = PlatformUtil.getCodeMap(codeType);
      if (map != null) {
        dt = map.toDataTable();
        LangUtil.decode(dt, "Value");
      }
    }
    return dt;
  }
}
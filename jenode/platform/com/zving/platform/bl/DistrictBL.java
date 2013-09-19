package com.zving.platform.bl;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.schema.ZDDistrict;

public class DistrictBL
{
  static Mapx<String, Object> codeMap;
  static Mapx<String, Object> nameMap;
  static DataTable table;

  public static void generateDistrictJS()
  {
    DAOSet set = new ZDDistrict().query(new Q(
      "where code!='000000' order by code", new Object[0]));

    StringBuffer provinceMap = new StringBuffer();
    StringBuffer cityMap = new StringBuffer();
    StringBuffer districtMap = new StringBuffer();
    provinceMap.append("var provinceMap = [");
    cityMap.append("var cityMap = {\n");
    districtMap.append("var districtMap = {\n");

    boolean firstProv = true;
    boolean firstCity = true;
    boolean firstDistrict = true;
    for (int i = 0; i < set.size(); i++) {
      ZDDistrict d = (ZDDistrict)set.get(i);
      if ("1".equals(d.getType())) {
        if (!firstProv) {
          provinceMap.append(",");
        }
        firstProv = false;
        provinceMap.append("'" + d.getCode() + "','" + d.getName() + "'");

        if (!firstCity) {
          cityMap.append(",\n");
        }
        cityMap.append("'" + d.getCode() + "':[");
        for (int j = i + 1; j < set.size(); j++) {
          d = (ZDDistrict)set.get(j);
          if ("2".equals(d.getType())) {
            firstCity = false;
            if (j == i + 1)
              cityMap.append("'" + d.getCode() + "','" + d.getName() + "'");
            else {
              cityMap.append(",'" + d.getCode() + "','" + d.getName() + "'");
            }

            if (!firstDistrict) {
              districtMap.append(",\n");
            }
            districtMap.append("'" + d.getCode() + "':[");
            for (int k = j + 1; k < set.size(); k++) {
              d = (ZDDistrict)set.get(k);
              if (!"3".equals(d.getType())) break;
              firstDistrict = false;
              if (k == j + 1)
                districtMap.append("'" + d.getCode() + "','" + d.getName() + "'");
              else {
                districtMap.append(",'" + d.getCode() + "','" + d.getName() + "'");
              }

            }

            districtMap.append("]"); } else {
            if (("1".equals(d.getType())) || ("0".equals(d.getType())))
              break;
          }
        }
        cityMap.append("]");
      } else if ("0".equals(d.getType()))
      {
        if (!firstProv) {
          provinceMap.append(",");
        }
        firstProv = false;
        provinceMap.append("'" + d.getCode() + "','" + d.getName() + "'");

        if (!firstCity) {
          cityMap.append(",\n");
        }
        cityMap.append("'" + d.getCode() + "':[");
        for (int j = i; j < i + 1; j++) {
          d = (ZDDistrict)set.get(j);
          if ("0".equals(d.getType())) {
            firstCity = false;
            cityMap.append("'" + d.getCode() + "','" + d.getName() + "'");

            if (!firstDistrict) {
              districtMap.append(",\n");
            }
            districtMap.append("'" + d.getCode() + "':[");
            for (int k = j + 1; k < set.size(); k++) {
              d = (ZDDistrict)set.get(k);
              if (!"3".equals(d.getType())) break;
              firstDistrict = false;
              if (k == j + 1)
                districtMap.append("'" + d.getCode() + "','" + d.getName() + "'");
              else {
                districtMap.append(",'" + d.getCode() + "','" + d.getName() + "'");
              }

            }

            districtMap.append("]"); } else {
            if ("1".equals(d.getType()))
              break;
          }
        }
        cityMap.append("]");
      }
    }

    provinceMap.append("];\n");
    cityMap.append("};\n");
    districtMap.append("};\n");

    String path = Config.getContextRealPath() + "WEB-INF/classes/com/zving/Platform/district.template";
    String func = FileUtil.readText(path);
    String JS = Config.getContextRealPath() + "framework/district.js";
    FileUtil.writeText(JS, provinceMap.toString() + cityMap.toString() + districtMap.toString() + func);
  }

  private static void initCache()
  {
    if (codeMap == null) {
      table = new Q(
        "select Name,Code from ZDDistrict where code like '11%' or code like '12%' or code like '31%' or code like '50%' or treelevel in (1,2,3) order by treelevel,code desc", new Object[0])
        .fetch();
      codeMap = table.toMapx(0, 1);
      nameMap = table.toMapx(1, 0);
    }
  }

  public static String getDistrictName(String code) {
    initCache();
    if (StringUtil.isEmpty(code)) {
      return "未知";
    }
    String district = null;
    if (code.startsWith("00")) {
      district = nameMap.getString(code);
    } else {
      String prov = nameMap.getString(code.substring(0, 2) + "0000");
      if (StringUtil.isNotEmpty(prov)) {
        if ((prov.startsWith("黑龙江")) || (prov.startsWith("内蒙古")))
          prov = prov.substring(0, 3);
        else {
          prov = prov.substring(0, 2);
        }
        if (code.endsWith("0000")) {
          district = prov;
        } else {
          String city = nameMap.getString(code);
          if (city == null) {
            city = nameMap.getString(code.substring(0, 4) + "00");
          }
          district = prov + (city == null ? "" : city);
        }
      }
    }
    if (StringUtil.isEmpty(district)) {
      district = "未知";
    }
    return district;
  }

  public static String getProvinceName(String address)
  {
    initCache();
    for (int j = 0; j < table.getRowCount(); j++) {
      String name = table.getString(j, "Name");
      if (address.indexOf(name) >= 0) {
        return name;
      }
    }
    for (int j = 0; j < table.getRowCount(); j++) {
      String name = table.getString(j, "Name");
      if (address.startsWith(name.substring(0, 2))) {
        return name;
      }
    }
    for (int j = 0; j < table.getRowCount(); j++) {
      String name = table.getString(j, "Name");
      if (address.indexOf(name.substring(0, 2)) >= 0) {
        return name;
      }
    }
    return null;
  }
}
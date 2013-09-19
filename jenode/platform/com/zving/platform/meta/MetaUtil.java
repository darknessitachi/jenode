package com.zving.platform.meta;

import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.ResponseData;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataColumn;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.data.Transaction;
import com.zving.framework.orm.DAO;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.utility.DateUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.code.YesOrNo;
import com.zving.platform.pub.PlatformCache;
import com.zving.schema.ZDMetaColumn;
import com.zving.schema.ZDMetaModel;
import java.util.ArrayList;
import java.util.List;

public class MetaUtil
{
  public static DataTable getControlHTML(long modelID, long groupID)
  {
    DAOSet set = new DAOSet();
    DAOSet all = PlatformCache.getMetaModel(modelID).getColumns();
    for (ZDMetaColumn column : all)
      if (column.getGroupID() == groupID)
      {
        set.add(column);
      }
    DataTable dt = set.toDataTable();
    dt.insertColumn("ControlHTML");
    for (int i = 0; i < dt.getRowCount(); i++) {
      dt.set(i, "ControlHTML", getControlHTML((ZDMetaColumn)set.get(i)));
    }
    return dt;
  }

  public static Mapx<String, ZDMetaColumn> getMapping(long modelID)
  {
    MetaModel mm = PlatformCache.getMetaModel(modelID);
    if (mm == null) {
      return null;
    }
    return mm.getMapping();
  }

  public static DAO<?> newTargetTableInstance(String targetTable) {
    Class metaValueDAO = null;
    try {
      metaValueDAO = Class.forName("com.zving.schema." + targetTable);
    } catch (Exception e) {
      e.printStackTrace();
    }
    DAO dao = null;
    try {
      dao = (DAO)metaValueDAO.newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return dao;
  }

  public static Mapx<String, String> getConverseMapping(long modelID)
  {
    MetaModel mm = PlatformCache.getMetaModel(modelID);
    if (mm == null) {
      return null;
    }
    return mm.getConverseMapping();
  }

  public static Mapx<String, Object> getExtendData(String PKValue, MetaModel mm) {
    Mapx map = new Mapx();
    if (mm == null) {
      return map;
    }
    Q qb = new Q("select * from " + mm.getDAO().getTargetTable() + " where PKValue=? and ModelID=?", new Object[] { PKValue, Long.valueOf(mm.getDAO().getID()) });
    DataTable metaDT = qb.fetch();
    if (metaDT.getRowCount() == 0) {
      return map;
    }
    for (ZDMetaColumn mc : mm.getColumns()) {
      if (mc.getDataType().equals("Datetime"))
        map.put("MetaValue_" + mc.getCode(), 
          DateUtil.toDateTimeString(DateUtil.parseDateTime(metaDT.getString(0, mc.getTargetField()))));
      else {
        map.put("MetaValue_" + mc.getCode(), metaDT.get(0, mc.getTargetField()));
      }
    }
    return map;
  }

  public static Mapx<String, Object> getExtendData(String PKValue, String modelCode) {
    MetaModel mm = MetaModel.load(modelCode);
    return getExtendData(PKValue, mm);
  }

  public static Mapx<String, Object> getExtendData(String PKValue, long modelID) {
    MetaModel mm = PlatformCache.getMetaModel(modelID);
    return getExtendData(PKValue, mm);
  }

  public static void attachExtendData(DataTable dt, String pkStr, String modelCode) {
    MetaModel mm = MetaModel.load(modelCode);
    attachExtendData(dt, pkStr, mm);
  }

  public static void attachExtendData(DataTable dt, String pkStr, long modelID) {
    MetaModel mm = MetaModel.load(modelID);
    attachExtendData(dt, pkStr, mm);
  }

  public static void attachExtendData(DataTable dt, String pkStr, MetaModel mm)
  {
    String[] pks = StringUtil.splitEx(pkStr, ",");
    for (int i = 0; i < pks.length; i++) {
      if (!dt.containsColumn(pks[i])) {
        throw new RuntimeException("附加元数据内容时发生错误，DataTable中没有指定的主键值!");
      }
    }
    for (ZDMetaColumn mc : mm.getColumns())
      if (!dt.containsColumn("MetaValue_" + mc.getCode()))
      {
        dt.insertColumn("MetaValue_" + mc.getCode());
      }
    int size = 20;
    for (int k = 0; size * k < dt.getRowCount(); k++) {
      ArrayList pkValues = new ArrayList();
      for (int i = 0; (i < size) && (i + k * size < dt.getRowCount()); i++) {
        StringBuffer sb = new StringBuffer();
        for (int j = 0; j < pks.length; j++) {
          if (j != 0) {
            sb.append("|");
          }
          sb.append(dt.getString(i + k * size, pks[j]));
        }
        String str = sb.toString();
        if (i != 0) {
          sb.append(",");
        }
        if (str.indexOf("'") >= 0) {
          throw new RuntimeException("附加元数据内容时发生错误，主键值中不能有单引号!");
        }
        pkValues.add(sb.toString());
      }
      Q qb = new Q("select * from " + mm.getDAO().getTargetTable() + " where ModelID=? and PKValue in ('" + 
        StringUtil.join(pkValues, "','") + "')", new Object[] { Long.valueOf(mm.getDAO().getID()) });
      DataTable metaDT = qb.fetch();
      for (int i = 0; i < pkValues.size(); i++) {
        String pk = ((String)pkValues.get(i)).toString();
        for (int j = 0; j < metaDT.getRowCount(); j++)
          if (metaDT.getString(j, "PKValue").equals(pk))
            for (ZDMetaColumn mc : mm.getColumns()) {
              if (mc.getDataType().equals("Date")) {
                metaDT.getDataColumn(mc.getTargetField()).setDateFormat("yyyy-MM-dd");
              }
              dt.set(i + k * size, "MetaValue_" + mc.getCode(), metaDT.getString(j, mc.getTargetField()));
            }
      }
    }
  }

  public static boolean deleteExtendData(String PKValue, long modelID)
  {
    Transaction tran = new Transaction();
    deleteExtendData(tran, PKValue, modelID);
    return tran.commit();
  }

  public static void deleteExtendData(Transaction tran, String PKValue, long modelID) {
    MetaModel mm = PlatformCache.getMetaModel(modelID);
    if (mm == null) {
      return;
    }
    Q qb = new Q("delete from " + mm.getDAO().getTargetTable() + " where ModelID=? and PKValue=?", new Object[] { Long.valueOf(modelID), PKValue });
    tran.add(qb);
  }

  public static boolean backupExtendData(String PKValue, long modelID) {
    Transaction tran = new Transaction();
    backupExtendData(tran, PKValue, modelID);
    return tran.commit();
  }

  public static void backupExtendData(Transaction tran, String PKValue, long modelID) {
    MetaModel mm = PlatformCache.getMetaModel(modelID);
    if (mm == null) {
      return;
    }

    DAO dao = newTargetTableInstance(mm.getDAO().getTargetTable());
    dao.setV("ModelID", Long.valueOf(modelID));
    dao.setV("PKValue", PKValue);
    if (dao.fill())
      tran.deleteAndBackup(dao);
  }

  public static boolean batchDeleteExtendData(String PKValues, long modelID)
  {
    Transaction tran = new Transaction();
    batchDeleteExtendData(tran, PKValues, modelID);
    return tran.commit();
  }

  public static void batchDeleteExtendData(Transaction tran, String PKValues, long modelID) {
    MetaModel mm = PlatformCache.getMetaModel(modelID);
    if (mm == null) {
      return;
    }
    Q qb = new Q("delete from " + mm.getDAO().getTargetTable() + " where ModelID=? and PKValue in (" + PKValues + ")", new Object[] { Long.valueOf(modelID) });
    tran.add(qb);
  }

  private static String getUpdateSQL(String table, List<ZDMetaColumn> cols, long modelID, String pkValue) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    sb.append("update ").append(table).append(" set ");
    for (ZDMetaColumn c : cols) {
      if (!first)
        sb.append(", ");
      else {
        first = false;
      }
      sb.append(c.getTargetField());
      sb.append("=?");
    }
    sb.append(" where ModelID=").append(modelID).append(" and PKValue='").append(pkValue).append("'");
    return sb.toString();
  }

  private static String getInsertSQL(String table, List<ZDMetaColumn> cols, long modelID, String pkValue) {
    StringBuilder sb = new StringBuilder();
    sb.append("insert into ");
    sb.append(table);
    sb.append(" (ModelID,PKValue");
    for (ZDMetaColumn col : cols) {
      sb.append(",").append(col.getTargetField());
    }
    sb.append(") values (").append(modelID).append(",'").append(pkValue).append("'");
    for (int i = 0; i < cols.size(); i++) {
      sb.append(",").append("?");
    }
    sb.append(")");
    return sb.toString();
  }

  public static void saveExtendData(Transaction tran, Mapx<String, Object> map, String PKValue, MetaModel mm) {
    if (mm == null) {
      return;
    }
    Mapx mMap = getMapping(mm.getDAO().getID());
    if (mMap == null) {
      return;
    }
    String targetTable = mm.getDAO().getTargetTable();
    Q qSelect = new Q("select count(1) from " + targetTable + " where ModelID=? and PKValue=?", new Object[] { Long.valueOf(mm.getDAO().getID()), PKValue });
    Q q = new Q();
    if (qSelect.executeInt() > 0)
      q.setSQL(getUpdateSQL(targetTable, mMap.valueArray(), mm.getDAO().getID(), PKValue));
    else {
      q.setSQL(getInsertSQL(targetTable, mMap.valueArray(), mm.getDAO().getID(), PKValue));
    }

    for (String key : mMap.keySet()) {
      Object v = null;
      if (map.containsKey("MetaValue_" + key)) {
        ZDMetaColumn mc = (ZDMetaColumn)mMap.get(key);
        v = map.getString("MetaValue_" + key);
        if (mc.getDataType().equals("Long")) {
          if (ObjectUtil.notEmpty(v))
            v = new Long(map.getLong("MetaValue_" + key));
          else {
            v = null;
          }
        }
        if (mc.getDataType().equals("Double")) {
          if (ObjectUtil.notEmpty(v))
            v = new Double(map.getString("MetaValue_" + key));
          else {
            v = null;
          }
        }
        if (mc.getDataType().equals("Datetime")) {
          if (ObjectUtil.notEmpty(v))
            v = DateUtil.parseDateTime(map.getString("MetaValue_" + key));
          else {
            v = null;
          }
        }
        if ((mc.getControlType().equals("RichText")) || (mc.getControlType().equals("TextArea"))) {
          v = StringUtil.htmlDecode(v.toString());
        }
        q.add(new Object[] { v });
      } else {
        q.add(new Object[] { v });
      }
    }
    tran.add(q);
  }

  public static void saveExtendData(Transaction tran, Mapx<String, Object> map, String PKValue, long modelID) {
    MetaModel mm = MetaModel.load(modelID);
    if (mm == null) {
      return;
    }
    saveExtendData(tran, map, PKValue, mm);
  }

  public static void saveExtendData(Transaction tran, Mapx<String, Object> map, String PKValue, String modelCode) {
    MetaModel mm = MetaModel.load(modelCode);
    if (mm == null) {
      return;
    }
    saveExtendData(tran, map, PKValue, mm);
  }

  public static boolean saveExtendData(Mapx<String, Object> map, String PKValue, long modelID) {
    Transaction tran = new Transaction();
    saveExtendData(tran, map, PKValue, modelID);
    return tran.commit();
  }

  public static boolean saveExtendData(Mapx<String, Object> map, String PKValue, String modelCode) {
    Transaction tran = new Transaction();
    saveExtendData(tran, map, PKValue, modelCode);
    return tran.commit();
  }

  public static String getDisplayValue(ZDMetaColumn mc) {
    return null;
  }

  public static String getControlHTML(ZDMetaColumn mc) {
    StringBuffer sb = new StringBuffer();
    IMetadataColumnControlType controlType = MetadataColumnControlTypeServise.getColumnControlTypoe(mc.getControlType());
    sb.append(controlType.getHtml(mc, null));
    return sb.toString();
  }

  public static String getControlHTML(ZDMetaColumn mc, String value) {
    StringBuffer sb = new StringBuffer();
    IMetadataColumnControlType controlType = MetadataColumnControlTypeServise.getColumnControlTypoe(mc.getControlType());
    sb.append(controlType.getHtml(mc, value));
    return sb.toString();
  }

  public static String getClass(ZDMetaColumn mc)
  {
    String styleText = "";
    if (StringUtil.isNotEmpty(mc.getStyleClass())) {
      styleText = styleText + " class=\"" + mc.getStyleClass() + "\"";
    }
    if (StringUtil.isNotEmpty(mc.getStyleText()))
      styleText = styleText + " style=\"" + mc.getStyleText() + "\"";
    else {
      styleText = styleText + " style=\"width:180px\"";
    }
    return styleText;
  }

  public static String getValue(ZDMetaColumn mc)
  {
    String value = null;
    String code = "MetaValue_" + mc.getCode();
    if (Current.getRequest() != null) {
      String value1 = Current.getRequest().getString(code);
      String value2 = Current.getResponse().getString(code);
      if (value2 != null) {
        value = value2;
      }
      if (value == null) {
        value = value1;
      }
    }
    if (value == null) {
      value = mc.getDefaultValue() == null ? "" : mc.getDefaultValue();
    }
    return value;
  }

  public static String getVerifys(ZDMetaColumn mc)
  {
    ArrayList verifyList = new ArrayList();
    if (YesOrNo.isYes(mc.getMandatoryFlag())) {
      verifyList.add("NotNull");
    }
    if (mc.getDataType().equals("Double")) {
      verifyList.add("Number");
    }
    if (mc.getDataType().equals("Long")) {
      verifyList.add("Int");
    }
    if (StringUtil.isNotEmpty(mc.getVerifyRule())) {
      for (String vr : StringUtil.splitEx(mc.getVerifyRule(), "&&")) {
        if (!verifyList.contains(vr)) {
          verifyList.add(vr);
        }
      }
    }
    String verify = "";
    if (verifyList.size() > 0) {
      verify = verify + " verify=\"" + StringUtil.join(verifyList, "&&") + "\"";
    }
    return verify;
  }
}
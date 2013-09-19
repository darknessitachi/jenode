package com.zving.platform.meta.bl;

import com.zving.framework.User;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.Q;
import com.zving.framework.data.Transaction;
import com.zving.framework.i18n.Lang;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.utility.Errorx;
import com.zving.framework.utility.NumberUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.meta.MetadataService;
import com.zving.platform.pub.NoUtil;
import com.zving.platform.pub.OrderUtil;
import com.zving.schema.ZDMetaColumn;
import com.zving.schema.ZDMetaColumnGroup;
import java.util.Date;

public class MetaModelColumnBL
{
  public static boolean isColumnGroupCodeExists(String code, String id)
  {
    Q qb = new Q("select count(1) from ZDMetaColumnGroup where Code=?", new Object[] { code });
    if (ObjectUtil.notEmpty(id)) {
      qb.append(" and ID<>?", new Object[] { id });
    }
    return qb.executeInt() > 0;
  }

  public static boolean isColumnCodeExists(String code, long id, long modelID) {
    Q qb = new Q("select count(1) from ZDMetaColumn where Code=? and ModelID=?", new Object[] { code, Long.valueOf(modelID) });
    if (ObjectUtil.notEmpty(Long.valueOf(id))) {
      qb.append(" and ID<>?", new Object[] { Long.valueOf(id) });
    }
    return qb.executeInt() > 0;
  }

  public static void saveColumnGroup(Mapx<String, Object> params, Transaction trans) {
    String id = params.getString("ID");
    if ((ObjectUtil.empty(id)) || (!NumberUtil.isLong(id))) {
      ZDMetaColumnGroup mcg = new ZDMetaColumnGroup();
      mcg.setID(NoUtil.getMaxID("MetaColumnGroupID"));
      mcg.setValue(params);
      mcg.setAddTime(new Date());
      mcg.setAddUser(User.getUserName());
      trans.insert(mcg);
    } else {
      ZDMetaColumnGroup mcg = new ZDMetaColumnGroup();
      mcg.setID(id);
      mcg.fill();
      mcg.setValue(params);
      mcg.setModifyTime(new Date());
      mcg.setModifyUser(User.getUserName());
      trans.update(mcg);
    }
  }

  public static DAOSet<ZDMetaColumnGroup> deleteColumnGroup(String ids, Transaction trans) {
    ids = StringUtil.replaceEx(ids, ",", "','");
    DAOSet set = new ZDMetaColumnGroup().query(new Q("where ID in ('" + ids + "')", new Object[0]));
    trans.deleteAndBackup(set);
    return set;
  }

  public static void saveColumn(Mapx<String, Object> params, Transaction trans) {
    String id = params.getString("ID");
    if (ObjectUtil.notEmpty(id)) {
      ZDMetaColumn mcg = new ZDMetaColumn();
      mcg.setID(id);
      mcg.fill();
      String dataType = mcg.getDataType();
      String targetField = mcg.getTargetField();
      mcg.setValue(params);
      mcg.setModifyTime(new Date());
      mcg.setModifyUser(User.getUserName());
      if (!dataType.equals(mcg.getDataType())) {
        try {
          String oldTargetField = targetField;
          targetField = MetadataService.arrangeTargetField(params.getLong("ModelID"), targetField, mcg.getDataType());

          if ("Y".equals(params.getString("copy"))) {
            trans.add(new Q("update ZDMetaValue set " + targetField + "=" + oldTargetField + " where ModelID=?", new Object[] { 
              Long.valueOf(params.getLong("ModelID")) }));
          }
          trans.add(new Q("update ZDMetaValue set " + oldTargetField + "=null where ModelID=?", new Object[] { 
            Long.valueOf(params
            .getLong("ModelID")) }));
        } catch (Exception e) {
          Errorx.addError(Lang.get("Platform.NoMoreColumnCanBeUse"));
          return;
        }
      }
      mcg.setTargetField(targetField);
      trans.update(mcg);
    } else {
      ZDMetaColumn mcg = new ZDMetaColumn();
      mcg.setID(NoUtil.getMaxID("MetaColumnID"));
      mcg.setValue(params);
      mcg.setOrderFlag(OrderUtil.getDefaultOrder());
      mcg.setAddTime(new Date());
      mcg.setAddUser(User.getUserName());
      String targetField = null;
      try {
        targetField = MetadataService.arrangeTargetField(params.getLong("ModelID"), null, mcg.getDataType());
      } catch (Exception e) {
        Errorx.addError(Lang.get("Platform.NoMoreColumnCanBeUse"));
        return;
      }
      mcg.setTargetField(targetField);
      trans.insert(mcg);
    }
  }

  public static DAOSet<ZDMetaColumn> deleteColumn(String ids, Transaction trans) {
    ids = StringUtil.replaceEx(ids, ",", "','");
    DAOSet set = new ZDMetaColumn().query(new Q("where ID in ('" + ids + "')", new Object[0]));
    trans.deleteAndBackup(set);
    return set;
  }

  public static long sortColumn(int delta, long id, Transaction trans) {
    ZDMetaColumn mcg = new ZDMetaColumn();
    mcg.setID(id);
    mcg.fill();

    int total = new Q("select count(1) from ZDMetaColumn where ModelID=?", new Object[] { Long.valueOf(mcg.getModelID()) }).executeInt();
    if ((mcg.getOrderFlag() == 0L) && (delta < 0)) {
      return 0L;
    }
    if ((mcg.getOrderFlag() == total - 1) && (delta > 0)) {
      return 0L;
    }

    Q qb = new Q("update ZDMetaColumn set OrderFlag=? where ModelID=? and OrderFlag=?", new Object[] { Long.valueOf(mcg.getOrderFlag()), 
      Long.valueOf(mcg.getModelID()), Long.valueOf(mcg.getOrderFlag() + delta) });
    trans.add(qb);
    mcg.setOrderFlag(mcg.getOrderFlag() + delta);
    trans.update(mcg);
    return mcg.getID();
  }
}
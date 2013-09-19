package com.zving.platform.meta.ui;

import com.zving.framework.Current;
import com.zving.framework.ResponseData;
import com.zving.framework.UIFacade;
import com.zving.framework.User;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.cache.CacheManager;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.data.Transaction;
import com.zving.framework.i18n.Lang;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.meta.MetaModel;
import com.zving.platform.meta.MetadataColumnControlTypeServise;
import com.zving.platform.meta.MetadataService;
import com.zving.platform.pub.NoUtil;
import com.zving.platform.pub.OrderUtil;
import com.zving.platform.pub.PlatformCache;
import com.zving.platform.pub.PlatformUtil;
import com.zving.schema.ZDMetaColumn;
import java.util.Date;

@Alias("MetaColumn")
public class MetaColumnUI extends UIFacade
{
  @Priv
  public void bindGrid(DataGridAction dga)
  {
    DataTable dt = null;
    if (StringUtil.isNotEmpty($V("ID"))) {
      dt = PlatformCache.getMetaModel(Long.parseLong($V("ID"))).getColumns().toDataTable();
      dt.sort("OrderFlag", "ASC");
      dt.decodeColumn("MandatoryFlag", PlatformUtil.getCodeMap("YesOrNo"));
      dga.bindData(dt);
    }
  }

  @Priv
  public void init() {
    String id = $V("ID");
    if (ObjectUtil.notEmpty(id)) {
      ZDMetaColumn mcg = new ZDMetaColumn();
      mcg.setID(id);
      mcg.fill();
      this.Response.putAll(mcg.toMapx());
      if (StringUtil.isNotNull(mcg.getListOptions()))
        this.Response.put("ListOptions", mcg.getListOptions().replaceAll("\n", "<br>"));
    }
    else {
      $S("MandatoryFlag", "N");
      DataTable groupsdt = getGroups();
      if ((groupsdt != null) && (groupsdt.getRowCount() > 0)) {
        $S("GroupID", groupsdt.get(0, "ID"));
      }
      $S("DataType", "ShortText");
      DataTable controldt = MetadataColumnControlTypeServise.getColumnControlTypeNameMap().toDataTable();
      if ((controldt != null) && (controldt.getRowCount() > 0))
        $S("ControlType", controldt.get(0, 0));
    }
  }

  @Priv("Platform.Metadata.Add||Platform.Metadata.SimilarCreate")
  public void save()
  {
    String id = $V("ID");
    String modelID = $V("ModelID");
    Transaction tran = new Transaction();
    if (ObjectUtil.notEmpty(id)) {
      DAOSet set = new ZDMetaColumn().query(new Q("where Code=? and ModelID=? and ID<>?", new Object[] { $V("Code"), 
        modelID, id }));
      if ((set != null) && (set.size() > 0)) {
        fail(Lang.get("Platform.CodeExists"));
        return;
      }
      ZDMetaColumn mcg = new ZDMetaColumn();
      mcg.setID(id);
      mcg.fill();
      String dataType = mcg.getDataType();
      String targetField = mcg.getTargetField();
      mcg.setValue(this.Request);
      mcg.setModifyTime(new Date());
      mcg.setModifyUser(User.getUserName());
      if (!dataType.equals(mcg.getDataType())) {
        try {
          String oldTargetField = targetField;
          targetField = MetadataService.arrangeTargetField(Long.parseLong(modelID), targetField, mcg.getDataType());

          if ("Y".equals($V("copy"))) {
            tran.add(new Q("update ZDMetaValue set " + targetField + "=" + oldTargetField + " where ModelID=?", new Object[] { 
              modelID }));
          }
          tran.add(new Q("update ZDMetaValue set " + oldTargetField + "=null where ModelID=?", new Object[] { modelID }));
        } catch (Exception e) {
          fail(Lang.get("Platform.NoMoreColumnCanBeUse"));
          this.Response.setStatus(2);
          return;
        }
      }
      mcg.setTargetField(targetField);
      tran.update(mcg);
    } else {
      DAOSet set = new ZDMetaColumn().query(new Q("where Code=? and ModelID=?", new Object[] { $V("Code"), modelID }));
      if ((set != null) && (set.size() > 0)) {
        fail(Lang.get("Platform.CodeExists"));
        this.Response.setStatus(2);
        return;
      }
      ZDMetaColumn mcg = new ZDMetaColumn();
      mcg.setID(NoUtil.getMaxID("MetaColumnID"));
      mcg.setValue(this.Request);
      mcg.setOrderFlag(new Q("select count(1) from ZDMetaColumn where ModelID=?", new Object[] { Long.valueOf(Long.parseLong($V("ModelID"))) })
        .executeLong());
      mcg.setAddTime(new Date());
      mcg.setAddUser(User.getUserName());
      String targetField = null;
      try {
        targetField = MetadataService.arrangeTargetField(Long.parseLong(modelID), null, mcg.getDataType());
      } catch (Exception e) {
        fail(Lang.get("Platform.NoMoreColumnCanBeUse"));
        this.Response.setStatus(2);
        return;
      }
      mcg.setTargetField(targetField);
      tran.insert(mcg);
    }
    if (tran.commit()) {
      success(Lang.get("Common.ExecuteSuccess"));
      CacheManager.remove("Platform", "MetaModel", Long.parseLong(modelID));
    } else {
      fail(Lang.get("Common.ExecuteFailed") + ":" + tran.getExceptionMessage());
    }
  }

  @Priv("Platform.Metadata.Delete")
  public void delete() {
    String ids = $V("IDs");
    ids = StringUtil.replaceEx(ids, ",", "','");
    DAOSet set = new ZDMetaColumn().query(new Q("where ID in ('" + ids + "')", new Object[0]));
    if (set.deleteAndBackup()) {
      CacheManager.remove("Platform", "MetaModel", ((ZDMetaColumn)set.get(0)).getModelID());
    }
    success(Lang.get("Common.DeleteSuccess"));
  }

  @Priv
  public DataTable getGroups() {
    String id = $V("ModelID");
    if (ObjectUtil.notEmpty(id)) {
      return new Q("select ID,Name from ZDMetaColumnGroup where ModelID=?", new Object[] { Long.valueOf(Long.parseLong(id)) }).fetch();
    }
    return null;
  }

  @Priv("Platform.Metadata.Add||Platform.Metadata.SimilarCreate")
  public void sortColumn() {
    long targetOrderFlag = Long.valueOf($V("TargetOrderFlag")).longValue();
    String id = $V("ID");
    if ((StringUtil.isNull(id)) || (targetOrderFlag <= 0L)) {
      fail(Lang.get("Common.ExecuteFailed"));
      return;
    }
    ZDMetaColumn schema = new ZDMetaColumn();
    schema.setID(id);
    if (!schema.fill()) {
      fail(Lang.get("Common.ExecuteFailed"));
      return;
    }
    OrderUtil.updateOrder(new ZDMetaColumn().table(), "OrderFlag", targetOrderFlag, 
      " and ModelID='" + schema.getModelID() + "'", schema, Current.getTransaction());

    if (Current.getTransaction().commit()) {
      CacheManager.remove("Platform", "MetaModel", schema.getModelID());
      success(Lang.get("Common.ExecuteSuccess"));
    } else {
      fail(Lang.get("Common.ExecuteFailed"));
    }
  }
}
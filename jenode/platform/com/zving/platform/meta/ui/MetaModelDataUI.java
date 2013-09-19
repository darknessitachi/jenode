package com.zving.platform.meta.ui;

import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.UIFacade;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.data.Transaction;
import com.zving.framework.i18n.Lang;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.ui.tag.ListAction;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.meta.MetaModel;
import com.zving.platform.meta.MetaUtil;
import com.zving.platform.meta.MetadataBL;
import com.zving.platform.pub.PlatformCache;
import com.zving.schema.ZDMetaColumn;
import com.zving.schema.ZDMetaModel;
import com.zving.schema.ZDMetaValue;
import java.util.ArrayList;

@Alias("MetaModelData")
public class MetaModelDataUI extends UIFacade
{
  @Priv
  public void bindBackendColumn(ListAction la)
  {
    long id = this.Request.getLong("ID");
    if (id == 0L) {
      return;
    }
    DataTable dt = new DataTable();
    dt.insertColumn("Name");
    dt.insertColumn("PlaceHolder");
    dt.insertRow(new Object[] { "PKValue", "${PKValue}" });

    DAOSet set = PlatformCache.getMetaModel(id).getColumns();
    for (ZDMetaColumn column : set) {
      dt.insertRow(new Object[] { column.getName(), "${" + column.getCode() + "}" });
    }
    la.bindData(dt);
  }

  @Priv
  public void bindGrid(DataGridAction dga) {
    long id = this.Request.getLong("ID");
    if (id == 0L) {
      return;
    }
    dga.bindData(getSelectQ(id));
  }

  @Priv
  public void init() {
    long id = this.Request.getLong("ID");
    if (id == 0L) {
      return;
    }
    String pk = $V("PKValue");
    if (ObjectUtil.empty(pk)) {
      return;
    }
    Q qb = getSelectQ(id);
    qb.append(" and PKValue=?", new Object[] { pk });
    DataTable dt = qb.fetch();
    if (dt.getRowCount() > 0) {
      Mapx map = dt.getDataRow(0).toMapx();
      for (String k : map.keyArray())
        $S("MetaValue_" + k, map.get(k));
    }
  }

  @Priv
  public void bindGroupList(ListAction la)
  {
    long id = this.Request.getLong("ID");
    if (id == 0L) {
      return;
    }
    DataTable dt = PlatformCache.getMetaModel(id).getGroups().toDataTable();
    la.bindData(dt);
  }

  @Priv
  public void bindFieldList(ListAction la) {
    long id = this.Request.getLong("ID");
    if (id == 0L) {
      return;
    }
    long groupID = la.getParentCurrentDataRow().getLong("ID");
    DataTable dt = MetaUtil.getControlHTML(id, groupID);
    la.bindData(dt);
  }

  @Priv("Platform.Metadata.AddData||Platform.Metadata.EditData")
  public void save() {
    long id = this.Request.getLong("ModelID");
    String pk = $V("PKValue");
    String oldPk = $V("OldPKValue");
    if ((id == 0L) || (ObjectUtil.empty(pk))) {
      fail(Lang.get("Platform.MetaModel.PKValueAndModelIDCanNotBeNull"));
      return;
    }
    ZDMetaValue mv = new ZDMetaValue();
    mv.setPKValue(pk);
    mv.setModelID(id);
    if ((!pk.equals(oldPk)) && (mv.fill())) {
      fail(Lang.get("Platform.MetaModel.DuplicatePKValue"));
      return;
    }
    MetadataBL.addMetadata(this.Request, Current.getTransaction());
    if (Current.getTransaction().commit())
      success(Lang.get("Common.SaveSuccess"));
    else
      success(Lang.get("Common.SaveFailed") + ":" + Current.getTransaction().getExceptionMessage());
  }

  @Priv("Platform.Metadata.DeleteData")
  public void delete()
  {
    long id = this.Request.getLong("ID");
    if (id == 0L) {
      return;
    }
    String pks = $V("PKValues");
    if (ObjectUtil.empty(pks)) {
      return;
    }
    pks = StringUtil.replaceEx(pks, ",", "','");
    Q qb = new Q("where ModelID=? and PKValue in ('" + pks + "')", new Object[] { Long.valueOf(id) });
    DAOSet set = new ZDMetaValue().query(qb);
    MetadataBL.deleteMetadata(set, Current.getTransaction());
    if (Current.getTransaction().commit())
      success(Lang.get("Common.DeleteSuccess"));
    else
      success(Lang.get("Common.DeleteFailed"));
  }

  public static Q getSelectQ(long id)
  {
    DAOSet set = PlatformCache.getMetaModel(id).getColumns();
    if (set.size() == 0) {
      return new Q("select * from ZDMetaValue where 1=2", new Object[0]);
    }
    ArrayList list = new ArrayList();
    for (ZDMetaColumn column : set) {
      list.add(column.getTargetField() + " as \"" + column.getCode() + "\"");
    }
    String table = PlatformCache.getMetaModel(id).getDAO().getTargetTable();
    Q qb = new Q("select ModelID,PKValue," + StringUtil.join(list) + " from " + table + " where ModelID=?", new Object[] { Long.valueOf(id) });
    return qb;
  }
}
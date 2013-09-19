package com.zving.platform.meta.ui;

import com.zving.framework.Current;
import com.zving.framework.ResponseData;
import com.zving.framework.UIFacade;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.cache.CacheManager;
import com.zving.framework.data.Q;
import com.zving.framework.data.Transaction;
import com.zving.framework.i18n.Lang;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.utility.ObjectUtil;
import com.zving.platform.meta.bl.MetaModelColumnBL;
import com.zving.schema.ZDMetaColumnGroup;

@Alias("MetaColumnGroup")
public class MetaColumnGroupUI extends UIFacade
{
  @Priv
  public void bindGrid(DataGridAction dga)
  {
    Q qb = new Q("select * from ZDMetaColumnGroup where ModelID=?", new Object[] { $V("ID") });
    dga.bindData(qb);
  }

  @Priv
  public void init() {
    String id = $V("ID");
    if (ObjectUtil.notEmpty(id)) {
      ZDMetaColumnGroup mcg = new ZDMetaColumnGroup();
      mcg.setID(id);
      mcg.fill();
      this.Response.putAll(mcg.toMapx());
    }
  }

  @Priv("Platform.Metadata.Add||Platform.Metadata.SimilarCreate")
  public void save() {
    String modelID = $V("ModelID");
    String groupID = $V("ID");
    if (MetaModelColumnBL.isColumnGroupCodeExists($V("Code"), groupID)) {
      fail(Lang.get("Platform.CodeExists"));
      return;
    }
    MetaModelColumnBL.saveColumnGroup(this.Request, Current.getTransaction());
    if (Current.getTransaction().commit()) {
      success(Lang.get("Common.ExecuteSuccess"));
      CacheManager.remove("Platform", "MetaModel", Long.parseLong(modelID));
    } else {
      fail(Lang.get("Common.ExecuteFailed") + ":" + Current.getTransaction().getExceptionMessage());
    }
  }

  @Priv("Platform.Metadata.Delete")
  public void delete()
  {
    String ids = $V("IDs");
    if (ObjectUtil.empty(ids)) {
      fail(Lang.get("Common.InvalidID"));
      return;
    }
    DAOSet set = MetaModelColumnBL.deleteColumnGroup(ids, Current.getTransaction());
    if (Current.getTransaction().commit()) {
      CacheManager.remove("Platform", "MetaModel", ((ZDMetaColumnGroup)set.get(0)).getModelID());
      success(Lang.get("Common.DeleteSuccess"));
    } else {
      fail(Lang.get("Common.DeleteFailed"));
    }
  }
}
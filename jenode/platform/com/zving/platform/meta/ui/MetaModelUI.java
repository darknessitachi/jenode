package com.zving.platform.meta.ui;

import com.zving.framework.Current;
import com.zving.framework.ResponseData;
import com.zving.framework.UIFacade;
import com.zving.framework.User;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.cache.CacheManager;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.data.Transaction;
import com.zving.framework.extend.ExtendManager;
import com.zving.framework.i18n.Lang;
import com.zving.framework.i18n.LangUtil;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.meta.IMetaModelType;
import com.zving.platform.meta.MetaModel;
import com.zving.platform.meta.MetadataService;
import com.zving.platform.pub.NoUtil;
import com.zving.platform.pub.PlatformCache;
import com.zving.schema.ZDMetaColumn;
import com.zving.schema.ZDMetaColumnGroup;
import com.zving.schema.ZDMetaModel;
import com.zving.schema.ZDMetaValue;
import com.zving.schema.ZDModelTemplate;
import java.util.Date;

@Alias("MetaModel")
public class MetaModelUI extends UIFacade
{
  @Priv
  public void init()
  {
    String id = $V("ID");
    if (ObjectUtil.notEmpty($V("CopyID"))) {
      id = $V("CopyID");
    }
    if (StringUtil.isNotEmpty(id)) {
      MetaModel mm = PlatformCache.getMetaModel(Long.parseLong(id));
      this.Response.putAll(mm.getDAO().toMapx());
      this.Response.remove("ID");
    } else {
      $S("OwnerID", "0");
      $S("TargetTable", "ZDMetaValue");
    }
  }

  @Priv
  public DataTable getTypes() {
    DataTable dt = new DataTable();
    dt.insertColumn("ID");
    dt.insertColumn("Name");
    for (IMetaModelType type : MetadataService.getInstance().getAll())
      if ((!"Y".equals($V("extend"))) || (!type.isSystemModel()))
      {
        dt.insertRow(new Object[] { type.getID(), type.getName() });
      }
    return dt;
  }

  @Priv
  public void bindGrid(DataGridAction dga) {
    Q qb = new Q("select * from ZDMetaModel where 1=1", new Object[0]);
    if (StringUtil.isNotEmpty(dga.getParam("SearchName"))) {
      qb.append(" and Name like ?", new Object[] { "%" + dga.getParam("SearchName") + "%" });
    }
    if (StringUtil.isNotEmpty(dga.getParam("Type"))) {
      qb.append(" and OwnerType=?", new Object[] { dga.getParam("Type") });
    }
    qb.append(" order by AddTime desc", new Object[0]);
    dga.bindData(qb);
  }

  @Priv("Platform.Metadata.Add||Platform.Metadata.SimilarCreate")
  public void save() {
    Transaction tran = new Transaction();
    String strID = $V("ID");
    if (ObjectUtil.empty(strID)) {
      strID = "0";
    }
    String name = $V("Name");
    long id = Long.parseLong(strID);
    Object obj;
    if (id != 0L) {
      if (isNameExists(name, strID)) {
        fail(Lang.get("Common.Name") + Lang.get("Common.Exists"));
        return;
      }
      DAOSet set = new ZDMetaModel().query(new Q("where Code=? and ID<>?", new Object[] { $V("Code"), Long.valueOf(id) }));
      if ((set != null) && (set.size() > 0)) {
        fail(Lang.get("Platform.CodeExists"));
        return;
      }
      ZDMetaModel mm = new ZDMetaModel();
      mm.setID(id);
      mm.fill();
      String oldModeCode = mm.getCode();
      tran.add(mm.clone(), 4);
      mm.setValue(this.Request);
      tran.add(mm, 2);
      mm.setModifyTime(new Date());
      mm.setModifyUser(User.getUserName());

      Object[] objs = ExtendManager.invoke("com.zving.platform.BeforeMetaModelSave", new Object[] { mm, oldModeCode });
      if (ObjectUtil.notEmpty(objs)) {
        StringBuilder sb = new StringBuilder();
        for (obj : objs) {
          sb.append(ObjectUtil.empty(obj) ? "" : Lang.get(obj.toString()));
        }
        if (StringUtil.isNotNull(sb.toString().trim()))
          fail(sb.toString());
      }
    }
    else
    {
      if (isNameExists(name, null)) {
        fail(Lang.get("Common.Name") + Lang.get("Common.Exists"));
        return;
      }
      DAOSet set = new ZDMetaModel().query(new Q("where Code=?", new Object[] { $V("Code") }));
      if ((set != null) && (set.size() > 0)) {
        fail(Lang.get("Platform.CodeExists"));
        return;
      }
      ZDMetaModel mm = new ZDMetaModel();
      id = NoUtil.getMaxID("MetaModelID");
      mm.setValue(this.Request);
      mm.setID(id);
      tran.add(mm, 1);
      mm.setAddTime(new Date());
      mm.setAddUser(User.getUserName());

      if (ObjectUtil.notEmpty($V("CopyID"))) {
        Q qb = new Q("where ModelID=?", new Object[] { Long.valueOf(Long.parseLong($V("CopyID"))) });
        DAOSet columnSet = new ZDMetaColumn().query(qb);
        for (ZDMetaColumn c : columnSet) {
          c.setID(NoUtil.getMaxID("MetaColumnID"));
          c.setModelID(id);
          c.setAddTime(new Date());
          c.setAddUser(User.getUserName());
          c.setModifyTime(c.getAddTime());
          c.setModifyUser(c.getAddUser());
        }
        DAOSet groupSet = new ZDMetaColumnGroup().query(qb);
        for (ZDMetaColumnGroup g : groupSet) {
          g.setID(NoUtil.getMaxID("MetaColumnGroupID"));
          g.setModelID(id);
          g.setAddTime(new Date());
          g.setAddUser(User.getUserName());
          g.setModifyTime(g.getAddTime());
          g.setModifyUser(g.getAddUser());
        }
        tran.insert(columnSet);
        tran.insert(groupSet);
      }
    }
    if (tran.commit()) {
      success(Lang.get("Common.ExecuteSuccess"));
      CacheManager.remove("Platform", "MetaModel", id);
    } else {
      fail(Lang.get("Common.ExecuteFailed") + ":" + tran.getExceptionMessage());
    }
  }

  @Priv("Platform.Metadata.Delete")
  public void del() {
    String ids = $V("IDs");
    Transaction trans = Current.getTransaction();
    ids = StringUtil.replaceEx(ids, ",", "','");
    DAOSet set = new ZDMetaModel().query(new Q("where ID in ('" + ids + "')", new Object[0]));
    trans.deleteAndBackup(set);

    DAOSet values = new ZDMetaValue().query(new Q("where ModelID in ('" + ids + "')", new Object[0]));
    trans.deleteAndBackup(values);

    DAOSet columns = new ZDMetaColumn().query(new Q("where ModelID in ('" + ids + "')", new Object[0]));
    trans.deleteAndBackup(columns);

    DAOSet columnGroups = new ZDMetaColumnGroup().query(new Q("where ModelID in ('" + ids + "')", new Object[0]));
    trans.deleteAndBackup(columnGroups);

    DAOSet templates = new ZDModelTemplate().query(new Q("where ModelID in ('" + ids + "')", new Object[0]));
    trans.deleteAndBackup(templates);

    Object[] objs = ExtendManager.invoke("com.zving.platform.BeforeMetaModelDelete", new Object[] { set });
    if (ObjectUtil.notEmpty(objs)) {
      StringBuilder sb = new StringBuilder();
      for (Object obj : objs) {
        sb.append(ObjectUtil.empty(obj) ? "" : obj.toString());
      }
      if (sb.length() > 0) {
        fail(sb.toString());
        return;
      }
    }
    if (trans.commit()) {
      for (int i = 0; i < set.size(); i++) {
        CacheManager.remove("Platform", "MetaModel", ((ZDMetaModel)set.get(i)).getID());
      }
      success(Lang.get("Common.DeleteSuccess"));
    } else {
      success(Lang.get("Common.DeleteSuccess"));
    }
  }

  @Priv
  public DataTable getCodeTypes() {
    Q qb = new Q("select CodeType,CodeName from ZDCode where ParentCode='System'", new Object[0]);
    DataTable dt = qb.fetch();
    LangUtil.decode(dt, "CodeName");
    return dt;
  }

  private boolean isNameExists(String name, String id) {
    Q qb = new Q("select count(1) from ZDMetaModel where Name=?", new Object[] { name });
    if (StringUtil.isNotEmpty(id)) {
      qb.append(" and ID<>?", new Object[] { Long.valueOf(Long.parseLong(id)) });
    }
    int count = qb.executeInt();
    if (count > 0) {
      return true;
    }
    return false;
  }
}
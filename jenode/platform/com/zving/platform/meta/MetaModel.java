package com.zving.platform.meta;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.Q;
import com.zving.framework.orm.DAOSet;
import com.zving.schema.ZDMetaColumn;
import com.zving.schema.ZDMetaColumnGroup;
import com.zving.schema.ZDMetaModel;
import com.zving.schema.ZDModelTemplate;

public class MetaModel
{
  private Mapx<String, ZDMetaColumn> mapping;
  private Mapx<String, String> converseMapping;
  private DAOSet<ZDMetaColumn> columns;
  private DAOSet<ZDMetaColumnGroup> groups;
  private ZDMetaModel schema;
  private DAOSet<ZDModelTemplate> templates;

  public static MetaModel load(long id)
  {
    ZDMetaModel mm = new ZDMetaModel();
    mm.setID(id);
    if (!mm.fill()) {
      return null;
    }
    return load(mm);
  }

  public static MetaModel load(String modelCode) {
    DAOSet set = new ZDMetaModel().query(new Q("where Code=?", new Object[] { modelCode }));
    if ((set == null) || (set.size() == 0)) {
      return null;
    }
    return load((ZDMetaModel)set.get(0));
  }

  private static MetaModel load(ZDMetaModel mm) {
    MetaModel model = new MetaModel();
    model.schema = mm;
    model.columns = new ZDMetaColumn().query(new Q("where ModelID=?", new Object[] { Long.valueOf(mm.getID()) }));
    model.groups = new ZDMetaColumnGroup().query(new Q("where ModelID=?", new Object[] { Long.valueOf(mm.getID()) }));
    model.templates = new ZDModelTemplate().query(new Q("where ModelID=?", new Object[] { Long.valueOf(mm.getID()) }));

    model.mapping = new Mapx();
    model.converseMapping = new Mapx();
    for (ZDMetaColumn mc : model.columns) {
      model.mapping.put(mc.getCode(), mc);
      model.converseMapping.put(mc.getTargetField(), mc.getCode());
    }
    return model;
  }

  public ZDModelTemplate getTemplateByType(String templateType) {
    for (ZDModelTemplate mt : this.templates) {
      if (mt.getTemplateType().equals(templateType)) {
        return mt;
      }
    }
    return null;
  }

  public Mapx<String, ZDMetaColumn> getMapping() {
    return this.mapping;
  }

  public Mapx<String, String> getConverseMapping() {
    return this.converseMapping;
  }

  public DAOSet<ZDMetaColumn> getColumns() {
    return this.columns;
  }

  public DAOSet<ZDMetaColumnGroup> getGroups() {
    return this.groups;
  }

  public ZDMetaModel getDAO() {
    return this.schema;
  }

  public void setTemplates(DAOSet<ZDModelTemplate> templates) {
    this.templates = templates;
  }

  public DAOSet<ZDModelTemplate> getTemplates() {
    return this.templates;
  }
}
package com.zving.framework.orm;

import com.zving.framework.annotation.dao.Column;
import com.zving.framework.annotation.dao.Indexes;
import com.zving.framework.annotation.dao.Table;
import com.zving.framework.collection.CaseIgnoreMapx;
import com.zving.framework.core.asm.objectweb.tree.ClassNode;
import com.zving.framework.core.asm.objectweb.tree.FieldNode;
import com.zving.framework.core.bean.BeanDescription;
import com.zving.framework.core.bean.BeanManager;
import com.zving.framework.core.bean.BeanProperty;
import com.zving.framework.core.scanner.AsmUtil;
import com.zving.framework.utility.ObjectUtil;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DAOMetadata
{
  private String table;
  private String indexes;
  private DAOColumn[] columns;
  private String insertSQL;
  private String primaryKeyConditions;
  private String deleteSQL;
  private String fillAllSQL;
  private CaseIgnoreMapx<String, BeanProperty> beanProperties;

  public DAOMetadata(Class<? extends DAO> clazz)
  {
    Table t = (Table)clazz.getAnnotation(Table.class);
    if (t == null) {
      throw new DAOException("DAO class " + clazz.getName() + " not annotated by @Table!");
    }
    BeanDescription bean = BeanManager.getBeanDescription(clazz);
    this.table = t.value();
    Indexes i = (Indexes)clazz.getAnnotation(Indexes.class);
    if (i != null) {
      this.indexes = i.value();
    }
    List list = new ArrayList();
    this.beanProperties = new CaseIgnoreMapx();
    for (Field f : clazz.getDeclaredFields())
      if (f.isAnnotationPresent(Column.class))
      {
        Column c = (Column)f.getAnnotation(Column.class);
        String name = c.name();
        if (ObjectUtil.empty(name)) {
          name = f.getName();
          name = name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        this.beanProperties.put(name, bean.getProperty(name));
        DAOColumn dc = new DAOColumn(name, c.type(), c.length(), c.precision(), c.mandatory(), c.pk());
        list.add(dc);
      }
    this.columns = new DAOColumn[list.size()];
    this.columns = ((DAOColumn[])list.toArray(this.columns));
    initSQL();
  }

  public DAOMetadata(ClassNode cn) {
    String TABLE = Table.class.getName().replace('.', '/');
    String INDEXINFO = Indexes.class.getName().replace('.', '/');
    String COLUMN = Column.class.getName().replace('.', '/');

    this.table = ((String)AsmUtil.getAnnotationValue(cn, TABLE, "value"));
    String index = (String)AsmUtil.getAnnotationValue(cn, INDEXINFO, "value");
    if (index != null) {
      this.indexes = index;
    }
    List list = new ArrayList();
    for (FieldNode mn : cn.fields)
      if ((mn.name != null) && (!mn.name.startsWith("<")))
      {
        boolean flag = AsmUtil.isAnnotationPresent(mn, COLUMN);
        if (flag) {
          String name = (String)AsmUtil.getAnnotationValue(mn, COLUMN, "name");
          if (ObjectUtil.empty(name)) {
            name = mn.name;
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
          }
          Integer type = (Integer)AsmUtil.getAnnotationValue(mn, COLUMN, "type");
          Integer length = (Integer)AsmUtil.getAnnotationValue(mn, COLUMN, "length");
          Integer precision = (Integer)AsmUtil.getAnnotationValue(mn, COLUMN, "precision");
          Boolean mandatory = (Boolean)AsmUtil.getAnnotationValue(mn, COLUMN, "mandatory");
          Boolean pk = (Boolean)AsmUtil.getAnnotationValue(mn, COLUMN, "pk");
          DAOColumn dc = new DAOColumn(name, type.intValue(), length == null ? 0 : length.intValue(), precision == null ? 0 : precision.intValue(), 
            mandatory == null ? false : mandatory.booleanValue(), pk == null ? false : pk.booleanValue());
          list.add(dc);
        }
      }
    this.columns = new DAOColumn[list.size()];
    this.columns = ((DAOColumn[])list.toArray(this.columns));
    initSQL();
  }

  protected DAOMetadata(String table, DAOColumn[] columns, String indexes, CaseIgnoreMapx<String, BeanProperty> properties) {
    this.table = table;
    this.columns = columns;
    this.indexes = indexes;
    this.beanProperties = properties;
    initSQL();
  }

  private void initSQL()
  {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    sb.append("insert into ");
    sb.append(this.table);
    sb.append(" (");
    for (DAOColumn c : this.columns) {
      if (!first)
        sb.append(",");
      else {
        first = false;
      }
      sb.append(c.getColumnName());
    }
    sb.append(") values (");
    for (int i = 0; i < this.columns.length; i++) {
      if (i != 0) {
        sb.append(",");
      }
      sb.append("?");
    }
    sb.append(")");
    this.insertSQL = sb.toString();

    sb = new StringBuilder();
    first = true;
    sb.append(" where ");
    first = true;
    for (DAOColumn c : this.columns)
      if (c.isPrimaryKey())
      {
        if (!first)
          sb.append(" and ");
        else {
          first = false;
        }
        sb.append(c.getColumnName());
        sb.append("=?");
      }
    this.primaryKeyConditions = sb.toString();

    sb = new StringBuilder();
    first = true;
    sb.append("select * from ");
    sb.append(this.table);
    sb.append(" where ");
    first = true;
    for (DAOColumn c : this.columns)
      if (c.isPrimaryKey())
      {
        if (!first)
          sb.append(" and ");
        else {
          first = false;
        }
        sb.append(c.getColumnName());
        sb.append("=?");
      }
    this.fillAllSQL = sb.toString();

    sb = new StringBuilder();
    first = true;
    sb.append("delete from ");
    sb.append(this.table);
    sb.append(" where ");
    first = true;
    for (DAOColumn c : this.columns)
      if (c.isPrimaryKey())
      {
        if (!first)
          sb.append(" and ");
        else {
          first = false;
        }
        sb.append(c.getColumnName());
        sb.append("=?");
      }
    this.deleteSQL = sb.toString();
  }

  protected Object getV(DAO<?> dao, String column) {
    BeanProperty p = (BeanProperty)this.beanProperties.get(column);
    if (p == null) {
      throw new DAOException("DAO class hasn't getter for " + column);
    }
    return p.read(dao);
  }

  protected void setV(DAO<?> dao, String column, Object value) {
    BeanProperty p = (BeanProperty)this.beanProperties.get(column);
    if (p == null) {
      throw new DAOException("DAO class hasn't setter for " + column);
    }
    p.write(dao, value);
  }

  protected int getColumnOrder(DAOColumn c) {
    for (int i = 0; i < this.columns.length; i++) {
      if (this.columns[i] == c) {
        return i;
      }
    }
    return -1;
  }

  public String getTable() {
    return this.table;
  }

  public String getIndexes() {
    return this.indexes;
  }

  public DAOColumn[] getColumns() {
    return this.columns;
  }

  public String getInsertSQL() {
    return this.insertSQL;
  }

  public String getDeleteSQL() {
    return this.deleteSQL;
  }

  public String getFillAllSQL() {
    return this.fillAllSQL;
  }

  public String getPrimaryKeyConditions() {
    return this.primaryKeyConditions;
  }

  protected CaseIgnoreMapx<String, BeanProperty> getBeanProperties() {
    return this.beanProperties;
  }
}
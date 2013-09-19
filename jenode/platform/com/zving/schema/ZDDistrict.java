package com.zving.schema;

import com.zving.framework.annotation.dao.Column;
import com.zving.framework.annotation.dao.Indexes;
import com.zving.framework.annotation.dao.Table;
import com.zving.framework.orm.DAO;

@Table("ZDDistrict")
@Indexes("")
public class ZDDistrict extends DAO<ZDDistrict>
{

  @Column(type=1, length=6, mandatory=true, pk=true)
  protected String Code;

  @Column(type=1, length=100)
  protected String Name;

  @Column(type=1, length=20)
  protected String CodeOrder;

  @Column(type=8)
  protected Integer TreeLevel;

  @Column(type=1, length=2)
  protected String Type;

  public String getCode()
  {
    return this.Code;
  }

  public String getCodeOrder()
  {
    return this.CodeOrder;
  }

  public String getName()
  {
    return this.Name;
  }

  public int getTreeLevel()
  {
    if (this.TreeLevel == null) {
      return 0;
    }
    return this.TreeLevel.intValue();
  }

  public String getType()
  {
    return this.Type;
  }

  public void setCode(String code)
  {
    this.Code = code;
  }

  public void setCodeOrder(String codeOrder)
  {
    this.CodeOrder = codeOrder;
  }

  public void setName(String name)
  {
    this.Name = name;
  }

  public void setTreeLevel(int treeLevel)
  {
    this.TreeLevel = new Integer(treeLevel);
  }

  public void setTreeLevel(String treeLevel)
  {
    if (treeLevel == null) {
      this.TreeLevel = null;
      return;
    }
    this.TreeLevel = new Integer(treeLevel);
  }

  public void setType(String type)
  {
    this.Type = type;
  }
}
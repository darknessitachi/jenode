package com.zving.schema;

import com.zving.framework.annotation.dao.Column;
import com.zving.framework.annotation.dao.Indexes;
import com.zving.framework.annotation.dao.Table;
import com.zving.framework.orm.DAO;
import com.zving.framework.utility.DateUtil;
import java.util.Date;

@Table("ZDBranch")
@Indexes("")
public class ZDBranch extends DAO<ZDBranch>
{

  @Column(type=1, length=100, mandatory=true, pk=true)
  protected String BranchInnerCode;

  @Column(type=1, length=100)
  protected String BranchCode;

  @Column(type=1, length=100, mandatory=true)
  protected String ParentInnerCode;

  @Column(type=1, length=1, mandatory=true)
  protected String Type;

  @Column(type=7, mandatory=true)
  protected Long OrderFlag;

  @Column(type=1, length=2000, mandatory=true)
  protected String Name;

  @Column(type=7, mandatory=true)
  protected Long TreeLevel;

  @Column(type=1, length=2)
  protected String IsLeaf;

  @Column(type=1, length=20)
  protected String Phone;

  @Column(type=1, length=20)
  protected String Fax;

  @Column(type=1, length=100)
  protected String Manager;

  @Column(type=1, length=100)
  protected String Leader1;

  @Column(type=1, length=100)
  protected String Leader2;

  @Column(type=1, length=50)
  protected String Prop1;

  @Column(type=1, length=50)
  protected String Prop2;

  @Column(type=1, length=50)
  protected String Prop3;

  @Column(type=1, length=50)
  protected String Prop4;

  @Column(type=1, length=100)
  protected String Memo;

  @Column(type=12, mandatory=true)
  protected Date AddTime;

  @Column(type=1, length=200, mandatory=true)
  protected String AddUser;

  @Column(type=12)
  protected Date ModifyTime;

  @Column(type=1, length=200)
  protected String ModifyUser;

  public Date getAddTime()
  {
    return this.AddTime;
  }

  public String getAddUser()
  {
    return this.AddUser;
  }

  public String getBranchCode()
  {
    return this.BranchCode;
  }

  public String getBranchInnerCode()
  {
    return this.BranchInnerCode;
  }

  public String getFax()
  {
    return this.Fax;
  }

  public String getIsLeaf()
  {
    return this.IsLeaf;
  }

  public String getLeader1()
  {
    return this.Leader1;
  }

  public String getLeader2()
  {
    return this.Leader2;
  }

  public String getManager()
  {
    return this.Manager;
  }

  public String getMemo()
  {
    return this.Memo;
  }

  public Date getModifyTime()
  {
    return this.ModifyTime;
  }

  public String getModifyUser()
  {
    return this.ModifyUser;
  }

  public String getName()
  {
    return this.Name;
  }

  public long getOrderFlag()
  {
    if (this.OrderFlag == null) {
      return 0L;
    }
    return this.OrderFlag.longValue();
  }

  public String getParentInnerCode()
  {
    return this.ParentInnerCode;
  }

  public String getPhone()
  {
    return this.Phone;
  }

  public String getProp1()
  {
    return this.Prop1;
  }

  public String getProp2()
  {
    return this.Prop2;
  }

  public String getProp3()
  {
    return this.Prop3;
  }

  public String getProp4()
  {
    return this.Prop4;
  }

  public long getTreeLevel()
  {
    if (this.TreeLevel == null) {
      return 0L;
    }
    return this.TreeLevel.longValue();
  }

  public String getType()
  {
    return this.Type;
  }

  public void setAddTime(Date addTime)
  {
    this.AddTime = addTime;
  }

  public void setAddTime(String addTime)
  {
    if (addTime == null) {
      this.AddTime = null;
      return;
    }
    this.AddTime = DateUtil.parseDateTime(addTime);
  }

  public void setAddUser(String addUser)
  {
    this.AddUser = addUser;
  }

  public void setBranchCode(String branchCode)
  {
    this.BranchCode = branchCode;
  }

  public void setBranchInnerCode(String branchInnerCode)
  {
    this.BranchInnerCode = branchInnerCode;
  }

  public void setFax(String fax)
  {
    this.Fax = fax;
  }

  public void setIsLeaf(String isLeaf)
  {
    this.IsLeaf = isLeaf;
  }

  public void setLeader1(String leader1)
  {
    this.Leader1 = leader1;
  }

  public void setLeader2(String leader2)
  {
    this.Leader2 = leader2;
  }

  public void setManager(String manager)
  {
    this.Manager = manager;
  }

  public void setMemo(String memo)
  {
    this.Memo = memo;
  }

  public void setModifyTime(Date modifyTime)
  {
    this.ModifyTime = modifyTime;
  }

  public void setModifyTime(String modifyTime)
  {
    if (modifyTime == null) {
      this.ModifyTime = null;
      return;
    }
    this.ModifyTime = DateUtil.parseDateTime(modifyTime);
  }

  public void setModifyUser(String modifyUser)
  {
    this.ModifyUser = modifyUser;
  }

  public void setName(String name)
  {
    this.Name = name;
  }

  public void setOrderFlag(long orderFlag)
  {
    this.OrderFlag = new Long(orderFlag);
  }

  public void setOrderFlag(String orderFlag)
  {
    if (orderFlag == null) {
      this.OrderFlag = null;
      return;
    }
    this.OrderFlag = new Long(orderFlag);
  }

  public void setParentInnerCode(String parentInnerCode)
  {
    this.ParentInnerCode = parentInnerCode;
  }

  public void setPhone(String phone)
  {
    this.Phone = phone;
  }

  public void setProp1(String prop1)
  {
    this.Prop1 = prop1;
  }

  public void setProp2(String prop2)
  {
    this.Prop2 = prop2;
  }

  public void setProp3(String prop3)
  {
    this.Prop3 = prop3;
  }

  public void setProp4(String prop4)
  {
    this.Prop4 = prop4;
  }

  public void setTreeLevel(long treeLevel)
  {
    this.TreeLevel = new Long(treeLevel);
  }

  public void setTreeLevel(String treeLevel)
  {
    if (treeLevel == null) {
      this.TreeLevel = null;
      return;
    }
    this.TreeLevel = new Long(treeLevel);
  }

  public void setType(String type)
  {
    this.Type = type;
  }
}
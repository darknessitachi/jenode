package com.zving.schema;

import com.zving.framework.annotation.dao.Column;
import com.zving.framework.annotation.dao.Indexes;
import com.zving.framework.annotation.dao.Table;
import com.zving.framework.orm.DAO;
import com.zving.framework.utility.DateUtil;
import java.util.Date;

@Table("ZDPrivilege")
@Indexes("")
public class ZDPrivilege extends DAO<ZDPrivilege>
{

  @Column(type=1, length=3, mandatory=true, pk=true)
  protected String OwnerType;

  @Column(type=1, length=100, mandatory=true, pk=true)
  protected String Owner;

  @Column(type=10)
  protected String Privs;

  @Column(type=12, mandatory=true)
  protected Date AddTime;

  @Column(type=1, length=50, mandatory=true)
  protected String AddUser;

  @Column(type=12)
  protected Date ModifyTime;

  @Column(type=1, length=50)
  protected String ModifyUser;

  public Date getAddTime()
  {
    return this.AddTime;
  }

  public String getAddUser()
  {
    return this.AddUser;
  }

  public Date getModifyTime()
  {
    return this.ModifyTime;
  }

  public String getModifyUser()
  {
    return this.ModifyUser;
  }

  public String getOwner()
  {
    return this.Owner;
  }

  public String getOwnerType()
  {
    return this.OwnerType;
  }

  public String getPrivs()
  {
    return this.Privs;
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

  public void setOwner(String owner)
  {
    this.Owner = owner;
  }

  public void setOwnerType(String ownerType)
  {
    this.OwnerType = ownerType;
  }

  public void setPrivs(String privs)
  {
    this.Privs = privs;
  }
}
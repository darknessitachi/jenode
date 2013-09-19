package com.zving.schema;

import com.zving.framework.annotation.dao.Column;
import com.zving.framework.annotation.dao.Indexes;
import com.zving.framework.annotation.dao.Table;
import com.zving.framework.orm.DAO;
import com.zving.framework.utility.DateUtil;
import java.util.Date;

@Table("ZDUserRole")
@Indexes("")
public class ZDUserRole extends DAO<ZDUserRole>
{

  @Column(type=1, length=50, mandatory=true, pk=true)
  protected String UserName;

  @Column(type=1, length=200, mandatory=true, pk=true)
  protected String RoleCode;

  @Column(type=1, length=50)
  protected String Prop1;

  @Column(type=1, length=50)
  protected String Prop2;

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

  public String getProp1()
  {
    return this.Prop1;
  }

  public String getProp2()
  {
    return this.Prop2;
  }

  public String getRoleCode()
  {
    return this.RoleCode;
  }

  public String getUserName()
  {
    return this.UserName;
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

  public void setProp1(String prop1)
  {
    this.Prop1 = prop1;
  }

  public void setProp2(String prop2)
  {
    this.Prop2 = prop2;
  }

  public void setRoleCode(String roleCode)
  {
    this.RoleCode = roleCode;
  }

  public void setUserName(String userName)
  {
    this.UserName = userName;
  }
}
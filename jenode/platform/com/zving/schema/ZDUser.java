package com.zving.schema;

import com.zving.framework.annotation.dao.Column;
import com.zving.framework.annotation.dao.Indexes;
import com.zving.framework.annotation.dao.Table;
import com.zving.framework.orm.DAO;
import com.zving.framework.utility.DateUtil;
import java.util.Date;

@Table("ZDUser")
@Indexes("")
public class ZDUser extends DAO<ZDUser>
{

  @Column(type=1, length=200, mandatory=true, pk=true)
  protected String UserName;

  @Column(type=1, length=1000)
  protected String RealName;

  @Column(type=1, length=100, mandatory=true)
  protected String Password;

  @Column(type=1, length=100, mandatory=true)
  protected String BranchInnerCode;

  @Column(type=1, length=2, mandatory=true)
  protected String IsBranchAdmin;

  @Column(type=1, length=50, mandatory=true)
  protected String Status;

  @Column(type=1, length=2)
  protected String Type;

  @Column(type=1, length=100)
  protected String Email;

  @Column(type=1, length=20)
  protected String Tel;

  @Column(type=1, length=50)
  protected String Mobile;

  @Column(type=12)
  protected Date LastLoginTime;

  @Column(type=1, length=50)
  protected String LastLoginIP;

  @Column(type=1, length=50)
  protected String Prop1;

  @Column(type=1, length=50)
  protected String Prop2;

  @Column(type=1, length=50)
  protected String Prop6;

  @Column(type=1, length=50)
  protected String Prop5;

  @Column(type=1, length=100)
  protected String Prop4;

  @Column(type=1, length=100)
  protected String Prop3;

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

  public String getBranchInnerCode()
  {
    return this.BranchInnerCode;
  }

  public String getEmail()
  {
    return this.Email;
  }

  public String getIsBranchAdmin()
  {
    return this.IsBranchAdmin;
  }

  public String getLastLoginIP()
  {
    return this.LastLoginIP;
  }

  public Date getLastLoginTime()
  {
    return this.LastLoginTime;
  }

  public String getMemo()
  {
    return this.Memo;
  }

  public String getMobile()
  {
    return this.Mobile;
  }

  public Date getModifyTime()
  {
    return this.ModifyTime;
  }

  public String getModifyUser()
  {
    return this.ModifyUser;
  }

  public String getPassword()
  {
    return this.Password;
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

  public String getProp5()
  {
    return this.Prop5;
  }

  public String getProp6()
  {
    return this.Prop6;
  }

  public String getRealName()
  {
    return this.RealName;
  }

  public String getStatus()
  {
    return this.Status;
  }

  public String getTel()
  {
    return this.Tel;
  }

  public String getType()
  {
    return this.Type;
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

  public void setBranchInnerCode(String branchInnerCode)
  {
    this.BranchInnerCode = branchInnerCode;
  }

  public void setEmail(String email)
  {
    this.Email = email;
  }

  public void setIsBranchAdmin(String isBranchAdmin)
  {
    this.IsBranchAdmin = isBranchAdmin;
  }

  public void setLastLoginIP(String lastLoginIP)
  {
    this.LastLoginIP = lastLoginIP;
  }

  public void setLastLoginTime(Date lastLoginTime)
  {
    this.LastLoginTime = lastLoginTime;
  }

  public void setLastLoginTime(String lastLoginTime)
  {
    if (lastLoginTime == null) {
      this.LastLoginTime = null;
      return;
    }
    this.LastLoginTime = DateUtil.parseDateTime(lastLoginTime);
  }

  public void setMemo(String memo)
  {
    this.Memo = memo;
  }

  public void setMobile(String mobile)
  {
    this.Mobile = mobile;
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

  public void setPassword(String password)
  {
    this.Password = password;
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

  public void setProp5(String prop5)
  {
    this.Prop5 = prop5;
  }

  public void setProp6(String prop6)
  {
    this.Prop6 = prop6;
  }

  public void setRealName(String realName)
  {
    this.RealName = realName;
  }

  public void setStatus(String status)
  {
    this.Status = status;
  }

  public void setTel(String tel)
  {
    this.Tel = tel;
  }

  public void setType(String type)
  {
    this.Type = type;
  }

  public void setUserName(String userName)
  {
    this.UserName = userName;
  }
}
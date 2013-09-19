package com.zving.schema;

import com.zving.framework.annotation.dao.Column;
import com.zving.framework.annotation.dao.Indexes;
import com.zving.framework.annotation.dao.Table;
import com.zving.framework.orm.DAO;
import com.zving.framework.utility.DateUtil;
import java.util.Date;

@Table("ZDMetaModel")
@Indexes("")
public class ZDMetaModel extends DAO<ZDMetaModel>
{

  @Column(type=7, mandatory=true, pk=true)
  protected Long ID;

  @Column(type=7, mandatory=true)
  protected Long OwnerID;

  @Column(type=1, length=40, mandatory=true)
  protected String OwnerType;

  @Column(type=1, length=40)
  protected String Code;

  @Column(type=1, length=200, mandatory=true)
  protected String Name;

  @Column(type=1, length=200, mandatory=true)
  protected String TargetTable;

  @Column(type=1, length=40)
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

  public String getCode()
  {
    return this.Code;
  }

  public long getID()
  {
    if (this.ID == null) {
      return 0L;
    }
    return this.ID.longValue();
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

  public long getOwnerID()
  {
    if (this.OwnerID == null) {
      return 0L;
    }
    return this.OwnerID.longValue();
  }

  public String getOwnerType()
  {
    return this.OwnerType;
  }

  public String getTargetTable()
  {
    return this.TargetTable;
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

  public void setCode(String code)
  {
    this.Code = code;
  }

  public void setID(long iD)
  {
    this.ID = new Long(iD);
  }

  public void setID(String iD)
  {
    if (iD == null) {
      this.ID = null;
      return;
    }
    this.ID = new Long(iD);
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

  public void setOwnerID(long ownerID)
  {
    this.OwnerID = new Long(ownerID);
  }

  public void setOwnerID(String ownerID)
  {
    if (ownerID == null) {
      this.OwnerID = null;
      return;
    }
    this.OwnerID = new Long(ownerID);
  }

  public void setOwnerType(String ownerType)
  {
    this.OwnerType = ownerType;
  }

  public void setTargetTable(String targetTable)
  {
    this.TargetTable = targetTable;
  }
}
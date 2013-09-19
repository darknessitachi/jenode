package com.zving.schema;

import com.zving.framework.annotation.dao.Column;
import com.zving.framework.annotation.dao.Indexes;
import com.zving.framework.annotation.dao.Table;
import com.zving.framework.orm.DAO;
import com.zving.framework.utility.DateUtil;
import java.util.Date;

@Table("ZDMetaColumnGroup")
@Indexes("")
public class ZDMetaColumnGroup extends DAO<ZDMetaColumnGroup>
{

  @Column(type=7, mandatory=true, pk=true)
  protected Long ID;

  @Column(type=7)
  protected Long ModelID;

  @Column(type=1, length=50, mandatory=true)
  protected String Code;

  @Column(type=1, length=2000, mandatory=true)
  protected String Name;

  @Column(type=1, length=400)
  protected String Memo;

  @Column(type=1, length=50, mandatory=true)
  protected String AddUser;

  @Column(type=12, mandatory=true)
  protected Date AddTime;

  @Column(type=1, length=50)
  protected String ModifyUser;

  @Column(type=12)
  protected Date ModifyTime;

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

  public long getModelID()
  {
    if (this.ModelID == null) {
      return 0L;
    }
    return this.ModelID.longValue();
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

  public void setModelID(long modelID)
  {
    this.ModelID = new Long(modelID);
  }

  public void setModelID(String modelID)
  {
    if (modelID == null) {
      this.ModelID = null;
      return;
    }
    this.ModelID = new Long(modelID);
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
}
package com.zving.schema;

import com.zving.framework.annotation.dao.Column;
import com.zving.framework.annotation.dao.Indexes;
import com.zving.framework.annotation.dao.Table;
import com.zving.framework.orm.DAO;
import com.zving.framework.utility.DateUtil;
import java.util.Date;

@Table("ZDSchedule")
@Indexes("")
public class ZDSchedule extends DAO<ZDSchedule>
{

  @Column(type=7, mandatory=true, pk=true)
  protected Long ID;

  @Column(type=7, mandatory=true)
  protected Long SourceID;

  @Column(type=1, length=30, mandatory=true)
  protected String TypeCode;

  @Column(type=1, length=100, mandatory=true)
  protected String CronExpression;

  @Column(type=1, length=10, mandatory=true)
  protected String PlanType;

  @Column(type=12)
  protected Date StartTime;

  @Column(type=1, length=255)
  protected String Description;

  @Column(type=1, length=2, mandatory=true)
  protected String IsUsing;

  @Column(type=1, length=50)
  protected String OrderFlag;

  @Column(type=1, length=50)
  protected String Prop1;

  @Column(type=1, length=50)
  protected String Prop2;

  @Column(type=1, length=50)
  protected String Prop3;

  @Column(type=1, length=50)
  protected String Prop4;

  @Column(type=1, length=200, mandatory=true)
  protected String AddUser;

  @Column(type=12, mandatory=true)
  protected Date AddTime;

  @Column(type=1, length=200)
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

  public String getCronExpression()
  {
    return this.CronExpression;
  }

  public String getDescription()
  {
    return this.Description;
  }

  public long getID()
  {
    if (this.ID == null) {
      return 0L;
    }
    return this.ID.longValue();
  }

  public String getIsUsing()
  {
    return this.IsUsing;
  }

  public Date getModifyTime()
  {
    return this.ModifyTime;
  }

  public String getModifyUser()
  {
    return this.ModifyUser;
  }

  public String getOrderFlag()
  {
    return this.OrderFlag;
  }

  public String getPlanType()
  {
    return this.PlanType;
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

  public long getSourceID()
  {
    if (this.SourceID == null) {
      return 0L;
    }
    return this.SourceID.longValue();
  }

  public Date getStartTime()
  {
    return this.StartTime;
  }

  public String getTypeCode()
  {
    return this.TypeCode;
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

  public void setCronExpression(String cronExpression)
  {
    this.CronExpression = cronExpression;
  }

  public void setDescription(String description)
  {
    this.Description = description;
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

  public void setIsUsing(String isUsing)
  {
    this.IsUsing = isUsing;
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

  public void setOrderFlag(String orderFlag)
  {
    this.OrderFlag = orderFlag;
  }

  public void setPlanType(String planType)
  {
    this.PlanType = planType;
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

  public void setSourceID(long sourceID)
  {
    this.SourceID = new Long(sourceID);
  }

  public void setSourceID(String sourceID)
  {
    if (sourceID == null) {
      this.SourceID = null;
      return;
    }
    this.SourceID = new Long(sourceID);
  }

  public void setStartTime(Date startTime)
  {
    this.StartTime = startTime;
  }

  public void setStartTime(String startTime)
  {
    if (startTime == null) {
      this.StartTime = null;
      return;
    }
    this.StartTime = DateUtil.parseDateTime(startTime);
  }

  public void setTypeCode(String typeCode)
  {
    this.TypeCode = typeCode;
  }
}
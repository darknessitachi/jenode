package com.zving.schema;

import com.zving.framework.annotation.dao.Column;
import com.zving.framework.annotation.dao.Indexes;
import com.zving.framework.annotation.dao.Table;
import com.zving.framework.orm.DAO;
import com.zving.framework.utility.DateUtil;
import java.util.Date;

@Table("ZDUserLog")
@Indexes("")
public class ZDUserLog extends DAO<ZDUserLog>
{

  @Column(type=1, length=200, mandatory=true, pk=true)
  protected String UserName;

  @Column(type=7, mandatory=true, pk=true)
  protected Long LogID;

  @Column(type=1, length=40)
  protected String IP;

  @Column(type=1, length=50, mandatory=true)
  protected String LogType;

  @Column(type=1, length=50)
  protected String SubType;

  @Column(type=1, length=3000)
  protected String LogMessage;

  @Column(type=1, length=40)
  protected String Memo;

  @Column(type=12, mandatory=true)
  protected Date AddTime;

  public Date getAddTime()
  {
    return this.AddTime;
  }

  public String getIP()
  {
    return this.IP;
  }

  public long getLogID()
  {
    if (this.LogID == null) {
      return 0L;
    }
    return this.LogID.longValue();
  }

  public String getLogMessage()
  {
    return this.LogMessage;
  }

  public String getLogType()
  {
    return this.LogType;
  }

  public String getMemo()
  {
    return this.Memo;
  }

  public String getSubType()
  {
    return this.SubType;
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

  public void setIP(String iP)
  {
    this.IP = iP;
  }

  public void setLogID(long logID)
  {
    this.LogID = new Long(logID);
  }

  public void setLogID(String logID)
  {
    if (logID == null) {
      this.LogID = null;
      return;
    }
    this.LogID = new Long(logID);
  }

  public void setLogMessage(String logMessage)
  {
    this.LogMessage = logMessage;
  }

  public void setLogType(String logType)
  {
    this.LogType = logType;
  }

  public void setMemo(String memo)
  {
    this.Memo = memo;
  }

  public void setSubType(String subType)
  {
    this.SubType = subType;
  }

  public void setUserName(String userName)
  {
    this.UserName = userName;
  }
}
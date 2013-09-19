package com.zving.schema;

import com.zving.framework.annotation.dao.Column;
import com.zving.framework.annotation.dao.Indexes;
import com.zving.framework.annotation.dao.Table;
import com.zving.framework.orm.DAO;
import com.zving.framework.utility.DateUtil;
import java.util.Date;

@Table("ZDMessage")
@Indexes("")
public class ZDMessage extends DAO<ZDMessage>
{

  @Column(type=7, mandatory=true, pk=true)
  protected Long ID;

  @Column(type=1, length=50)
  protected String FromUser;

  @Column(type=1, length=50)
  protected String ToUser;

  @Column(type=1, length=10)
  protected String Box;

  @Column(type=7)
  protected Long ReadFlag;

  @Column(type=7)
  protected Long PopFlag;

  @Column(type=7)
  protected Long DelByFromUser;

  @Column(type=7)
  protected Long DelByToUser;

  @Column(type=1, length=500)
  protected String Subject;

  @Column(type=10)
  protected String Content;

  @Column(type=12)
  protected Date AddTime;

  @Column(type=1, length=500)
  protected String RedirectURL;

  @Column(type=1, length=100)
  protected String Prop1;

  @Column(type=1, length=100)
  protected String Prop2;

  @Column(type=1, length=100)
  protected String Prop3;

  @Column(type=1, length=100)
  protected String Prop4;

  public Date getAddTime()
  {
    return this.AddTime;
  }

  public String getBox()
  {
    return this.Box;
  }

  public String getContent()
  {
    return this.Content;
  }

  public long getDelByFromUser()
  {
    if (this.DelByFromUser == null) {
      return 0L;
    }
    return this.DelByFromUser.longValue();
  }

  public long getDelByToUser()
  {
    if (this.DelByToUser == null) {
      return 0L;
    }
    return this.DelByToUser.longValue();
  }

  public String getFromUser()
  {
    return this.FromUser;
  }

  public long getID()
  {
    if (this.ID == null) {
      return 0L;
    }
    return this.ID.longValue();
  }

  public long getPopFlag()
  {
    if (this.PopFlag == null) {
      return 0L;
    }
    return this.PopFlag.longValue();
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

  public long getReadFlag()
  {
    if (this.ReadFlag == null) {
      return 0L;
    }
    return this.ReadFlag.longValue();
  }

  public String getRedirectURL()
  {
    return this.RedirectURL;
  }

  public String getSubject()
  {
    return this.Subject;
  }

  public String getToUser()
  {
    return this.ToUser;
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

  public void setBox(String box)
  {
    this.Box = box;
  }

  public void setContent(String content)
  {
    this.Content = content;
  }

  public void setDelByFromUser(long delByFromUser)
  {
    this.DelByFromUser = new Long(delByFromUser);
  }

  public void setDelByFromUser(String delByFromUser)
  {
    if (delByFromUser == null) {
      this.DelByFromUser = null;
      return;
    }
    this.DelByFromUser = new Long(delByFromUser);
  }

  public void setDelByToUser(long delByToUser)
  {
    this.DelByToUser = new Long(delByToUser);
  }

  public void setDelByToUser(String delByToUser)
  {
    if (delByToUser == null) {
      this.DelByToUser = null;
      return;
    }
    this.DelByToUser = new Long(delByToUser);
  }

  public void setFromUser(String fromUser)
  {
    this.FromUser = fromUser;
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

  public void setPopFlag(long popFlag)
  {
    this.PopFlag = new Long(popFlag);
  }

  public void setPopFlag(String popFlag)
  {
    if (popFlag == null) {
      this.PopFlag = null;
      return;
    }
    this.PopFlag = new Long(popFlag);
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

  public void setReadFlag(long readFlag)
  {
    this.ReadFlag = new Long(readFlag);
  }

  public void setReadFlag(String readFlag)
  {
    if (readFlag == null) {
      this.ReadFlag = null;
      return;
    }
    this.ReadFlag = new Long(readFlag);
  }

  public void setRedirectURL(String redirectURL)
  {
    this.RedirectURL = redirectURL;
  }

  public void setSubject(String subject)
  {
    this.Subject = subject;
  }

  public void setToUser(String toUser)
  {
    this.ToUser = toUser;
  }
}
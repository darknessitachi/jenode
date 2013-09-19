package com.zving.schema;

import com.zving.framework.annotation.dao.Column;
import com.zving.framework.annotation.dao.Indexes;
import com.zving.framework.annotation.dao.Table;
import com.zving.framework.orm.DAO;
import com.zving.framework.utility.DateUtil;
import java.util.Date;

@Table("ZDModelTemplate")
@Indexes("")
public class ZDModelTemplate extends DAO<ZDModelTemplate>
{

  @Column(type=7, mandatory=true, pk=true)
  protected Long ModelID;

  @Column(type=1, length=50, mandatory=true, pk=true)
  protected String TemplateType;

  @Column(type=10, mandatory=true)
  protected String TemplateContent;

  @Column(type=1, length=400)
  protected String Memo;

  @Column(type=1, length=50)
  protected String Prop7;

  @Column(type=1, length=50)
  protected String Prop6;

  @Column(type=1, length=200)
  protected String Prop5;

  @Column(type=1, length=200)
  protected String Prop4;

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

  public String getProp7()
  {
    return this.Prop7;
  }

  public String getTemplateContent()
  {
    return this.TemplateContent;
  }

  public String getTemplateType()
  {
    return this.TemplateType;
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

  public void setProp7(String prop7)
  {
    this.Prop7 = prop7;
  }

  public void setTemplateContent(String templateContent)
  {
    this.TemplateContent = templateContent;
  }

  public void setTemplateType(String templateType)
  {
    this.TemplateType = templateType;
  }
}
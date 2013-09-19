package com.zving.schema;

import com.zving.framework.annotation.dao.Column;
import com.zving.framework.annotation.dao.Indexes;
import com.zving.framework.annotation.dao.Table;
import com.zving.framework.orm.DAO;
import com.zving.framework.utility.DateUtil;
import java.util.Date;

@Table("ZDMetaColumn")
@Indexes("")
public class ZDMetaColumn extends DAO<ZDMetaColumn>
{

  @Column(type=7, mandatory=true, pk=true)
  protected Long ID;

  @Column(type=7, mandatory=true)
  protected Long ModelID;

  @Column(type=7, mandatory=true)
  protected Long GroupID;

  @Column(type=1, length=50, mandatory=true)
  protected String Code;

  @Column(type=1, length=2000, mandatory=true)
  protected String Name;

  @Column(type=1, length=50, mandatory=true)
  protected String TargetField;

  @Column(type=1, length=20, mandatory=true)
  protected String DataType;

  @Column(type=1, length=20, mandatory=true)
  protected String ControlType;

  @Column(type=1, length=2, mandatory=true)
  protected String MandatoryFlag;

  @Column(type=1, length=2000)
  protected String ListOptions;

  @Column(type=1, length=1000)
  protected String DefaultValue;

  @Column(type=1, length=200)
  protected String VerifyRule;

  @Column(type=1, length=200)
  protected String VerifyCondition;

  @Column(type=1, length=200)
  protected String StyleClass;

  @Column(type=1, length=400)
  protected String StyleText;

  @Column(type=1, length=1000)
  protected String Memo;

  @Column(type=1, length=2)
  protected String FVisible;

  @Column(type=1, length=2)
  protected String BVisible;

  @Column(type=7)
  protected Long OrderFlag;

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

  public String getBVisible()
  {
    return this.BVisible;
  }

  public String getCode()
  {
    return this.Code;
  }

  public String getControlType()
  {
    return this.ControlType;
  }

  public String getDataType()
  {
    return this.DataType;
  }

  public String getDefaultValue()
  {
    return this.DefaultValue;
  }

  public String getFVisible()
  {
    return this.FVisible;
  }

  public long getGroupID()
  {
    if (this.GroupID == null) {
      return 0L;
    }
    return this.GroupID.longValue();
  }

  public long getID()
  {
    if (this.ID == null) {
      return 0L;
    }
    return this.ID.longValue();
  }

  public String getListOptions()
  {
    return this.ListOptions;
  }

  public String getMandatoryFlag()
  {
    return this.MandatoryFlag;
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

  public long getOrderFlag()
  {
    if (this.OrderFlag == null) {
      return 0L;
    }
    return this.OrderFlag.longValue();
  }

  public String getStyleClass()
  {
    return this.StyleClass;
  }

  public String getStyleText()
  {
    return this.StyleText;
  }

  public String getTargetField()
  {
    return this.TargetField;
  }

  public String getVerifyCondition()
  {
    return this.VerifyCondition;
  }

  public String getVerifyRule()
  {
    return this.VerifyRule;
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

  public void setBVisible(String bVisible)
  {
    this.BVisible = bVisible;
  }

  public void setCode(String code)
  {
    this.Code = code;
  }

  public void setControlType(String controlType)
  {
    this.ControlType = controlType;
  }

  public void setDataType(String dataType)
  {
    this.DataType = dataType;
  }

  public void setDefaultValue(String defaultValue)
  {
    this.DefaultValue = defaultValue;
  }

  public void setFVisible(String fVisible)
  {
    this.FVisible = fVisible;
  }

  public void setGroupID(long groupID)
  {
    this.GroupID = new Long(groupID);
  }

  public void setGroupID(String groupID)
  {
    if (groupID == null) {
      this.GroupID = null;
      return;
    }
    this.GroupID = new Long(groupID);
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

  public void setListOptions(String listOptions)
  {
    this.ListOptions = listOptions;
  }

  public void setMandatoryFlag(String mandatoryFlag)
  {
    this.MandatoryFlag = mandatoryFlag;
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

  public void setStyleClass(String styleClass)
  {
    this.StyleClass = styleClass;
  }

  public void setStyleText(String styleText)
  {
    this.StyleText = styleText;
  }

  public void setTargetField(String targetField)
  {
    this.TargetField = targetField;
  }

  public void setVerifyCondition(String verifyCondition)
  {
    this.VerifyCondition = verifyCondition;
  }

  public void setVerifyRule(String verifyRule)
  {
    this.VerifyRule = verifyRule;
  }
}
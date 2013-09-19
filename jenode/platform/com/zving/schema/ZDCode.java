package com.zving.schema;

import com.zving.framework.annotation.dao.Column;
import com.zving.framework.annotation.dao.Indexes;
import com.zving.framework.annotation.dao.Table;
import com.zving.framework.orm.DAO;
import com.zving.framework.utility.DateUtil;
import java.util.Date;

@Table("ZDCode")
@Indexes("")
public class ZDCode extends DAO<ZDCode>
{

  @Column(type=1, length=40, mandatory=true, pk=true)
  protected String CodeType;

  @Column(type=1, length=40, mandatory=true, pk=true)
  protected String ParentCode;

  @Column(type=1, length=40, mandatory=true, pk=true)
  protected String CodeValue;

  @Column(type=1, length=2000, mandatory=true)
  protected String CodeName;

  @Column(type=7, mandatory=true)
  protected Long CodeOrder;

  @Column(type=1, length=40)
  protected String Prop1;

  @Column(type=1, length=40)
  protected String Prop2;

  @Column(type=1, length=40)
  protected String Prop3;

  @Column(type=1, length=40)
  protected String Prop4;

  @Column(type=1, length=400)
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

  public String getCodeName()
  {
    return this.CodeName;
  }

  public long getCodeOrder()
  {
    if (this.CodeOrder == null) {
      return 0L;
    }
    return this.CodeOrder.longValue();
  }

  public String getCodeType()
  {
    return this.CodeType;
  }

  public String getCodeValue()
  {
    return this.CodeValue;
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

  public String getParentCode()
  {
    return this.ParentCode;
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

  public void setCodeName(String codeName)
  {
    this.CodeName = codeName;
  }

  public void setCodeOrder(long codeOrder)
  {
    this.CodeOrder = new Long(codeOrder);
  }

  public void setCodeOrder(String codeOrder)
  {
    if (codeOrder == null) {
      this.CodeOrder = null;
      return;
    }
    this.CodeOrder = new Long(codeOrder);
  }

  public void setCodeType(String codeType)
  {
    this.CodeType = codeType;
  }

  public void setCodeValue(String codeValue)
  {
    this.CodeValue = codeValue;
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

  public void setParentCode(String parentCode)
  {
    this.ParentCode = parentCode;
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
}
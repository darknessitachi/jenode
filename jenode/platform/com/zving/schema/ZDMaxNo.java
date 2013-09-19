package com.zving.schema;

import com.zving.framework.annotation.dao.Column;
import com.zving.framework.annotation.dao.Indexes;
import com.zving.framework.annotation.dao.Table;
import com.zving.framework.orm.DAO;

@Table("ZDMaxNo")
@Indexes("")
public class ZDMaxNo extends DAO<ZDMaxNo>
{

  @Column(type=1, length=20, mandatory=true, pk=true)
  protected String NoType;

  @Column(type=1, length=255, mandatory=true, pk=true)
  protected String NoSubType;

  @Column(type=7, mandatory=true)
  protected Long NoMaxValue;

  @Column(type=7)
  protected Long Length;

  public long getLength()
  {
    if (this.Length == null) {
      return 0L;
    }
    return this.Length.longValue();
  }

  public long getNoMaxValue()
  {
    if (this.NoMaxValue == null) {
      return 0L;
    }
    return this.NoMaxValue.longValue();
  }

  public String getNoSubType()
  {
    return this.NoSubType;
  }

  public String getNoType()
  {
    return this.NoType;
  }

  public void setLength(long length)
  {
    this.Length = new Long(length);
  }

  public void setLength(String length)
  {
    if (length == null) {
      this.Length = null;
      return;
    }
    this.Length = new Long(length);
  }

  public void setNoMaxValue(long noMaxValue)
  {
    this.NoMaxValue = new Long(noMaxValue);
  }

  public void setNoMaxValue(String noMaxValue)
  {
    if (noMaxValue == null) {
      this.NoMaxValue = null;
      return;
    }
    this.NoMaxValue = new Long(noMaxValue);
  }

  public void setNoSubType(String noSubType)
  {
    this.NoSubType = noSubType;
  }

  public void setNoType(String noType)
  {
    this.NoType = noType;
  }
}
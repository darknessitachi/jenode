package com.zving.schema;

import com.zving.framework.annotation.dao.Column;
import com.zving.framework.annotation.dao.Indexes;
import com.zving.framework.annotation.dao.Table;
import com.zving.framework.orm.DAO;
import com.zving.framework.utility.DateUtil;
import java.util.Date;

@Table("ZDMetaValue")
@Indexes("")
public class ZDMetaValue extends DAO<ZDMetaValue>
{

  @Column(type=7, mandatory=true, pk=true)
  protected Long ModelID;

  @Column(type=1, length=200, mandatory=true, pk=true)
  protected String PKValue;

  @Column(type=1, length=50)
  protected String ShortText1;

  @Column(type=1, length=50)
  protected String ShortText2;

  @Column(type=1, length=50)
  protected String ShortText3;

  @Column(type=1, length=50)
  protected String ShortText4;

  @Column(type=1, length=50)
  protected String ShortText5;

  @Column(type=1, length=50)
  protected String ShortText6;

  @Column(type=1, length=50)
  protected String ShortText7;

  @Column(type=1, length=50)
  protected String ShortText8;

  @Column(type=1, length=50)
  protected String ShortText9;

  @Column(type=1, length=50)
  protected String ShortText10;

  @Column(type=1, length=50)
  protected String ShortText11;

  @Column(type=1, length=50)
  protected String ShortText12;

  @Column(type=1, length=50)
  protected String ShortText13;

  @Column(type=1, length=50)
  protected String ShortText14;

  @Column(type=1, length=50)
  protected String ShortText15;

  @Column(type=1, length=50)
  protected String ShortText16;

  @Column(type=1, length=50)
  protected String ShortText17;

  @Column(type=1, length=50)
  protected String ShortText18;

  @Column(type=1, length=50)
  protected String ShortText19;

  @Column(type=1, length=50)
  protected String ShortText20;

  @Column(type=1, length=50)
  protected String ShortText21;

  @Column(type=1, length=50)
  protected String ShortText22;

  @Column(type=1, length=50)
  protected String ShortText23;

  @Column(type=1, length=50)
  protected String ShortText24;

  @Column(type=1, length=50)
  protected String ShortText25;

  @Column(type=1, length=200)
  protected String MediumText1;

  @Column(type=1, length=200)
  protected String MediumText2;

  @Column(type=1, length=200)
  protected String MediumText3;

  @Column(type=1, length=200)
  protected String MediumText4;

  @Column(type=1, length=200)
  protected String MediumText5;

  @Column(type=1, length=200)
  protected String MediumText6;

  @Column(type=1, length=200)
  protected String MediumText7;

  @Column(type=1, length=200)
  protected String MediumText8;

  @Column(type=1, length=200)
  protected String MediumText9;

  @Column(type=1, length=200)
  protected String MediumText10;

  @Column(type=1, length=200)
  protected String MediumText11;

  @Column(type=1, length=200)
  protected String MediumText12;

  @Column(type=1, length=200)
  protected String MediumText13;

  @Column(type=1, length=200)
  protected String MediumText14;

  @Column(type=1, length=200)
  protected String MediumText15;

  @Column(type=1, length=200)
  protected String MediumText16;

  @Column(type=1, length=200)
  protected String MediumText17;

  @Column(type=1, length=200)
  protected String MediumText18;

  @Column(type=1, length=200)
  protected String MediumText19;

  @Column(type=1, length=200)
  protected String MediumText20;

  @Column(type=1, length=200)
  protected String MediumText21;

  @Column(type=1, length=200)
  protected String MediumText22;

  @Column(type=1, length=200)
  protected String MediumText23;

  @Column(type=1, length=200)
  protected String MediumText24;

  @Column(type=1, length=200)
  protected String MediumText25;

  @Column(type=1, length=2000)
  protected String LargeText1;

  @Column(type=1, length=2000)
  protected String LargeText2;

  @Column(type=1, length=2000)
  protected String LargeText3;

  @Column(type=1, length=2000)
  protected String LargeText4;

  @Column(type=10)
  protected String ClobText1;

  @Column(type=7)
  protected Long Long1;

  @Column(type=7)
  protected Long Long2;

  @Column(type=7)
  protected Long Long3;

  @Column(type=7)
  protected Long Long4;

  @Column(type=7)
  protected Long Long5;

  @Column(type=7)
  protected Long Long6;

  @Column(type=7)
  protected Long Long7;

  @Column(type=7)
  protected Long Long8;

  @Column(type=7)
  protected Long Long9;

  @Column(type=7)
  protected Long Long10;

  @Column(type=6)
  protected Double Double1;

  @Column(type=6)
  protected Double Double2;

  @Column(type=6)
  protected Double Double3;

  @Column(type=6)
  protected Double Double4;

  @Column(type=6)
  protected Double Double5;

  @Column(type=6)
  protected Double Double6;

  @Column(type=6)
  protected Double Double7;

  @Column(type=6)
  protected Double Double8;

  @Column(type=6)
  protected Double Double9;

  @Column(type=6)
  protected Double Double10;

  @Column(type=12)
  protected Date Date1;

  @Column(type=12)
  protected Date Date2;

  @Column(type=12)
  protected Date Date3;

  @Column(type=12)
  protected Date Date4;

  @Column(type=12)
  protected Date Date5;

  @Column(type=12)
  protected Date Date6;

  @Column(type=12)
  protected Date Date7;

  @Column(type=12)
  protected Date Date8;

  @Column(type=12)
  protected Date Date9;

  @Column(type=12)
  protected Date Date10;

  public String getClobText1()
  {
    return this.ClobText1;
  }

  public Date getDate1()
  {
    return this.Date1;
  }

  public Date getDate10()
  {
    return this.Date10;
  }

  public Date getDate2()
  {
    return this.Date2;
  }

  public Date getDate3()
  {
    return this.Date3;
  }

  public Date getDate4()
  {
    return this.Date4;
  }

  public Date getDate5()
  {
    return this.Date5;
  }

  public Date getDate6()
  {
    return this.Date6;
  }

  public Date getDate7()
  {
    return this.Date7;
  }

  public Date getDate8()
  {
    return this.Date8;
  }

  public Date getDate9()
  {
    return this.Date9;
  }

  public double getDouble1()
  {
    if (this.Double1 == null) {
      return 0.0D;
    }
    return this.Double1.doubleValue();
  }

  public double getDouble10()
  {
    if (this.Double10 == null) {
      return 0.0D;
    }
    return this.Double10.doubleValue();
  }

  public double getDouble2()
  {
    if (this.Double2 == null) {
      return 0.0D;
    }
    return this.Double2.doubleValue();
  }

  public double getDouble3()
  {
    if (this.Double3 == null) {
      return 0.0D;
    }
    return this.Double3.doubleValue();
  }

  public double getDouble4()
  {
    if (this.Double4 == null) {
      return 0.0D;
    }
    return this.Double4.doubleValue();
  }

  public double getDouble5()
  {
    if (this.Double5 == null) {
      return 0.0D;
    }
    return this.Double5.doubleValue();
  }

  public double getDouble6()
  {
    if (this.Double6 == null) {
      return 0.0D;
    }
    return this.Double6.doubleValue();
  }

  public double getDouble7()
  {
    if (this.Double7 == null) {
      return 0.0D;
    }
    return this.Double7.doubleValue();
  }

  public double getDouble8()
  {
    if (this.Double8 == null) {
      return 0.0D;
    }
    return this.Double8.doubleValue();
  }

  public double getDouble9()
  {
    if (this.Double9 == null) {
      return 0.0D;
    }
    return this.Double9.doubleValue();
  }

  public String getLargeText1()
  {
    return this.LargeText1;
  }

  public String getLargeText2()
  {
    return this.LargeText2;
  }

  public String getLargeText3()
  {
    return this.LargeText3;
  }

  public String getLargeText4()
  {
    return this.LargeText4;
  }

  public long getLong1()
  {
    if (this.Long1 == null) {
      return 0L;
    }
    return this.Long1.longValue();
  }

  public long getLong10()
  {
    if (this.Long10 == null) {
      return 0L;
    }
    return this.Long10.longValue();
  }

  public long getLong2()
  {
    if (this.Long2 == null) {
      return 0L;
    }
    return this.Long2.longValue();
  }

  public long getLong3()
  {
    if (this.Long3 == null) {
      return 0L;
    }
    return this.Long3.longValue();
  }

  public long getLong4()
  {
    if (this.Long4 == null) {
      return 0L;
    }
    return this.Long4.longValue();
  }

  public long getLong5()
  {
    if (this.Long5 == null) {
      return 0L;
    }
    return this.Long5.longValue();
  }

  public long getLong6()
  {
    if (this.Long6 == null) {
      return 0L;
    }
    return this.Long6.longValue();
  }

  public long getLong7()
  {
    if (this.Long7 == null) {
      return 0L;
    }
    return this.Long7.longValue();
  }

  public long getLong8()
  {
    if (this.Long8 == null) {
      return 0L;
    }
    return this.Long8.longValue();
  }

  public long getLong9()
  {
    if (this.Long9 == null) {
      return 0L;
    }
    return this.Long9.longValue();
  }

  public String getMediumText1()
  {
    return this.MediumText1;
  }

  public String getMediumText10()
  {
    return this.MediumText10;
  }

  public String getMediumText11()
  {
    return this.MediumText11;
  }

  public String getMediumText12()
  {
    return this.MediumText12;
  }

  public String getMediumText13()
  {
    return this.MediumText13;
  }

  public String getMediumText14()
  {
    return this.MediumText14;
  }

  public String getMediumText15()
  {
    return this.MediumText15;
  }

  public String getMediumText16()
  {
    return this.MediumText16;
  }

  public String getMediumText17()
  {
    return this.MediumText17;
  }

  public String getMediumText18()
  {
    return this.MediumText18;
  }

  public String getMediumText19()
  {
    return this.MediumText19;
  }

  public String getMediumText2()
  {
    return this.MediumText2;
  }

  public String getMediumText20()
  {
    return this.MediumText20;
  }

  public String getMediumText21()
  {
    return this.MediumText21;
  }

  public String getMediumText22()
  {
    return this.MediumText22;
  }

  public String getMediumText23()
  {
    return this.MediumText23;
  }

  public String getMediumText24()
  {
    return this.MediumText24;
  }

  public String getMediumText25()
  {
    return this.MediumText25;
  }

  public String getMediumText3()
  {
    return this.MediumText3;
  }

  public String getMediumText4()
  {
    return this.MediumText4;
  }

  public String getMediumText5()
  {
    return this.MediumText5;
  }

  public String getMediumText6()
  {
    return this.MediumText6;
  }

  public String getMediumText7()
  {
    return this.MediumText7;
  }

  public String getMediumText8()
  {
    return this.MediumText8;
  }

  public String getMediumText9()
  {
    return this.MediumText9;
  }

  public long getModelID()
  {
    if (this.ModelID == null) {
      return 0L;
    }
    return this.ModelID.longValue();
  }

  public String getPKValue()
  {
    return this.PKValue;
  }

  public String getShortText1()
  {
    return this.ShortText1;
  }

  public String getShortText10()
  {
    return this.ShortText10;
  }

  public String getShortText11()
  {
    return this.ShortText11;
  }

  public String getShortText12()
  {
    return this.ShortText12;
  }

  public String getShortText13()
  {
    return this.ShortText13;
  }

  public String getShortText14()
  {
    return this.ShortText14;
  }

  public String getShortText15()
  {
    return this.ShortText15;
  }

  public String getShortText16()
  {
    return this.ShortText16;
  }

  public String getShortText17()
  {
    return this.ShortText17;
  }

  public String getShortText18()
  {
    return this.ShortText18;
  }

  public String getShortText19()
  {
    return this.ShortText19;
  }

  public String getShortText2()
  {
    return this.ShortText2;
  }

  public String getShortText20()
  {
    return this.ShortText20;
  }

  public String getShortText21()
  {
    return this.ShortText21;
  }

  public String getShortText22()
  {
    return this.ShortText22;
  }

  public String getShortText23()
  {
    return this.ShortText23;
  }

  public String getShortText24()
  {
    return this.ShortText24;
  }

  public String getShortText25()
  {
    return this.ShortText25;
  }

  public String getShortText3()
  {
    return this.ShortText3;
  }

  public String getShortText4()
  {
    return this.ShortText4;
  }

  public String getShortText5()
  {
    return this.ShortText5;
  }

  public String getShortText6()
  {
    return this.ShortText6;
  }

  public String getShortText7()
  {
    return this.ShortText7;
  }

  public String getShortText8()
  {
    return this.ShortText8;
  }

  public String getShortText9()
  {
    return this.ShortText9;
  }

  public void setClobText1(String clobText1)
  {
    this.ClobText1 = clobText1;
  }

  public void setDate1(Date date1)
  {
    this.Date1 = date1;
  }

  public void setDate1(String date1)
  {
    if (date1 == null) {
      this.Date1 = null;
      return;
    }
    this.Date1 = DateUtil.parseDateTime(date1);
  }

  public void setDate10(Date date10)
  {
    this.Date10 = date10;
  }

  public void setDate10(String date10)
  {
    if (date10 == null) {
      this.Date10 = null;
      return;
    }
    this.Date10 = DateUtil.parseDateTime(date10);
  }

  public void setDate2(Date date2)
  {
    this.Date2 = date2;
  }

  public void setDate2(String date2)
  {
    if (date2 == null) {
      this.Date2 = null;
      return;
    }
    this.Date2 = DateUtil.parseDateTime(date2);
  }

  public void setDate3(Date date3)
  {
    this.Date3 = date3;
  }

  public void setDate3(String date3)
  {
    if (date3 == null) {
      this.Date3 = null;
      return;
    }
    this.Date3 = DateUtil.parseDateTime(date3);
  }

  public void setDate4(Date date4)
  {
    this.Date4 = date4;
  }

  public void setDate4(String date4)
  {
    if (date4 == null) {
      this.Date4 = null;
      return;
    }
    this.Date4 = DateUtil.parseDateTime(date4);
  }

  public void setDate5(Date date5)
  {
    this.Date5 = date5;
  }

  public void setDate5(String date5)
  {
    if (date5 == null) {
      this.Date5 = null;
      return;
    }
    this.Date5 = DateUtil.parseDateTime(date5);
  }

  public void setDate6(Date date6)
  {
    this.Date6 = date6;
  }

  public void setDate6(String date6)
  {
    if (date6 == null) {
      this.Date6 = null;
      return;
    }
    this.Date6 = DateUtil.parseDateTime(date6);
  }

  public void setDate7(Date date7)
  {
    this.Date7 = date7;
  }

  public void setDate7(String date7)
  {
    if (date7 == null) {
      this.Date7 = null;
      return;
    }
    this.Date7 = DateUtil.parseDateTime(date7);
  }

  public void setDate8(Date date8)
  {
    this.Date8 = date8;
  }

  public void setDate8(String date8)
  {
    if (date8 == null) {
      this.Date8 = null;
      return;
    }
    this.Date8 = DateUtil.parseDateTime(date8);
  }

  public void setDate9(Date date9)
  {
    this.Date9 = date9;
  }

  public void setDate9(String date9)
  {
    if (date9 == null) {
      this.Date9 = null;
      return;
    }
    this.Date9 = DateUtil.parseDateTime(date9);
  }

  public void setDouble1(double double1)
  {
    this.Double1 = new Double(double1);
  }

  public void setDouble1(String double1)
  {
    if (double1 == null) {
      this.Double1 = null;
      return;
    }
    this.Double1 = new Double(double1);
  }

  public void setDouble10(double double10)
  {
    this.Double10 = new Double(double10);
  }

  public void setDouble10(String double10)
  {
    if (double10 == null) {
      this.Double10 = null;
      return;
    }
    this.Double10 = new Double(double10);
  }

  public void setDouble2(double double2)
  {
    this.Double2 = new Double(double2);
  }

  public void setDouble2(String double2)
  {
    if (double2 == null) {
      this.Double2 = null;
      return;
    }
    this.Double2 = new Double(double2);
  }

  public void setDouble3(double double3)
  {
    this.Double3 = new Double(double3);
  }

  public void setDouble3(String double3)
  {
    if (double3 == null) {
      this.Double3 = null;
      return;
    }
    this.Double3 = new Double(double3);
  }

  public void setDouble4(double double4)
  {
    this.Double4 = new Double(double4);
  }

  public void setDouble4(String double4)
  {
    if (double4 == null) {
      this.Double4 = null;
      return;
    }
    this.Double4 = new Double(double4);
  }

  public void setDouble5(double double5)
  {
    this.Double5 = new Double(double5);
  }

  public void setDouble5(String double5)
  {
    if (double5 == null) {
      this.Double5 = null;
      return;
    }
    this.Double5 = new Double(double5);
  }

  public void setDouble6(double double6)
  {
    this.Double6 = new Double(double6);
  }

  public void setDouble6(String double6)
  {
    if (double6 == null) {
      this.Double6 = null;
      return;
    }
    this.Double6 = new Double(double6);
  }

  public void setDouble7(double double7)
  {
    this.Double7 = new Double(double7);
  }

  public void setDouble7(String double7)
  {
    if (double7 == null) {
      this.Double7 = null;
      return;
    }
    this.Double7 = new Double(double7);
  }

  public void setDouble8(double double8)
  {
    this.Double8 = new Double(double8);
  }

  public void setDouble8(String double8)
  {
    if (double8 == null) {
      this.Double8 = null;
      return;
    }
    this.Double8 = new Double(double8);
  }

  public void setDouble9(double double9)
  {
    this.Double9 = new Double(double9);
  }

  public void setDouble9(String double9)
  {
    if (double9 == null) {
      this.Double9 = null;
      return;
    }
    this.Double9 = new Double(double9);
  }

  public void setLargeText1(String largeText1)
  {
    this.LargeText1 = largeText1;
  }

  public void setLargeText2(String largeText2)
  {
    this.LargeText2 = largeText2;
  }

  public void setLargeText3(String largeText3)
  {
    this.LargeText3 = largeText3;
  }

  public void setLargeText4(String largeText4)
  {
    this.LargeText4 = largeText4;
  }

  public void setLong1(long long1)
  {
    this.Long1 = new Long(long1);
  }

  public void setLong1(String long1)
  {
    if (long1 == null) {
      this.Long1 = null;
      return;
    }
    this.Long1 = new Long(long1);
  }

  public void setLong10(long long10)
  {
    this.Long10 = new Long(long10);
  }

  public void setLong10(String long10)
  {
    if (long10 == null) {
      this.Long10 = null;
      return;
    }
    this.Long10 = new Long(long10);
  }

  public void setLong2(long long2)
  {
    this.Long2 = new Long(long2);
  }

  public void setLong2(String long2)
  {
    if (long2 == null) {
      this.Long2 = null;
      return;
    }
    this.Long2 = new Long(long2);
  }

  public void setLong3(long long3)
  {
    this.Long3 = new Long(long3);
  }

  public void setLong3(String long3)
  {
    if (long3 == null) {
      this.Long3 = null;
      return;
    }
    this.Long3 = new Long(long3);
  }

  public void setLong4(long long4)
  {
    this.Long4 = new Long(long4);
  }

  public void setLong4(String long4)
  {
    if (long4 == null) {
      this.Long4 = null;
      return;
    }
    this.Long4 = new Long(long4);
  }

  public void setLong5(long long5)
  {
    this.Long5 = new Long(long5);
  }

  public void setLong5(String long5)
  {
    if (long5 == null) {
      this.Long5 = null;
      return;
    }
    this.Long5 = new Long(long5);
  }

  public void setLong6(long long6)
  {
    this.Long6 = new Long(long6);
  }

  public void setLong6(String long6)
  {
    if (long6 == null) {
      this.Long6 = null;
      return;
    }
    this.Long6 = new Long(long6);
  }

  public void setLong7(long long7)
  {
    this.Long7 = new Long(long7);
  }

  public void setLong7(String long7)
  {
    if (long7 == null) {
      this.Long7 = null;
      return;
    }
    this.Long7 = new Long(long7);
  }

  public void setLong8(long long8)
  {
    this.Long8 = new Long(long8);
  }

  public void setLong8(String long8)
  {
    if (long8 == null) {
      this.Long8 = null;
      return;
    }
    this.Long8 = new Long(long8);
  }

  public void setLong9(long long9)
  {
    this.Long9 = new Long(long9);
  }

  public void setLong9(String long9)
  {
    if (long9 == null) {
      this.Long9 = null;
      return;
    }
    this.Long9 = new Long(long9);
  }

  public void setMediumText1(String mediumText1)
  {
    this.MediumText1 = mediumText1;
  }

  public void setMediumText10(String mediumText10)
  {
    this.MediumText10 = mediumText10;
  }

  public void setMediumText11(String mediumText11)
  {
    this.MediumText11 = mediumText11;
  }

  public void setMediumText12(String mediumText12)
  {
    this.MediumText12 = mediumText12;
  }

  public void setMediumText13(String mediumText13)
  {
    this.MediumText13 = mediumText13;
  }

  public void setMediumText14(String mediumText14)
  {
    this.MediumText14 = mediumText14;
  }

  public void setMediumText15(String mediumText15)
  {
    this.MediumText15 = mediumText15;
  }

  public void setMediumText16(String mediumText16)
  {
    this.MediumText16 = mediumText16;
  }

  public void setMediumText17(String mediumText17)
  {
    this.MediumText17 = mediumText17;
  }

  public void setMediumText18(String mediumText18)
  {
    this.MediumText18 = mediumText18;
  }

  public void setMediumText19(String mediumText19)
  {
    this.MediumText19 = mediumText19;
  }

  public void setMediumText2(String mediumText2)
  {
    this.MediumText2 = mediumText2;
  }

  public void setMediumText20(String mediumText20)
  {
    this.MediumText20 = mediumText20;
  }

  public void setMediumText21(String mediumText21)
  {
    this.MediumText21 = mediumText21;
  }

  public void setMediumText22(String mediumText22)
  {
    this.MediumText22 = mediumText22;
  }

  public void setMediumText23(String mediumText23)
  {
    this.MediumText23 = mediumText23;
  }

  public void setMediumText24(String mediumText24)
  {
    this.MediumText24 = mediumText24;
  }

  public void setMediumText25(String mediumText25)
  {
    this.MediumText25 = mediumText25;
  }

  public void setMediumText3(String mediumText3)
  {
    this.MediumText3 = mediumText3;
  }

  public void setMediumText4(String mediumText4)
  {
    this.MediumText4 = mediumText4;
  }

  public void setMediumText5(String mediumText5)
  {
    this.MediumText5 = mediumText5;
  }

  public void setMediumText6(String mediumText6)
  {
    this.MediumText6 = mediumText6;
  }

  public void setMediumText7(String mediumText7)
  {
    this.MediumText7 = mediumText7;
  }

  public void setMediumText8(String mediumText8)
  {
    this.MediumText8 = mediumText8;
  }

  public void setMediumText9(String mediumText9)
  {
    this.MediumText9 = mediumText9;
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

  public void setPKValue(String pKValue)
  {
    this.PKValue = pKValue;
  }

  public void setShortText1(String shortText1)
  {
    this.ShortText1 = shortText1;
  }

  public void setShortText10(String shortText10)
  {
    this.ShortText10 = shortText10;
  }

  public void setShortText11(String shortText11)
  {
    this.ShortText11 = shortText11;
  }

  public void setShortText12(String shortText12)
  {
    this.ShortText12 = shortText12;
  }

  public void setShortText13(String shortText13)
  {
    this.ShortText13 = shortText13;
  }

  public void setShortText14(String shortText14)
  {
    this.ShortText14 = shortText14;
  }

  public void setShortText15(String shortText15)
  {
    this.ShortText15 = shortText15;
  }

  public void setShortText16(String shortText16)
  {
    this.ShortText16 = shortText16;
  }

  public void setShortText17(String shortText17)
  {
    this.ShortText17 = shortText17;
  }

  public void setShortText18(String shortText18)
  {
    this.ShortText18 = shortText18;
  }

  public void setShortText19(String shortText19)
  {
    this.ShortText19 = shortText19;
  }

  public void setShortText2(String shortText2)
  {
    this.ShortText2 = shortText2;
  }

  public void setShortText20(String shortText20)
  {
    this.ShortText20 = shortText20;
  }

  public void setShortText21(String shortText21)
  {
    this.ShortText21 = shortText21;
  }

  public void setShortText22(String shortText22)
  {
    this.ShortText22 = shortText22;
  }

  public void setShortText23(String shortText23)
  {
    this.ShortText23 = shortText23;
  }

  public void setShortText24(String shortText24)
  {
    this.ShortText24 = shortText24;
  }

  public void setShortText25(String shortText25)
  {
    this.ShortText25 = shortText25;
  }

  public void setShortText3(String shortText3)
  {
    this.ShortText3 = shortText3;
  }

  public void setShortText4(String shortText4)
  {
    this.ShortText4 = shortText4;
  }

  public void setShortText5(String shortText5)
  {
    this.ShortText5 = shortText5;
  }

  public void setShortText6(String shortText6)
  {
    this.ShortText6 = shortText6;
  }

  public void setShortText7(String shortText7)
  {
    this.ShortText7 = shortText7;
  }

  public void setShortText8(String shortText8)
  {
    this.ShortText8 = shortText8;
  }

  public void setShortText9(String shortText9)
  {
    this.ShortText9 = shortText9;
  }
}
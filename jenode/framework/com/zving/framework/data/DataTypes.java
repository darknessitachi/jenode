package com.zving.framework.data;

public class DataTypes
{
  public static final int DATETIME = 12;
  public static final int STRING = 1;
  public static final int BLOB = 2;
  public static final int BIGDECIMAL = 3;
  public static final int DECIMAL = 4;
  public static final int FLOAT = 5;
  public static final int DOUBLE = 6;
  public static final int LONG = 7;
  public static final int INTEGER = 8;
  public static final int SMALLINT = 9;
  public static final int CLOB = 10;
  public static final int BIT = 11;

  public static String toString(int type)
  {
    switch (type) {
    case 12:
      return "DateTime";
    case 1:
      return "String";
    case 2:
      return "BLOB";
    case 3:
      return "BigDecimal";
    case 4:
      return "Decimal";
    case 5:
      return "Float";
    case 6:
      return "Double";
    case 7:
      return "Long";
    case 8:
      return "Integer";
    case 9:
      return "SmallInt";
    case 10:
      return "Clob";
    case 11:
      return "Bit";
    }
    return "Unknown";
  }
}
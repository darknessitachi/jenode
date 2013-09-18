package com.zving.framework.orm;

import java.io.Serializable;

public class DAOColumn
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private int ColumnType;
  private String ColumnName;
  private int Length;
  private int Precision;
  private boolean Mandatory;
  private boolean isPrimaryKey;

  public DAOColumn(String name, int type, int length, int precision, boolean mandatory, boolean ispk)
  {
    this.ColumnType = type;
    this.ColumnName = name;
    this.Length = length;
    this.Precision = precision;
    this.Mandatory = mandatory;
    this.isPrimaryKey = ispk;
  }

  public String getColumnName()
  {
    return this.ColumnName;
  }

  public int getColumnType()
  {
    return this.ColumnType;
  }

  public boolean isPrimaryKey()
  {
    return this.isPrimaryKey;
  }

  public int getLength()
  {
    return this.Length;
  }

  public int getPrecision()
  {
    return this.Precision;
  }

  public boolean isMandatory()
  {
    return this.Mandatory;
  }
}
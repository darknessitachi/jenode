package com.zving.framework.data;

import com.zving.framework.Config;
import com.zving.framework.collection.Filter;
import com.zving.framework.collection.Mapx;
import com.zving.framework.utility.DateUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.framework.utility.commons.ArrayUtils;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class DataTable
  implements Serializable, Cloneable, Iterable<DataRow>
{
  private static final long serialVersionUID = 1L;
  private boolean isWebMode;
  private DataRow[] rows;
  private DataColumn[] columns;

  public DataTable()
  {
    this.rows = new DataRow[0];
    this.columns = new DataColumn[0];
  }

  public DataTable(DataColumn[] types, Object[][] values) {
    if (types == null) {
      types = new DataColumn[0];
    }
    this.columns = types;
    this.rows = null;
    renameAmbiguousColumns(this.columns);
    if (values != null) {
      this.rows = new DataRow[values.length];
      for (int i = 0; i < this.rows.length; i++)
        this.rows[i] = new DataRow(this.columns, values[i]);
    }
    else {
      this.rows = new DataRow[0];
    }
  }

  public boolean renameAmbiguousColumns(DataColumn[] types)
  {
    if (types == null) {
      return false;
    }
    for (int i = 0; i < types.length; i++) {
      String columnName = types[i].getColumnName();
      int count = 1;
      for (int j = i + 1; j < types.length; j++) {
        if (columnName == null) {
          throw new RuntimeException("Column name cann't be null,index is " + i);
        }
        if (columnName.equalsIgnoreCase(types[j].getColumnName())) {
          types[j].ColumnName = (columnName + "_" + String.valueOf(++count));
        }
      }
    }
    return true;
  }

  public DataTable(ResultSet rs) {
    this(rs, 2147483647, 0, false);
  }

  public DataTable(ResultSet rs, boolean latin1Flag) {
    this(rs, 2147483647, 0, latin1Flag);
  }

  public DataTable(ResultSet rs, int pageSize, int pageIndex) {
    this(rs, pageSize, pageIndex, false);
  }

  public DataTable(ResultSet rs, int pageSize, int pageIndex, boolean latin1Flag)
  {
    try
    {
      ResultSetMetaData rsmd = rs.getMetaData();
      int columnCount = rsmd.getColumnCount();
      DataColumn[] types = new DataColumn[columnCount];
      for (int i = 1; i <= columnCount; i++) {
        String name = rsmd.getColumnLabel(i);
        boolean b = rsmd.isNullable(i) == 1;
        DataColumn dc = new DataColumn();
        dc.setAllowNull(b);
        dc.setColumnName(name);

        int dataType = rsmd.getColumnType(i);
        if ((dataType == 1) || (dataType == 12)) {
          dc.ColumnType = 1;
        } else if ((dataType == 93) || (dataType == 91)) {
          dc.ColumnType = 12;
        } else if (dataType == 3) {
          dc.ColumnType = 4;
        } else if ((dataType == 8) || (dataType == 7)) {
          dc.ColumnType = 6;
        } else if (dataType == 6) {
          dc.ColumnType = 5;
        } else if (dataType == 4) {
          dc.ColumnType = 8;
        } else if ((dataType == 5) || (dataType == -6)) {
          dc.ColumnType = 9;
        } else if (dataType == -7) {
          dc.ColumnType = 11;
        } else if (dataType == -5) {
          dc.ColumnType = 7;
        } else if ((dataType == 2004) || (dataType == -4)) {
          dc.ColumnType = 2;
        } else if ((dataType == 2005) || (dataType == -1)) {
          dc.ColumnType = 10;
        } else if (dataType == 2) {
          int dataScale = rsmd.getScale(i);
          int dataPrecision = rsmd.getPrecision(i);
          if (dataScale == 0) {
            if (dataPrecision == 0)
              dc.ColumnType = 3;
            else
              dc.ColumnType = 7;
          }
          else
            dc.ColumnType = 3;
        }
        else {
          dc.ColumnType = 1;
        }
        types[(i - 1)] = dc;
      }

      this.columns = types;
      renameAmbiguousColumns(this.columns);

      ArrayList list = new ArrayList();
      int index = 0;
      int begin = pageIndex * pageSize;
      int end = (pageIndex + 1) * pageSize;
      while (rs.next()) {
        if (index >= end) {
          break;
        }
        if (index >= begin) {
          Object[] t = new Object[columnCount];
          for (int j = 1; j <= columnCount; j++) {
            if (this.columns[(j - 1)].getColumnType() == 10) {
              String str = DBUtil.clobToString(rs.getClob(j));
              if ((latin1Flag) && (StringUtil.isNotEmpty(str))) {
                try {
                  str = new String(str.getBytes("ISO-8859-1"), Config.getGlobalCharset());
                } catch (UnsupportedEncodingException e) {
                  e.printStackTrace();
                }
              }
              if (" ".equals(str)) {
                str = "";
              }
              t[(j - 1)] = str;
            } else if (this.columns[(j - 1)].getColumnType() == 2) {
              t[(j - 1)] = DBUtil.blobToBytes(rs.getBlob(j));
            } else if (this.columns[(j - 1)].getColumnType() == 12) {
              Object obj = rs.getObject(j);
              if ((obj instanceof Date))
                t[(j - 1)] = obj;
              else
                t[(j - 1)] = DBUtil.getOracleTimestamp(obj);
            }
            else if (this.columns[(j - 1)].getColumnType() == 11) {
              t[(j - 1)] = (("true".equals(rs.getString(j))) || ("1".equals(rs.getString(j))) ? "1" : "0");
            } else if (this.columns[(j - 1)].getColumnType() == 1) {
              String str = rs.getString(j);
              if ((latin1Flag) && (StringUtil.isNotEmpty(str))) {
                try {
                  str = new String(str.getBytes("ISO-8859-1"), Config.getGlobalCharset());
                } catch (UnsupportedEncodingException e) {
                  e.printStackTrace();
                }
              }
              t[(j - 1)] = str;
            } else {
              t[(j - 1)] = rs.getObject(j);
            }
          }
          DataRow tmpRow = new DataRow(this.columns, t);
          list.add(tmpRow);
        }
        index++;
      }
      this.rows = new DataRow[list.size()];
      list.toArray(this.rows);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public DataTable deleteColumn(int columnIndex) {
    if (this.columns.length == 0) {
      return this;
    }
    if ((columnIndex < 0) || ((this.columns != null) && (columnIndex >= this.columns.length))) {
      throw new RuntimeException("Index is out of range：" + columnIndex);
    }
    this.columns = ((DataColumn[])ArrayUtils.remove(this.columns, columnIndex));
    for (int i = 0; i < this.rows.length; i++) {
      this.rows[i].columns = null;
      this.rows[i].columns = this.columns;
      this.rows[i].values = ArrayUtils.remove(this.rows[i].values, columnIndex);
    }
    return this;
  }

  public DataTable deleteColumn(String columnName) {
    if (this.columns.length == 0) {
      return this;
    }
    for (int i = 0; i < this.columns.length; i++) {
      if (this.columns[i].getColumnName().equalsIgnoreCase(columnName)) {
        deleteColumn(i);
        break;
      }
    }
    return this;
  }

  public DataTable insertColumn(String columnName) {
    return insertColumn(new DataColumn(columnName, 1), null, this.columns.length);
  }

  public DataTable insertColumn(String columnName, Object columnValue) {
    Object[] cv = new Object[this.rows.length];
    for (int i = 0; i < cv.length; i++) {
      cv[i] = columnValue;
    }
    return insertColumn(new DataColumn(columnName, 1), cv, this.columns.length);
  }

  public DataTable insertColumns(String[] columnNames) {
    for (int i = 0; i < columnNames.length; i++) {
      insertColumn(new DataColumn(columnNames[i], 1), null, this.columns.length);
    }
    return this;
  }

  public DataTable insertColumn(String columnName, Object[] columnValue) {
    return insertColumn(new DataColumn(columnName, 1), columnValue, this.columns.length);
  }

  public DataTable insertColumn(DataColumn dc) {
    return insertColumn(dc, null, this.columns.length);
  }

  public DataTable insertColumn(DataColumn dc, Object[] columnValue) {
    return insertColumn(dc, columnValue, this.columns.length);
  }

  public DataTable insertColumn(String columnName, Object[] columnValue, int index) {
    return insertColumn(new DataColumn(columnName, 1), columnValue, index);
  }

  public DataTable insertColumn(DataColumn dc, Object[] columnValue, int index) {
    if (index > this.columns.length) {
      throw new RuntimeException("Index is out of range:" + index);
    }
    for (int i = 0; i < this.columns.length; i++) {
      if (this.columns[i].getColumnName().equalsIgnoreCase(dc.getColumnName())) {
        throw new RuntimeException("Column is exist:" + dc.getColumnName());
      }
    }
    this.columns = ((DataColumn[])ArrayUtils.add(this.columns, index, dc));
    if (columnValue == null) {
      columnValue = new Object[this.rows.length];
    }
    if (this.rows.length == 0) {
      this.rows = new DataRow[columnValue.length];
      for (int i = 0; i < this.rows.length; i++)
        this.rows[i] = new DataRow(this.columns, new Object[] { columnValue[i] });
    }
    else {
      for (int i = 0; i < this.rows.length; i++) {
        this.rows[i].columns = null;
        this.rows[i].columns = this.columns;
        this.rows[i].values = ArrayUtils.add(this.rows[i].values, index, columnValue[i]);
      }
    }
    return this;
  }

  public DataTable insertRow(DataRow dr) {
    return insertRow(dr, this.rows.length);
  }

  public DataTable insertRow(DataRow dr, int index) {
    if (this.columns.length == 0) {
      this.columns = dr.columns;
    }
    return insertRow(dr.getDataValues(), index);
  }

  public DataTable insertRow(Object[] rowValue) {
    return insertRow(rowValue, this.rows.length);
  }

  public DataTable insertRow(Object[] rowValue, int index) {
    if (index > this.rows.length) {
      throw new RuntimeException(index + " is out of range ,max is " + this.rows.length);
    }
    if (rowValue != null) {
      if (this.columns.length == 0) {
        this.columns = new DataColumn[rowValue.length];
        for (int i = 0; i < this.columns.length; i++) {
          this.columns[i] = new DataColumn("_Columns_" + i, 1);
        }
      }
      if (rowValue.length != this.columns.length) {
        throw new RuntimeException("Parameter's length is " + rowValue.length + "，but column count is " + this.columns.length);
      }
      for (int i = 0; i < this.columns.length; i++)
        if (this.columns[i].ColumnType == 12) {
          Object v = rowValue[i];
          if ((v != null) && 
            (!Date.class.isInstance(v))) {
            Date d = DateUtil.parseDateTime(v.toString());
            if (d == null) {
              throw new RuntimeException("Invalid date string:" + v);
            }
            v = d;
          }
        }
    }
    else
    {
      rowValue = new Object[this.columns.length];
    }
    DataRow[] newRows = new DataRow[this.rows.length + 1];
    System.arraycopy(this.rows, 0, newRows, 0, index);
    if (index < this.rows.length) {
      System.arraycopy(this.rows, index, newRows, index + 1, this.rows.length - index);
    }
    newRows[index] = new DataRow(this.columns, rowValue);
    this.rows = newRows;
    return this;
  }

  public DataTable deleteRow(int index) {
    if (index >= this.rows.length) {
      throw new RuntimeException(index + " is out of range,max is " + (this.rows.length - 1) + "!");
    }
    this.rows = ((DataRow[])ArrayUtils.remove(this.rows, index));
    return this;
  }

  public DataTable deleteRow(DataRow dr) {
    for (int i = 0; i < this.rows.length; i++) {
      if (dr == this.rows[i]) {
        deleteRow(i);
        return this;
      }
    }
    throw new RuntimeException("DataRow not in DataTable");
  }

  public DataRow get(int rowIndex) {
    if ((rowIndex >= this.rows.length) || (rowIndex < 0)) {
      throw new RuntimeException("Index out of range:" + rowIndex);
    }
    return this.rows[rowIndex];
  }

  public DataTable set(int rowIndex, int colIndex, Object value) {
    getDataRow(rowIndex).set(colIndex, value);
    return this;
  }

  public DataTable set(int rowIndex, String columnName, Object value) {
    getDataRow(rowIndex).set(columnName, value);
    return this;
  }

  public Object get(int rowIndex, int colIndex) {
    return getDataRow(rowIndex).get(colIndex);
  }

  public Object get(int rowIndex, String columnName) {
    return getDataRow(rowIndex).get(columnName);
  }

  public String getString(int rowIndex, int colIndex) {
    return getDataRow(rowIndex).getString(colIndex);
  }

  public String getString(int rowIndex, String columnName) {
    return getDataRow(rowIndex).getString(columnName);
  }

  public int getInt(int rowIndex, int colIndex) {
    return getDataRow(rowIndex).getInt(colIndex);
  }

  public int getInt(int rowIndex, String columnName) {
    return getDataRow(rowIndex).getInt(columnName);
  }

  public long getLong(int rowIndex, int colIndex) {
    return getDataRow(rowIndex).getLong(colIndex);
  }

  public long getLong(int rowIndex, String columnName) {
    return getDataRow(rowIndex).getLong(columnName);
  }

  public double getDouble(int rowIndex, int colIndex) {
    return getDataRow(rowIndex).getDouble(colIndex);
  }

  public double getDouble(int rowIndex, String columnName) {
    return getDataRow(rowIndex).getDouble(columnName);
  }

  public Date getDate(int rowIndex, int colIndex) {
    return getDataRow(rowIndex).getDate(colIndex);
  }

  public Date getDate(int rowIndex, String columnName) {
    return getDataRow(rowIndex).getDate(columnName);
  }

  public DataRow getDataRow(int rowIndex) {
    if ((rowIndex >= this.rows.length) || (rowIndex < 0)) {
      throw new RuntimeException("Index is out of range:" + rowIndex);
    }
    return this.rows[rowIndex];
  }

  public DataColumn getDataColumn(int columnIndex) {
    if ((columnIndex < 0) || (columnIndex >= this.columns.length)) {
      throw new RuntimeException("Index is out of range:" + columnIndex);
    }
    return this.columns[columnIndex];
  }

  public DataColumn getDataColumn(String columnName) {
    for (int i = 0; i < this.columns.length; i++) {
      if (this.columns[i].getColumnName().equalsIgnoreCase(columnName)) {
        return getDataColumn(i);
      }
    }
    return null;
  }

  public Object[] getColumnValues(int columnIndex) {
    if ((columnIndex < 0) || (columnIndex >= this.columns.length)) {
      throw new RuntimeException("Index is out of range:" + columnIndex);
    }
    Object[] arr = new Object[getRowCount()];
    for (int i = 0; i < arr.length; i++) {
      arr[i] = this.rows[i].values[columnIndex];
    }
    return arr;
  }

  public Object[] getColumnValues(String columnName) {
    for (int i = 0; i < this.columns.length; i++) {
      if (this.columns[i].getColumnName().equalsIgnoreCase(columnName)) {
        return getColumnValues(i);
      }
    }
    return null;
  }

  public void sort(Comparator<DataRow> c) {
    Arrays.sort(this.rows, c);
  }

  public void sort(String columnName) {
    sort(columnName, "desc", false);
  }

  public void sort(String columnName, String order) {
    sort(columnName, order, false);
  }

  public void sort(String columnName, String order, final boolean isNumber) {
    final String cn = columnName;
    final String od = order;
    sort(new Comparator() {
      public int compare(DataRow dr1, DataRow dr2) {
        Object v1 = dr1.get(cn);
        Object v2 = dr2.get(cn);
        if (((v1 instanceof Number)) && ((v2 instanceof Number))) {
          double d1 = ((Number)v1).doubleValue();
          double d2 = ((Number)v2).doubleValue();
          if (d1 == d2)
            return 0;
          if (d1 > d2) {
            return "asc".equalsIgnoreCase(od) ? 1 : -1;
          }
          return "asc".equalsIgnoreCase(od) ? -1 : 1;
        }
        if (((v1 instanceof Date)) && ((v2 instanceof Date))) {
          Date d1 = (Date)v1;
          Date d2 = (Date)v1;
          if ("asc".equalsIgnoreCase(od)) {
            return d1.compareTo(d2);
          }
          return -d1.compareTo(d2);
        }
        if (isNumber) {
          double d1 = 0.0D; double d2 = 0.0D;
          try {
            d1 = Double.parseDouble(String.valueOf(v1));
            d2 = Double.parseDouble(String.valueOf(v2));
          } catch (Exception localException) {
          }
          if (d1 == d2)
            return 0;
          if (d1 > d2) {
            return "asc".equalsIgnoreCase(od) ? -1 : 1;
          }
          return "asc".equalsIgnoreCase(od) ? 1 : -1;
        }

        int c = dr1.getString(cn).compareTo(dr2.getString(cn));
        if ("asc".equalsIgnoreCase(od)) {
          return c;
        }
        return -c;
      }
    });
  }

  public DataTable filter(Filter<DataRow> filter)
  {
    ArrayList valueList = new ArrayList();
    for (int i = 0; i < this.rows.length; i++) {
      if (filter.filter(this.rows[i])) {
        valueList.add(this.rows[i]);
      }
    }
    DataTable dt = new DataTable();
    dt.columns = this.columns;
    dt.rows = new DataRow[valueList.size()];
    valueList.toArray(dt.rows);
    dt.setWebMode(this.isWebMode);
    return dt;
  }

  public Object clone()
  {
    DataColumn[] dcs = new DataColumn[this.columns.length];
    for (int i = 0; i < this.columns.length; i++) {
      dcs[i] = ((DataColumn)this.columns[i].clone());
    }
    DataTable dt = new DataTable();
    dt.columns = dcs;
    dt.rows = new DataRow[this.rows.length];
    for (int i = 0; i < this.rows.length; i++) {
      dt.rows[i] = new DataRow(dcs, this.rows[i].values);
    }
    dt.setWebMode(this.isWebMode);
    return dt;
  }

  public Mapx<String, Object> toMapx(String keyColumnName, String valueColumnName)
  {
    if (StringUtil.isEmpty(keyColumnName)) {
      throw new RuntimeException("Key column name can't be empty");
    }
    if (StringUtil.isEmpty(valueColumnName)) {
      throw new RuntimeException("Value column name can't be empty");
    }
    int keyIndex = 0; int valueIndex = 0;
    boolean keyFlag = false; boolean valueFlag = false;
    for (int i = 0; i < this.columns.length; i++) {
      if (this.columns[i].getColumnName().equalsIgnoreCase(keyColumnName)) {
        keyIndex = i;
        keyFlag = true;
        if (valueFlag) {
          break;
        }
      }
      else if (this.columns[i].getColumnName().equalsIgnoreCase(valueColumnName)) {
        valueIndex = i;
        valueFlag = true;
        if (keyFlag)
        {
          break;
        }
      }
    }
    return toMapx(keyIndex, valueIndex);
  }

  public Mapx<String, Object> toMapx(int keyColumnIndex, int valueColumnIndex)
  {
    if ((keyColumnIndex < 0) || (keyColumnIndex >= this.columns.length)) {
      throw new RuntimeException("Key index is out of range:" + keyColumnIndex);
    }
    if ((valueColumnIndex < 0) || (valueColumnIndex >= this.columns.length)) {
      throw new RuntimeException("Value index is out of range:" + valueColumnIndex);
    }
    Mapx map = new Mapx();
    for (int i = 0; i < this.rows.length; i++) {
      Object key = this.rows[i].values[keyColumnIndex];
      if (key == null)
        map.put(null, this.rows[i].values[valueColumnIndex]);
      else {
        map.put(key.toString(), this.rows[i].values[valueColumnIndex]);
      }
    }
    return map;
  }

  public void decodeColumn(String colName, Map<?, ?> map)
  {
    for (int i = 0; i < this.columns.length; i++)
      if (this.columns[i].getColumnName().equalsIgnoreCase(colName)) {
        decodeColumn(i, map);
        return;
      }
  }

  public void decodeColumn(int colIndex, Map<?, ?> map)
  {
    String newName = this.columns[colIndex].ColumnName + "Name";
    insertColumn(newName);
    for (int i = 0; i < getRowCount(); i++) {
      String v = getString(i, colIndex);
      set(i, newName, map.get(v));
    }
  }

  public void union(DataTable anotherDT)
  {
    if (anotherDT.getRowCount() == 0) {
      return;
    }
    if (getRowCount() == 0) {
      this.rows = anotherDT.rows;
      return;
    }
    if (getColCount() != anotherDT.getColCount()) {
      throw new RuntimeException("This's column count is " + getColCount() + " ,but parameter's column column count is " + 
        anotherDT.getColCount());
    }
    int srcPos = this.rows.length;
    DataRow[] newRows = new DataRow[this.rows.length + anotherDT.getRowCount()];
    System.arraycopy(this.rows, 0, newRows, 0, srcPos);
    System.arraycopy(anotherDT.rows, 0, newRows, srcPos, anotherDT.getRowCount());
    this.rows = null;
    this.rows = newRows;
  }

  public DataTable getPagedDataTable(int pageSize, int pageIndex)
  {
    DataTable dt = new DataTable(this.columns, null);
    for (int i = pageIndex * pageSize; (i < (pageIndex + 1) * pageSize) && (i < this.rows.length); i++) {
      dt.insertRow(this.rows[i]);
    }
    return dt;
  }

  public int getRowCount() {
    return this.rows.length;
  }

  @Deprecated
  public int getColCount()
  {
    return this.columns.length;
  }

  public int getColumnCount() {
    return this.columns.length;
  }

  public DataColumn[] getDataColumns() {
    return this.columns;
  }

  public boolean isWebMode() {
    return this.isWebMode;
  }

  public DataTable setWebMode(boolean isWebMode) {
    this.isWebMode = isWebMode;
    for (int i = 0; i < this.rows.length; i++) {
      this.rows[i].setWebMode(isWebMode);
    }
    return this;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    String[] columnNames = new String[getColumnCount()];
    for (int i = 0; i < columnNames.length; i++) {
      if (i != 0) {
        sb.append("\t");
      }
      sb.append(this.columns[i].getColumnName());
    }
    sb.append("\n");
    for (int i = 0; i < getRowCount(); i++) {
      for (int j = 0; j < getColumnCount(); j++) {
        if (j != 0) {
          sb.append("\t");
        }
        sb.append(StringUtil.javaEncode(getString(i, j)));
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  public boolean containsColumn(String name) {
    return getDataColumn(name) != null;
  }

  public Iterator<DataRow> iterator() {
    final DataTable dt = this;
    return new Iterator() {
      private int i = 0;

      public boolean hasNext() {
        return dt.getRowCount() > this.i;
      }

      public DataRow next() {
        return dt.getDataRow(this.i++);
      }

      public void remove() {
        dt.deleteRow(this.i);
      }
    };
  }
}
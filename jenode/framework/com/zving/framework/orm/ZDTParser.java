package com.zving.framework.orm;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataColumn;
import com.zving.framework.data.DataTable;
import com.zving.framework.utility.BufferedRandomAccessFile;
import com.zving.framework.utility.DateUtil;
import com.zving.framework.utility.NumberUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.framework.utility.ZipUtil;
import java.io.IOException;
import java.util.ArrayList;

public class ZDTParser
{
  public static final String VERSION_1 = "1";
  public static final String VERSION_2 = "2";
  public static final String VERSION_CURRENT = "2";
  private String file;
  private String Version;
  private ArrayList<ZDTTableInfo> Tables = new ArrayList();

  public ZDTParser(String file) {
    this.file = file;
  }

  public void parse() {
    BufferedRandomAccessFile braf = null;
    try {
      braf = new BufferedRandomAccessFile(this.file, "r");

      byte[] bs = new byte[4];
      braf.read(bs);
      int len = NumberUtil.toInt(bs);
      bs = new byte[len];
      braf.read(bs);
      this.Version = new String(bs);
      if (!ObjectUtil.in(new Object[] { this.Version, "1", "2" })) {
        throw new RuntimeException("Unknown .zdt version:" + this.Version);
      }

      Mapx map = new Mapx();
      int currentPos = len + 4;
      String columns;
      while (braf.getFilePointer() != braf.length())
      {
        bs = new byte[4];
        braf.read(bs);
        currentPos += 4;

        len = NumberUtil.toInt(bs);
        bs = new byte[len];
        braf.read(bs);
        currentPos += len;
        String name = new String(bs);

        bs = new byte[4];
        braf.read(bs);
        currentPos += 4;

        len = NumberUtil.toInt(bs);
        bs = new byte[len];
        braf.read(bs);
        currentPos += len;
        columns = new String(bs);
        DAOColumn[] scs = parseColumns(columns);

        bs = new byte[4];
        braf.read(bs);
        currentPos += 4;

        len = NumberUtil.toInt(bs);
        bs = new byte[len];
        braf.read(bs);
        currentPos += len;
        String indexInfo = new String(bs);
        if ("_ZVING_NULL".equals(indexInfo)) {
          indexInfo = null;
        }

        bs = new byte[4];
        braf.read(bs);
        currentPos += 4;

        len = NumberUtil.toInt(bs);
        bs = new byte[len];
        braf.read(bs);
        bs = ZipUtil.unzip(bs);
        currentPos += len;

        if (!map.containsKey(name)) {
          ZDTTableInfo ti = new ZDTTableInfo();
          ti.Name = name;
          ti.Columns = scs;
          ti.IndexInfo = indexInfo;
          ti.StartPosition = (currentPos - len - 4);
          ti.Positions.add(Integer.valueOf(ti.StartPosition));
          map.put(name, ti);
          this.Tables.add(ti);
        } else {
          ZDTTableInfo ti = (ZDTTableInfo)map.get(name);
          ti.Positions.add(Integer.valueOf(currentPos - len - 4));
        }

      }

      for (ZDTTableInfo ti : this.Tables) {
        DataTable dt = readOneTable(ti, braf, ((Integer)ti.Positions.get(ti.Positions.size() - 1)).intValue());
        ti.RowCount = ((ti.Positions.size() - 1) * 500 + dt.getRowCount());
      }
    } catch (Exception e1) {
      e1.printStackTrace();

      if (braf != null)
        try {
          braf.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
    finally
    {
      if (braf != null)
        try {
          braf.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
  }

  public DataTable getDataTable(ZDTTableInfo ti, int start, int end)
  {
    BufferedRandomAccessFile braf = null;
    try {
      end--;
      int startIndex = start / 500;
      int endIndex = end / 500;
      if (endIndex >= ti.Positions.size()) {
        endIndex = ti.Positions.size() - 1;
      }
      if (start / 500 > ti.Positions.size()) {
        throw new RuntimeException("Invalid start position：" + start);
      }
      braf = new BufferedRandomAccessFile(this.file, "r");
      DataTable dt = null;
      for (int i = startIndex; i <= endIndex; i++) {
        DataTable dt2 = readOneTable(ti, braf, ((Integer)ti.Positions.get(i)).intValue());
        if (dt == null) {
          dt = new DataTable(dt2.getDataColumns(), null);
        }
        int rowStart = 0;
        if (i == startIndex) {
          rowStart = start % 500;
        }
        for (int j = rowStart; (dt.getRowCount() < end + 1 - start) && (j < dt2.getRowCount()); j++) {
          dt.insertRow(dt2.getDataRow(j));
        }
      }
      return dt;
    } catch (Exception e1) {
      e1.printStackTrace();
    } finally {
      if (braf != null) {
        try {
          braf.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  private static DataTable readOneTable(ZDTTableInfo ti, BufferedRandomAccessFile braf, int pos) throws Exception {
    braf.seek(pos);
    byte[] bs = new byte[4];
    braf.read(bs);
    int len = NumberUtil.toInt(bs);
    bs = new byte[len];
    braf.read(bs);
    bs = ZipUtil.unzip(bs);
    String data = new String(bs, "UTF-8");
    if (ObjectUtil.empty(data)) {
      return new DataTable();
    }
    return parseDataTable(ti, data);
  }

  protected DAOColumn[] parseColumns(String columns) {
    String[] arr = StringUtil.splitEx(columns, "\n");
    ArrayList list = new ArrayList();
    for (String str : arr)
      if (!StringUtil.isEmpty(str))
      {
        String[] arr2 = StringUtil.splitEx(str, "\t");
        String name = arr2[0];
        if ("1".equals(this.Version)) {
          int type = Integer.parseInt(arr2[1]);
          int length = Integer.parseInt(arr2[3]);
          int precision = Integer.parseInt(arr2[4]);
          boolean mandatory = "true".equals(arr2[5]);
          boolean pk = "true".equals(arr2[6]);
          DAOColumn sc = new DAOColumn(name, type, length, precision, mandatory, pk);
          list.add(sc);
        }
        if ("2".equals(this.Version)) {
          int type = Integer.parseInt(arr2[1]);
          int length = Integer.parseInt(arr2[2]);
          int precision = Integer.parseInt(arr2[3]);
          boolean mandatory = "true".equals(arr2[4]);
          boolean pk = "true".equals(arr2[5]);
          DAOColumn sc = new DAOColumn(name, type, length, precision, mandatory, pk);
          list.add(sc);
        }
      }
    DAOColumn[] scs = new DAOColumn[list.size()];
    return (DAOColumn[])list.toArray(scs);
  }

  protected static DataTable parseDataTable(ZDTTableInfo ti, String data) {
    DAOColumn[] scs = ti.Columns;
    DataTable dt = new DataTable();
    for (DAOColumn sc : scs) {
      dt.insertColumn(new DataColumn(sc.getColumnName(), sc.getColumnType()));
    }
    String[] arr = StringUtil.splitEx(data, "\n");
    for (String str : arr) {
      String[] arr2 = StringUtil.splitEx(str, "\t");
      Object[] vs = new Object[scs.length];
      int i = 0;
      for (String v : arr2) {
        if (ObjectUtil.empty(v))
          vs[i] = null;
        else if (ObjectUtil.in(new Object[] { Integer.valueOf(scs[i].getColumnType()), Integer.valueOf(1), Integer.valueOf(10) })) {
          if (v.equals("null"))
            vs[i] = null;
          else
            vs[i] = StringUtil.javaDecode(v.substring(1, v.length() - 1));
        }
        else if (ObjectUtil.in(new Object[] { Integer.valueOf(scs[i].getColumnType()), Integer.valueOf(8), Integer.valueOf(9) }))
          vs[i] = Integer.valueOf(Integer.parseInt(v));
        else if (scs[i].getColumnType() == 7) {
          if ("\"\"".equals(v))
            vs[i] = new Long(0L);
          else
            try {
              vs[i] = Long.valueOf(Long.parseLong(v));
            } catch (Exception e) {
              e.printStackTrace();
            }
        }
        else if (scs[i].getColumnType() == 5)
          vs[i] = Float.valueOf(Float.parseFloat(v));
        else if (ObjectUtil.in(new Object[] { Integer.valueOf(scs[i].getColumnType()), Integer.valueOf(6), Integer.valueOf(4) }))
          vs[i] = Double.valueOf(Double.parseDouble(v));
        else if (scs[i].getColumnType() == 12)
          vs[i] = DateUtil.parseDateTime(v);
        else if (scs[i].getColumnType() == 2) {
          vs[i] = StringUtil.base64Decode(StringUtil.javaDecode(v));
        }
        i++;
      }
      dt.insertRow(vs);
    }
    return dt;
  }

  public String getFile() {
    return this.file;
  }

  public String getVersion() {
    return this.Version;
  }

  public ArrayList<ZDTTableInfo> getTables() {
    return this.Tables;
  }

  public static class ZDTTableInfo
  {
    public String Name;
    public DAOColumn[] Columns;
    public String IndexInfo;
    public int RowCount;
    public int StartPosition;
    public ArrayList<Integer> Positions = new ArrayList();
  }
}
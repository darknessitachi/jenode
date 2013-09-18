package com.zving.framework.orm;

import com.zving.framework.Config;
import com.zving.framework.core.asm.objectweb.tree.ClassNode;
import com.zving.framework.core.scanner.BuiltResource;
import com.zving.framework.core.scanner.BuiltResourceScanner;
import com.zving.framework.core.scanner.IBuiltResourceVisitor;
import com.zving.framework.data.DBConn;
import com.zving.framework.data.DBConnConfig;
import com.zving.framework.data.DataAccess;
import com.zving.framework.data.dbtype.DBTypeService;
import com.zving.framework.data.dbtype.IDBType;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DAOUtil
{
  private static long BackupNoBase = System.currentTimeMillis();

  public static boolean deleteByCondition(DAO<?> conditionDAO)
  {
    DataAccess da = new DataAccess();
    try {
      return deleteByCondition(conditionDAO, da);
    } finally {
      da.close();
    }
  }

  public static boolean deleteByCondition(DAO<?> conditionDAO, DataAccess da)
  {
    DAOMetadata meta = conditionDAO.metadata();
    DAOColumn[] columns = meta.getColumns();
    boolean firstFlag = true;
    StringBuilder sb = new StringBuilder(128);
    sb.append("delete from ");
    sb.append(meta.getTable());
    for (int i = 0; i < columns.length; i++) {
      DAOColumn sc = columns[i];
      if (!conditionDAO.isNull(sc)) {
        if (firstFlag) {
          sb.append(" where ");
          sb.append(sc.getColumnName());
          sb.append("=?");
          firstFlag = false;
        } else {
          sb.append(" and ");
          sb.append(sc.getColumnName());
          sb.append("=?");
        }
      }
    }
    Connection conn = da.getConnection();
    PreparedStatement pstmt = null;
    try {
      pstmt = conn.prepareStatement(sb.toString(), 1003, 1007);
      int i = 0; for (int j = 0; i < columns.length; i++) {
        DAOColumn sc = columns[i];
        Object v = conditionDAO.getV(i);
        if (v != null) {
          if (sc.getColumnType() == 12)
            pstmt.setDate(j + 1, new java.sql.Date(((java.util.Date)v).getTime()));
          else {
            pstmt.setObject(j + 1, v);
          }
          j++;
        }
      }
      pstmt.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    } finally {
      if (pstmt != null) {
        try {
          pstmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
        pstmt = null;
      }
    }
    return true;
  }

  public static <T extends DAO<T>> T getDAOFromBackupDAO(BackupDAO<T> bDAO)
  {
    try
    {
      Class c = bDAO.getDAO().getClass();
      DAO dao = (DAO)c.newInstance();
      for (int i = 0; i < dao.columns().length; i++) {
        dao.setV(i, bDAO.getV(i));
      }
      return dao;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static <T extends DAO<T>> DAOSet<T> getDAOSetFromBackupDAOSet(DAOSet<BackupDAO<T>> bset)
  {
    if (bset.size() == 0)
      return new DAOSet();
    try
    {
      bset.sort("BackupNo", "asc");

      ArrayList list = new ArrayList();
      DAOColumn[] columns = ((BackupDAO)bset.get(0)).columns();
      for (int i = 0; i < columns.length; i++) {
        if ((columns[i].isPrimaryKey()) && (!columns[i].getColumnName().equalsIgnoreCase("BackupNo"))) {
          list.add(Integer.valueOf(i));
        }
      }
      int[] keys = new int[list.size()];
      for (int i = 0; i < list.size(); i++) {
        keys[i] = ((Integer)list.get(i)).intValue();
      }
      for (int i = 0; i < bset.size(); i++)
      {
        Object[] ks = new Object[keys.length];
        for (int j = 0; j < ks.length; j++) {
          ks[j] = ((BackupDAO)bset.get(i)).getV(j);
        }
        for (int j = i + 1; j < bset.size(); ) {
          boolean flag = true;
          for (int k = 0; k < keys.length; k++) {
            if (!((BackupDAO)bset.get(j)).getV(keys[k]).equals(ks[k])) {
              flag = false;
              break;
            }
          }
          if (flag)
            bset.remove((BackupDAO)bset.get(j));
          else {
            j++;
          }
        }
      }
      DAOSet set = new DAOSet();
      Class daoClass = ((BackupDAO)bset.get(0)).getDAO().getClass();
      for (int j = 0; j < bset.size(); j++) {
        DAO dao = (DAO)daoClass.newInstance();
        BackupDAO bDAO = (BackupDAO)bset.get(j);
        for (int i = 0; i < dao.columns().length; i++) {
          dao.setV(i, bDAO.getV(i));
        }
        set.add(dao);
      }
      return set;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static boolean copyFieldValue(DAO<?> srcDAO, DAO<?> destDAO)
  {
    try
    {
      DAOColumn[] srcSC = srcDAO.columns();
      DAOColumn[] destSC = destDAO.columns();
      for (int i = 0; i < srcSC.length; i++)
        for (int j = 0; j < destSC.length; j++)
          if (srcSC[i].getColumnName().equals(destSC[j].getColumnName())) {
            int order = j;
            Object v = srcDAO.getV(i);
            if (v == null) {
              destDAO.setV(order, null); break;
            }if ((v instanceof java.util.Date)) {
              destDAO.setV(order, ((java.util.Date)v).clone()); break;
            }if ((v instanceof Double)) {
              destDAO.setV(order, new Double(((Double)v).doubleValue())); break;
            }if ((v instanceof Float)) {
              destDAO.setV(order, new Float(((Float)v).floatValue())); break;
            }if ((v instanceof Integer)) {
              destDAO.setV(order, new Integer(((Integer)v).intValue())); break;
            }if ((v instanceof Long)) {
              destDAO.setV(order, new Long(((Long)v).longValue())); break;
            }if ((v instanceof byte[])) {
              destDAO.setV(order, ((byte[])v).clone()); break;
            }if (!(v instanceof String)) break;
            destDAO.setV(order, v);

            break;
          }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return true;
  }

  public static synchronized String getBackupNo()
  {
    return String.valueOf(BackupNoBase++).substring(1);
  }

  public static void setParam(DAOColumn sc, PreparedStatement pstmt, DBConn conn, int i, Object v)
    throws SQLException
  {
    IDBType db = (IDBType)DBTypeService.getInstance().get(conn.getDBConfig().DBType);
    if (v == null) {
      if (sc.getColumnType() == 7)
        pstmt.setNull(i + 1, -5);
      else if (sc.getColumnType() == 8)
        pstmt.setNull(i + 1, 4);
      else if (sc.getColumnType() == 10) {
        if (conn.getDBConfig().isSybase())
          db.setClob(conn, pstmt, i + 1, "");
        else
          pstmt.setNull(i + 1, 2005);
      }
      else if (sc.getColumnType() == 6)
        pstmt.setNull(i + 1, 8);
      else if (sc.getColumnType() == 5)
        pstmt.setNull(i + 1, 6);
      else if (sc.getColumnType() == 4)
        pstmt.setNull(i + 1, 3);
      else if (sc.getColumnType() == 12)
        pstmt.setNull(i + 1, 91);
      else if (sc.getColumnType() == 11)
        pstmt.setNull(i + 1, -7);
      else if (sc.getColumnType() == 9)
        pstmt.setNull(i + 1, 5);
      else {
        pstmt.setNull(i + 1, 12);
      }
    }
    else if (sc.getColumnType() == 12) {
      pstmt.setTimestamp(i + 1, new Timestamp(((java.util.Date)v).getTime()));
    } else if (sc.getColumnType() == 10) {
      String str = (String)v;
      if ((conn.getDBConfig().isLatin1Charset) && (conn.getDBConfig().isOracle())) {
        try {
          str = new String(str.getBytes(Config.getGlobalCharset()), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      }
      db.setClob(conn, pstmt, i + 1, str);
    } else if (sc.getColumnType() == 2) {
      db.setBlob(conn, pstmt, i + 1, (byte[])v);
    } else if (sc.getColumnType() == 1) {
      String str = (String)v;
      int len = StringUtil.lengthEx(str, Config.getGlobalCharset().equals("UTF-8"));
      if (len > sc.getLength()) {
        throw new DAOException("Data is too long, max is " + sc.getLength() + ",actual is " + len + ":" + sc.getColumnName());
      }
      if ((conn.getDBConfig().isLatin1Charset) && (conn.getDBConfig().isOracle())) {
        try {
          str = new String(str.getBytes(Config.getGlobalCharset()), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      }
      if ((conn.getDBConfig().isSybase()) && (str.equals("")))
        pstmt.setNull(i + 1, 12);
      else
        pstmt.setString(i + 1, str);
    }
    else {
      pstmt.setObject(i + 1, v);
    }
  }

  public static String[] getAllDAOClassName()
  {
    DAOClassVisitor v = new DAOClassVisitor();
    BuiltResourceScanner.scanAll(ObjectUtil.toList(new DAOClassVisitor[] { 
      v }), null);
    List list = v.getResult();
    String[] arr = new String[list.size()];
    arr = (String[])list.toArray(arr);
    return arr;
  }

  public static DAO findDAO(String tableName)
  {
    boolean bFlag = false;
    if (tableName.toUpperCase().startsWith("B")) {
      tableName = tableName.substring(1);
      bFlag = true;
    }
    String[] arr = getAllDAOClassName();
    for (int i = 0; i < arr.length; i++) {
      String name = arr[i].toLowerCase();
      if (name.endsWith("." + tableName.toLowerCase() + "dao")) {
        try {
          if (bFlag) {
            return new BackupDAO(Class.forName(arr[i]));
          }
          return (DAO)Class.forName(arr[i]).newInstance();
        }
        catch (InstantiationException e)
        {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  public static DAOColumn findColumn(String tableName, String columnName)
  {
    DAO dao = findDAO(tableName);
    return findColumn(dao.columns(), columnName);
  }

  public static DAOColumn findColumn(DAOColumn[] scs, String columnName)
  {
    for (int i = 0; i < scs.length; i++) {
      if (scs[i].getColumnName().equalsIgnoreCase(columnName)) {
        return scs[i];
      }
    }
    return null;
  }

  public static ArrayList<String> getPrimaryKeyColumns(DAOColumn[] scs)
  {
    ArrayList list = new ArrayList();
    for (int i = 0; i < scs.length; i++) {
      if (scs[i].isPrimaryKey()) {
        list.add(scs[i].getColumnName());
      }
    }
    return list;
  }

  public static String getTableCode(DAO<?> dao)
  {
    return dao.table();
  }

  public static DAOColumn[] getColumns(DAO<?> dao) {
    return dao.columns();
  }

  public static DAOColumn[] addBackupColumn(DAOColumn[] scs) {
    DAOColumn[] bscs = new DAOColumn[scs.length + 4];
    for (int i = 0; i < scs.length; i++) {
      bscs[i] = scs[i];
    }
    bscs[scs.length] = new DAOColumn("BackupNo", 1, 15, 0, true, true);
    bscs[(scs.length + 1)] = new DAOColumn("BackupOperator", 1, 50, 0, true, false);
    bscs[(scs.length + 2)] = new DAOColumn("BackupTime", 12, 0, 0, true, false);
    bscs[(scs.length + 3)] = new DAOColumn("BackupMemo", 1, 200, 0, false, false);
    return bscs;
  }

  public static class DAOClassVisitor implements IBuiltResourceVisitor {
    private static final String SUPER = DAO.class.getName().replace('.', '/');
    private static final String FRAMEWORK = "com/zving/framework";
    ArrayList<String> list = new ArrayList();

    public String getID() {
      return "DAOClasVisitor";
    }

    public String getName() {
      return "DAOClasVisitor";
    }

    public boolean match(BuiltResource br) {
      if (br.getFullName().indexOf("com/zving/framework") >= 0) {
        return false;
      }
      return true;
    }

    public void visitClass(BuiltResource br, ClassNode cn) {
      if ((cn.superName.equals(SUPER)) && ((cn.access & 0x400) != 1))
        this.list.add(cn.name.replace('/', '.'));
    }

    public void visitInnerClass(BuiltResource br, ClassNode outerClass, ClassNode innerClass)
    {
    }

    public void visitResource(BuiltResource br) {
    }

    public List<String> getResult() {
      return this.list;
    }
  }

  public static class DAOMetadataVisitor implements IBuiltResourceVisitor
  {
    private static final String SUPER = DAO.class.getName().replace('.', '/');
    private static final String FRAMEWORK = "com/zving/framework";
    ArrayList<DAOMetadata> list = new ArrayList();

    public String getID() {
      return "DAOMetadataVisitor";
    }

    public String getName() {
      return "DAOMetadataVisitor";
    }

    public boolean match(BuiltResource br) {
      if (br.getFullName().indexOf("com/zving/framework") >= 0) {
        return false;
      }
      return true;
    }

    public void visitClass(BuiltResource br, ClassNode cn) {
      if ((cn.superName.equals(SUPER)) && ((cn.access & 0x400) != 1))
        this.list.add(new DAOMetadata(cn));
    }

    public void visitInnerClass(BuiltResource br, ClassNode outerClass, ClassNode innerClass)
    {
    }

    public void visitResource(BuiltResource br) {
    }

    public List<DAOMetadata> getResult() {
      return this.list;
    }
  }
}
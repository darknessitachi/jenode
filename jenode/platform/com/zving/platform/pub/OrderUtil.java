package com.zving.platform.pub;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.Q;
import com.zving.framework.data.Transaction;
import com.zving.framework.orm.DAO;
import com.zving.framework.utility.StringUtil;
import com.zving.framework.utility.commons.ArrayUtils;
import java.util.Arrays;
import java.util.Date;

public class OrderUtil
{
  public static final String BEFORE = "Before";
  public static final String AFTER = "After";
  private static long currentOrder = System.currentTimeMillis();

  public static boolean updateOrder(String table, String type, String targetOrder, String orders, String wherePart)
  {
    return updateOrder(table, "OrderFlag", type, targetOrder, orders, wherePart);
  }

  public static void updateOrder(String table, String column, long targetOrderFlag, String wherePart, DAO<?> sortDAO, Transaction tran)
  {
    Mapx mapx = sortDAO.toMapx();
    long orderFlag = mapx.getLong(column);
    long newOrderFlag = targetOrderFlag;
    if (orderFlag > targetOrderFlag) {
      Q qb1 = new Q("update " + table + " set " + column + "=" + column + "+1 where " + column + ">=? and " + column + "<? " + 
        wherePart, new Object[] { Long.valueOf(targetOrderFlag), Long.valueOf(orderFlag) });
      tran.add(qb1);
    } else {
      Q qb1 = new Q("update " + table + " set " + column + "=" + column + "-1 where " + column + ">? and " + column + "<=? " + 
        wherePart, new Object[] { Long.valueOf(orderFlag), Long.valueOf(targetOrderFlag) });
      tran.add(qb1);
    }
    mapx.put(column, Long.valueOf(newOrderFlag));
    sortDAO.setValue(mapx);
    tran.update(sortDAO);
  }

  public static void updateOrder(String table, String column, long targetOrderFlag, DAO<?> sortDAO, Transaction tran) {
    updateOrder(table, column, targetOrderFlag, "", sortDAO, tran);
  }

  public static void updateOrder(String table, long targetOrderFlag, String wherePart, DAO<?> sortDAO, Transaction tran) {
    updateOrder(table, "OrderFlag", targetOrderFlag, wherePart, sortDAO, tran);
  }

  public static void updateOrder(String table, long targetOrderFlag, DAO<?> sortDAO, Transaction tran) {
    updateOrder(table, "OrderFlag", targetOrderFlag, "", sortDAO, tran);
  }

  public static boolean updateOrder(String table, String column, String type, String targetOrder, String orders, String wherePart) {
    return updateOrder(table, column, type, targetOrder, orders, wherePart, null);
  }

  public static boolean updateOrder(String table, String column, String type, String targetOrder, String orders, String wherePart, Transaction tran)
  {
    if ((StringUtil.isEmpty(targetOrder)) || (targetOrder.length() < 13)) {
      targetOrder = getDefaultOrder();
    }
    if (!StringUtil.checkID(targetOrder)) {
      return false;
    }
    if (!StringUtil.checkID(orders)) {
      return false;
    }
    if (StringUtil.isEmpty(wherePart)) {
      wherePart = "1=1";
    }

    String[] arrtmp = orders.split(",");
    arrtmp = (String[])ArrayUtils.removeElement(arrtmp, targetOrder);
    long[] arr = new long[arrtmp.length + 1];
    for (int i = 0; i < arrtmp.length; i++) {
      arr[i] = Long.parseLong(arrtmp[i]);
    }
    long target = Long.parseLong(targetOrder);
    arr[arrtmp.length] = target;
    Arrays.sort(arr);

    boolean bFlag = true;
    if (tran == null) {
      tran = new Transaction();
      bFlag = false;
    }
    Date modifyTime = new Date();
    Q qb = null;
    boolean flag = "After".equals(type);
    for (int i = 0; i < arr.length; i++) {
      if (arr[i] == target) {
        if (flag) {
          target = target + arr.length - i - 1L;
          int d = arr.length - 1;
          for (int j = 0; j < arr.length; j++) {
            if (j != i) {
              qb = new Q("update " + table + " set " + column + "=?, ModifyTime=? where " + column + "=?", new Object[] { Long.valueOf((target - d) * 10L) });
              d--;
            } else {
              qb = new Q("update " + table + " set " + column + "=?, ModifyTime=? where " + column + "=?", new Object[] { Long.valueOf(target * 10L) });
            }
            qb.add(new Object[] { modifyTime });
            qb.add(new Object[] { Long.valueOf(arr[j]) });
            tran.add(qb);
          }

          for (int j = 0; j < i; j++) {
            if (arr[j] + 1L != arr[(j + 1)])
            {
              qb = new Q("update " + table + " set " + column + "=" + column + "-?, ModifyTime=? where " + column + 
                " between ? and ? and " + wherePart, new Object[0]);

              qb.add(new Object[] { Integer.valueOf(j + 1) });
              qb.add(new Object[] { modifyTime });
              qb.add(new Object[] { Long.valueOf(arr[j]) });
              qb.add(new Object[] { Long.valueOf(arr[(j + 1)]) });
              tran.add(qb);
            }
          }

          for (int j = arr.length - 1; j > i; j--)
            if (arr[j] != arr[(j - 1)] + 1L)
            {
              qb = new Q("update " + table + " set " + column + "=" + column + "+?, ModifyTime=? where " + column + 
                " between ? and ? and " + wherePart, new Object[0]);

              qb.add(new Object[] { Integer.valueOf(arr.length - j) });
              qb.add(new Object[] { modifyTime });
              qb.add(new Object[] { Long.valueOf(arr[(j - 1)]) });
              qb.add(new Object[] { Long.valueOf(arr[j]) });
              tran.add(qb);
            }
        } else {
          target -= i;
          int d = 1;
          for (int j = 0; j < arr.length; j++) {
            if (j != i) {
              qb = new Q("update " + table + " set " + column + "=?, ModifyTime=? where " + column + "=?", new Object[] { Long.valueOf((target + d) * 10L) });
              d++;
            } else {
              qb = new Q("update " + table + " set " + column + "=?, ModifyTime=? where " + column + "=?", new Object[] { Long.valueOf(target * 10L) });
            }
            qb.add(new Object[] { modifyTime });
            qb.add(new Object[] { Long.valueOf(arr[j]) });
            tran.add(qb);
          }

          for (int j = 0; j < i; j++) {
            if (arr[j] + 1L != arr[(j + 1)])
            {
              qb = new Q("update " + table + " set " + column + "=" + column + "-?, ModifyTime=? where " + column + 
                " between ? and ? and " + wherePart, new Object[0]);

              qb.add(new Object[] { Integer.valueOf(j + 1) });
              qb.add(new Object[] { modifyTime });
              qb.add(new Object[] { Long.valueOf(arr[j]) });
              qb.add(new Object[] { Long.valueOf(arr[(j + 1)]) });
              tran.add(qb);
            }
          }

          for (int j = arr.length - 1; j > i; j--)
            if (arr[j] != arr[(j - 1)] + 1L)
            {
              qb = new Q("update " + table + " set " + column + "=" + column + "+?, ModifyTime=? where " + column + 
                " between ? and ? and " + wherePart, new Object[0]);

              qb.add(new Object[] { Integer.valueOf(arr.length - j) });
              qb.add(new Object[] { modifyTime });
              qb.add(new Object[] { Long.valueOf(arr[(j - 1)]) });
              qb.add(new Object[] { Long.valueOf(arr[j]) });
              tran.add(qb);
            }
        }
        qb = new Q("update " + table + " set " + column + "=" + column + "/10 where " + column + ">? and " + 
          wherePart, new Object[] { Long.valueOf(target * 9L) });
        tran.add(qb);
        if (bFlag) {
          return true;
        }
        return tran.commit();
      }
    }

    return false;
  }

  public static synchronized long getDefaultOrder()
  {
    if (System.currentTimeMillis() <= currentOrder) {
      return ++currentOrder;
    }
    return OrderUtil.currentOrder = System.currentTimeMillis();
  }
}
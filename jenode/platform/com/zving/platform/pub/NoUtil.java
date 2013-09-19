package com.zving.platform.pub;

import com.zving.framework.Config;
import com.zving.framework.data.DBConn;
import com.zving.framework.data.DBConnPool;
import com.zving.framework.data.DataAccess;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.data.exception.DatabaseException;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.utility.StringUtil;
import com.zving.schema.ZDMaxNo;

public class NoUtil {
	private static DAOSet<ZDMaxNo> MaxNoSet;
	private static Object mutex = new Object();

	public static String getMaxNo(String noType) {
		return getMaxNo(noType, "SN");
	}

	public static String getMaxNoLoal(String noType) {
		return getMaxNoLocal(noType, "SN");
	}

	public static long getMaxID(String noType, String subType) {
		return getMaxIDUseLock(noType, subType);
	}

	public static synchronized long getMaxIDUseLock(String noType,
			String subType) {
		DBConn conn = DBConnPool.getConnection("Default", false, false);
		DataAccess da = new DataAccess(conn);
		try {
			da.setAutoCommit(false);
			Q qb = new Q(
					"select NoMaxValue from ZDMaxNo where NoType=? and NoSubType=?",
					new Object[] { noType, subType });
			if (Config.isOracle()) {
				qb.append(" for update", new Object[0]);
			}
			Object maxValue = da.executeOneValue(qb);
			if (maxValue != null) {
				long t = Long.parseLong(maxValue.toString()) + 1L;
				qb = new Q(
						"update ZDMaxNo set NoMaxValue=? where NoType=? and NoSubType=?",
						new Object[] { Long.valueOf(t), noType });
				qb.add(new Object[] { subType });
				da.executeNoQuery(qb);
				da.commit();
				return t;
			}
			ZDMaxNo maxno = new ZDMaxNo();
			maxno.setNoType(noType);
			maxno.setNoSubType(subType);
			maxno.setNoMaxValue(1L);
			maxno.setLength(10L);
			maxno.setDataAccess(da);
			if (maxno.insert()) {
				da.commit();
				return 1L;
			}
			throw new RuntimeException("获取最大号时发生错误!");
		} catch (Exception e) {
			da.rollback();
			throw new RuntimeException("获取最大号时发生错误:" + e.getMessage());
		} finally {
			try {
				da.setAutoCommit(true);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
			da.close();
		}
	}

	public static String getMaxNo(String noType, int length) {
		long t = getMaxID(noType, "SN");
		String no = String.valueOf(t);
		if (no.length() > length) {
			return no.substring(0, length);
		}
		return StringUtil.leftPad(no, '0', length);
	}

	public static String getMaxNo(String noType, String prefix, int length) {
		long t = getMaxID(noType, prefix);
		String no = String.valueOf(t);
		if (no.length() > length) {
			return no.substring(0, length);
		}
		return prefix + StringUtil.leftPad(no, '0', length);
	}

	public static synchronized String getMaxNoUseLock(String noType,
			String subType) {
		DataAccess da = new DataAccess();
		try {
			da.setAutoCommit(false);
			Q qb = new Q(
					"select NoMaxValue,Length from ZDMaxNo where NoType=? and NoSubType=?",
					new Object[] { noType, subType });
			if (Config.isOracle())
				qb.append(" for update", new Object[0]);
			else if (Config.isDB2())
				qb.append(" for update with rs", new Object[0]);
			else if ((Config.isSQLServer()) || (Config.isSybase())) {
				qb.append(" with (xlock)", new Object[0]);
			}
			DataTable dt = qb.fetch();
			if (dt.getRowCount() > 0) {
				long t = Long.parseLong(dt.getString(0, "NoMaxValue")) + 1L;
				int length = Integer.parseInt(dt.getString(0, "Length"));
				String no = String.valueOf(t);
				if (length > 0) {
					no = StringUtil.leftPad(no, '0', length);
				}
				qb = new Q(
						"update ZDMaxNo set NoMaxValue=? where NoType=? and NoSubType=?",
						new Object[] { Long.valueOf(t), noType });
				qb.add(new Object[] { subType });
				da.executeNoQuery(qb);
				da.commit();
				return no;
			}
			ZDMaxNo maxno = new ZDMaxNo();
			maxno.setNoType(noType);
			maxno.setNoSubType(subType);
			maxno.setNoMaxValue(1L);
			maxno.setLength(10L);
			maxno.setDataAccess(da);
			if (maxno.insert()) {
				da.commit();
				return "0000000001";
			}
			throw new RuntimeException("获取最大号时发生错误!");
		} catch (Exception e) {
			da.rollback();
			throw new RuntimeException("获取最大号时发生错误:" + e.getMessage());
		} finally {
			try {
				da.setAutoCommit(true);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
			da.close();
		}
	}

	public static synchronized long getMaxIDLocal(String noType, String subType) {
		if (MaxNoSet == null) {
			init();
		}
		ZDMaxNo maxno = null;
		if (MaxNoSet != null) {
			for (int i = 0; i < MaxNoSet.size(); i++) {
				maxno = (ZDMaxNo) MaxNoSet.get(i);
				if ((maxno.getNoType().equals(noType))
						&& (maxno.getNoSubType().equals(subType)))
					synchronized (mutex) {
						maxno.setNoMaxValue(maxno.getNoMaxValue() + 1L);
						if (!maxno.update()) {
							throw new RuntimeException("生成最大号错误,MaxNoType="
									+ noType + ",MaxSubType=" + subType);
						}
						return maxno.getNoMaxValue();
					}
			}
		} else {
			synchronized (mutex) {
				MaxNoSet = new DAOSet();
				maxno = new ZDMaxNo();
				maxno.setNoType(noType);
				maxno.setNoSubType(subType);
				maxno.setLength(0L);
				maxno.setNoMaxValue(1L);
				maxno.insert();
				MaxNoSet.add(maxno);
				return 1L;
			}
		}

		synchronized (mutex) {
			maxno = new ZDMaxNo();
			maxno.setNoType(noType);
			maxno.setNoSubType(subType);
			maxno.setLength(10L);
			maxno.setNoMaxValue(1L);
			maxno.insert();
			MaxNoSet.add(maxno);
			return 1L;
		}
	}

	public static long getMaxID(String noType) {
		return getMaxID(noType, "ID");
	}

	public static long getMaxIDLocal(String noType) {
		return getMaxIDLocal(noType, "ID");
	}

	public static String getMaxNo(String noType, String subType) {
		if (Config.isDebugMode()) {
			return getMaxNoUseLock(noType, subType);
		}
		return getMaxNoLocal(noType, subType);
	}

	public static synchronized String getMaxNoLocal(String noType,
			String subType) {
		if (MaxNoSet == null) {
			init();
		}
		ZDMaxNo maxno = null;
		if (MaxNoSet != null) {
			for (int i = 0; i < MaxNoSet.size(); i++) {
				maxno = (ZDMaxNo) MaxNoSet.get(i);
				if ((maxno.getNoType().equals(noType))
						&& (maxno.getNoSubType().equals(subType)))
					synchronized (mutex) {
						maxno.setNoMaxValue(maxno.getNoMaxValue() + 1L);
						if (!maxno.update()) {
							throw new RuntimeException("生成最大号错误,NoType="
									+ noType + ",MaxSubType=" + subType);
						}
						if (maxno.getLength() <= 0L) {
							return String.valueOf(maxno.getNoMaxValue());
						}
						return StringUtil.leftPad(
								String.valueOf(maxno.getNoMaxValue()), '0',
								(int) maxno.getLength());
					}
			}
		} else {
			synchronized (mutex) {
				MaxNoSet = new DAOSet();
				maxno = new ZDMaxNo();
				maxno.setNoType(noType);
				maxno.setNoSubType(subType);
				maxno.setLength(10L);
				maxno.setNoMaxValue(1L);
				maxno.insert();
				MaxNoSet.add(maxno);
				return "0000000001";
			}
		}

		synchronized (mutex) {
			maxno = new ZDMaxNo();
			maxno.setNoType(noType);
			maxno.setNoSubType(subType);
			maxno.setLength(10L);
			maxno.setNoMaxValue(1L);
			maxno.insert();
			MaxNoSet.add(maxno);
			return "0000000001";
		}
	}

	private static synchronized void init() {
		if (MaxNoSet != null) {
			return;
		}
		ZDMaxNo maxno = new ZDMaxNo();
		MaxNoSet = maxno.query();
	}
}
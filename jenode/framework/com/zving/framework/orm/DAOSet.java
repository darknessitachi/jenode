package com.zving.framework.orm;

import com.zving.framework.User;
import com.zving.framework.collection.Filter;
import com.zving.framework.data.DBConn;
import com.zving.framework.data.DBConnConfig;
import com.zving.framework.data.DataAccess;
import com.zving.framework.data.DataColumn;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.exception.DatabaseException;
import com.zving.framework.data.exception.DeleteException;
import com.zving.framework.data.exception.InsertException;
import com.zving.framework.data.exception.UpdateException;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import java.io.Serializable;
import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

public class DAOSet<T extends DAO<T>> implements Serializable, Cloneable,
		Iterable<T> {
	private static final long serialVersionUID = 1L;
	public ArrayList<T> elementData;
	protected int bConnFlag = 0;

	protected boolean bOperateFlag = false;
	protected int[] operateColumnOrders;
	protected String[] operateColumnNames;
	protected transient DataAccess da;
	protected static final int DefaultCapacity = 10;

	public DAOSet(int initialCapacity) {
		if (initialCapacity < 0) {
			throw new RuntimeException(
					"DAOSet's initialCapacity can't less than 0");
		}
		this.elementData = new ArrayList(initialCapacity);
	}

	public DAOSet() {
		this(10);
	}

	public void setDataAccess(DataAccess dAccess) {
		this.da = dAccess;
		this.bConnFlag = 1;
	}

	public boolean add(T s) {
		if (s == null) {
			return false;
		}
		this.elementData.add(s);
		return true;
	}

	public boolean add(DAOSet<T> aSet) {
		if (aSet == null) {
			return false;
		}
		int n = aSet.size();
		for (int i = 0; i < n; i++) {
			this.elementData.add(aSet.get(i));
		}
		return true;
	}

	public boolean addDAO(T dao) {
		return add(dao);
	}

	public boolean removeDAO(T dao) {
		return remove(dao);
	}

	public boolean remove(T t) {
		if (t == null) {
			return false;
		}
		return this.elementData.remove(t);
	}

	public void clear() {
		this.elementData.clear();
	}

	public boolean isEmpty() {
		return this.elementData.size() == 0;
	}

	public T get(int index) {
		return (T) this.elementData.get(index);
	}

	public boolean set(int index, T t) {
		this.elementData.set(index, t);
		return true;
	}

	public int size() {
		return this.elementData.size();
	}

	public boolean insert() {
		if (size() == 0) {
			return true;
		}
		DAOMetadata meta = get(0).metadata();
		if (this.bConnFlag == 0) {
			this.da = new DataAccess();
		}
		long start = System.currentTimeMillis();
		PreparedStatement pstmt = null;
		try {
			DBConn conn = this.da.getConnection();
			boolean autoComit = conn.getAutoCommit();
			if (this.bConnFlag == 0) {
				conn.setAutoCommit(false);
			}
			pstmt = conn.prepareStatement(meta.getInsertSQL(), 1003, 1007);
			for (DAO dao : this.elementData) {
				int i = 0;
				for (DAOColumn sc : meta.getColumns()) {
					if ((sc.isMandatory()) && (dao.getV(i) == null)) {
						LogUtil.warn(meta.getTable() + "'s mandatory column "
								+ sc.getColumnName() + " can't be null");
						return false;
					}
					Object v = dao.getV(i);
					DAOUtil.setParam(sc, pstmt, conn, i++, v);
				}
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			if (this.bConnFlag == 0) {
				conn.commit();
				conn.setAutoCommit(autoComit);
			}
			conn.setLastSuccessExecuteTime(System.currentTimeMillis());
			return true;
		} catch (Throwable e) {
			if ((e instanceof BatchUpdateException)) {
				LogUtil.warn(toDataTable());
			}
			String message = "Error:Set insert to " + meta.getTable()
					+ " failed:" + e.getMessage();
			LogUtil.warn(message);
			DataAccess.log(start, message, null);
			throw new InsertException(message);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pstmt = null;
			}
			DataAccess.log(start, meta.getInsertSQL(), null);
			if (this.bConnFlag == 0)
				this.da.close();
		}
	}

	public boolean update() {
		if (size() == 0) {
			return true;
		}
		DAOMetadata meta = get(0).metadata();
		String sql = null;
		if (this.bConnFlag == 0) {
			this.da = new DataAccess();
		}
		long start = System.currentTimeMillis();
		if (this.bOperateFlag) {
			dealOperateColumnNames();
			StringBuilder sb = new StringBuilder("update ");
			sb.append(meta.getTable());
			sb.append(" set ");
			for (int i = 0; i < this.operateColumnOrders.length; i++) {
				if (i != 0) {
					sb.append(",");
				}
				sb.append(meta.getColumns()[this.operateColumnOrders[i]]
						.getColumnName());
				sb.append("=?");
			}
			sb.append(meta.getPrimaryKeyConditions());
			sql = sb.toString();
		} else {
			StringBuilder sb = new StringBuilder("update ");
			sb.append(meta.getTable());
			sb.append(" set ");
			for (int i = 0; i < meta.getColumns().length; i++) {
				if (i != 0) {
					sb.append(",");
				}
				sb.append(meta.getColumns()[i].getColumnName());
				sb.append("=?");
			}
			sb.append(meta.getPrimaryKeyConditions());
			sql = sb.toString();
		}
		if (this.da.getConnection().getDBConfig().isSybase()) {
			sql = StringUtil.replaceAllIgnoreCase(sql, " Count=", "\"Count\"=");
			sql = StringUtil.replaceAllIgnoreCase(sql, " Scroll=",
					"\"Scroll\"=");
			sql = StringUtil
					.replaceAllIgnoreCase(sql, ",Count=", ",\"Count\"=");
			sql = StringUtil.replaceAllIgnoreCase(sql, ",Scroll=",
					",\"Scroll\"=");
		}
		PreparedStatement pstmt = null;
		try {
			DBConn conn = this.da.getConnection();
			boolean autoComit = conn.getAutoCommit();
			if (this.bConnFlag == 0) {
				conn.setAutoCommit(false);
			}
			pstmt = conn.prepareStatement(sql, 1003, 1007);
			for (DAO dao : this.elementData) {
				if (this.bOperateFlag)
					for (int i = 0; i < this.operateColumnOrders.length; i++) {
						Object v = dao.getV(this.operateColumnOrders[i]);
						DAOUtil.setParam(
								meta.getColumns()[this.operateColumnOrders[i]],
								pstmt, conn, i, v);
					}
				else {
					for (int i = 0; i < meta.getColumns().length; i++) {
						Object v = dao.getV(i);
						DAOUtil.setParam(meta.getColumns()[i], pstmt, conn, i,
								v);
					}
				}
				int i = 0;
				for (int j = 0; i < meta.getColumns().length; i++) {
					DAOColumn sc = meta.getColumns()[i];
					if (sc.isPrimaryKey()) {
						Object v = dao.getV(i);
						if (dao.oldValues != null) {
							v = dao.oldValues[i];
						}
						if (v == null) {
							LogUtil.warn(meta.getTable() + "'s primary column "
									+ sc.getColumnName() + " can't be null");
							return false;
						}
						if (this.bOperateFlag)
							DAOUtil.setParam(meta.getColumns()[i], pstmt, conn,
									j + this.operateColumnOrders.length, v);
						else {
							DAOUtil.setParam(meta.getColumns()[i], pstmt, conn,
									j + meta.getColumns().length, v);
						}

						j++;
					}
				}
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			if (this.bConnFlag == 0) {
				conn.commit();
				conn.setAutoCommit(autoComit);
			}
			conn.setLastSuccessExecuteTime(System.currentTimeMillis());
			return true;
		} catch (Throwable e) {
			String message = "Error:Set update to " + meta.getTable()
					+ " failed:" + e.getMessage();
			DataAccess.log(start, message, null);
			LogUtil.warn(message);
			throw new UpdateException(message);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pstmt = null;
			}
			DataAccess.log(start, sql, null);
			if (this.bConnFlag == 0)
				this.da.close();
		}
	}

	public boolean delete() {
		if (size() == 0) {
			return true;
		}
		DAOMetadata meta = get(0).metadata();
		if (this.bConnFlag == 0) {
			this.da = new DataAccess();
		}
		long start = System.currentTimeMillis();
		PreparedStatement pstmt = null;
		try {
			DBConn conn = this.da.getConnection();
			boolean autoComit = conn.getAutoCommit();
			if (this.bConnFlag == 0) {
				conn.setAutoCommit(false);
			}
			pstmt = conn.prepareStatement(meta.getDeleteSQL(), 1003, 1007);
			for (DAO dao : this.elementData) {
				int i = 0;
				for (int j = 0; i < meta.getColumns().length; i++) {
					DAOColumn sc = meta.getColumns()[i];
					if (sc.isPrimaryKey()) {
						Object v = dao.getV(i);
						if (dao.oldValues != null) {
							v = dao.oldValues[i];
						}
						if (v == null) {
							LogUtil.warn(meta.getTable() + "'s primary column "
									+ sc.getColumnName() + " can't be null");
							return false;
						}
						DAOUtil.setParam(meta.getColumns()[i], pstmt, conn, j,
								v);

						j++;
					}
				}
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			if (this.bConnFlag == 0) {
				conn.commit();
				conn.setAutoCommit(autoComit);
			}
			conn.setLastSuccessExecuteTime(System.currentTimeMillis());
			return true;
		} catch (Throwable e) {
			String message = "Error:Set delete from " + meta.getTable()
					+ " failed:" + e.getMessage();
			LogUtil.warn(message);
			DataAccess.log(start, message, null);
			throw new DeleteException(message);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pstmt = null;
			}
			DataAccess.log(start, meta.getDeleteSQL(), null);
			if (this.bConnFlag == 0)
				this.da.close();
		}
	}

	public boolean deleteAndInsert() {
		if (size() == 0) {
			return true;
		}
		if (this.bConnFlag == 1) {
			if (!delete()) {
				return false;
			}
			return insert();
		}
		this.da = new DataAccess();
		this.bConnFlag = 1;
		try {
			this.da.setAutoCommit(false);
			delete();
			insert();
			this.da.commit();
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			this.da.rollback();
			return false;
		} finally {
			try {
				this.da.setAutoCommit(true);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
			this.da.close();
			this.da = null;
			this.bConnFlag = 0;
		}
	}

	public boolean deleteAndBackup() {
		return deleteAndBackup(null, "Delete");
	}

	public boolean deleteAndBackup(String backupOperator, String backupMemo) {
		if (size() == 0) {
			return true;
		}
		if (ObjectUtil.empty(backupMemo))
			backupMemo = "Delete";
		try {
			backupOperator = ObjectUtil.empty(backupOperator) ? User
					.getUserName() : backupOperator;
			backupOperator = ObjectUtil.empty(backupOperator) ? "SYSTEM"
					: backupOperator;
			if (this.bConnFlag == 1) {
				if (!backup(backupOperator, backupMemo)) {
					return true;
				}
				delete();
				return true;
			}
			this.da = new DataAccess();
			this.bConnFlag = 1;
			try {
				this.da.setAutoCommit(false);
				backup(backupOperator, backupMemo);
				delete();
				this.da.commit();
				return true;
			} catch (Throwable e) {
				e.printStackTrace();
				this.da.rollback();
				return false;
			} finally {
				try {
					this.da.setAutoCommit(true);
				} catch (DatabaseException e) {
					e.printStackTrace();
				}
				this.da.close();
				this.da = null;
				this.bConnFlag = 0;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean backup() {
		return backup(null, null);
	}

	public boolean backup(String backupOperator, String backupMemo) {
		if (size() == 0) {
			return true;
		}
		DAOMetadata meta = get(0).metadata();
		backupOperator = StringUtil.isEmpty(backupOperator) ? User
				.getUserName() : backupOperator;
		backupOperator = StringUtil.isEmpty(backupOperator) ? "SYSTEM"
				: backupOperator;

		BackupDAO bDao = new BackupDAO(get(0).getClass());
		String backupSQL = bDao.getMetadata().getInsertSQL();
		Date current = new Date();
		if (this.bConnFlag == 0) {
			this.da = new DataAccess();
		}
		long start = System.currentTimeMillis();
		PreparedStatement pstmt = null;
		try {
			DBConn conn = this.da.getConnection();
			boolean autoComit = conn.getAutoCommit();
			if (this.bConnFlag == 0) {
				conn.setAutoCommit(false);
			}
			int i = meta.getColumns().length;
			DAOColumn sc1 = new DAOColumn("BackupNo", 1, 15, 0, true, true);
			DAOColumn sc2 = new DAOColumn("BackupOperator", 1, 200, 0, true,
					false);
			DAOColumn sc3 = new DAOColumn("BackupTime", 12, 0, 0, true, false);
			DAOColumn sc4 = new DAOColumn("BackupMemo", 1, 50, 0, false, false);
			pstmt = conn.prepareStatement(backupSQL, 1003, 1007);
			for (DAO dao : this.elementData) {
				int j = 0;
				for (DAOColumn sc : meta.getColumns()) {
					if ((sc.isMandatory()) && (dao.getV(j) == null)) {
						LogUtil.warn(meta.getTable() + "'s mandatory column "
								+ sc.getColumnName() + " can't be null");
						return false;
					}
					Object v = dao.getV(j);
					DAOUtil.setParam(sc, pstmt, conn, j++, v);
				}
				DAOUtil.setParam(sc1, pstmt, conn, i, DAOUtil.getBackupNo());
				DAOUtil.setParam(sc2, pstmt, conn, i + 1, backupOperator);
				DAOUtil.setParam(sc3, pstmt, conn, i + 2, current);
				DAOUtil.setParam(sc4, pstmt, conn, i + 3, backupMemo);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			if (this.bConnFlag == 0) {
				conn.commit();
				conn.setAutoCommit(autoComit);
			}
			conn.setLastSuccessExecuteTime(System.currentTimeMillis());

			return true;
		} catch (Throwable e) {
			if ((e instanceof BatchUpdateException)) {
				LogUtil.warn(toDataTable());
			}
			String message = "Error:Set backup from " + meta.getTable()
					+ " failed:" + e.getMessage();
			LogUtil.warn(message);
			DataAccess.log(start, message, null);

			throw new DatabaseException(message);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pstmt = null;
			}
			DataAccess.log(start, backupSQL, null);
			if (this.bConnFlag == 0)
				this.da.close();
		}
	}

	private void dealOperateColumnNames() {
		if (size() == 0) {
			return;
		}
		DAOMetadata meta = get(0).metadata();
		if (this.operateColumnNames != null) {
			this.operateColumnOrders = new int[this.operateColumnNames.length];
			int i = 0;
			for (int k = 0; i < this.operateColumnNames.length; i++) {
				boolean flag = false;
				for (int j = 0; j < meta.getColumns().length; j++) {
					if (this.operateColumnNames[i]
							.toString()
							.toLowerCase()
							.equals(meta.getColumns()[j].getColumnName()
									.toLowerCase())) {
						this.operateColumnOrders[k] = j;
						k++;
						flag = true;
						break;
					}
				}
				if (!flag) {
					throw new RuntimeException("Column not found:"
							+ this.operateColumnNames[i]);
				}
			}
			this.operateColumnNames = null;
		}
	}

	public void setOperateColumns(String[] colNames) {
		if ((colNames == null) || (colNames.length == 0)) {
			this.bOperateFlag = false;
			return;
		}
		this.operateColumnNames = colNames;
		this.bOperateFlag = true;
	}

	public DataTable toDataTable() {
		if (size() == 0) {
			return new DataTable();
		}
		DAOMetadata meta = get(0).metadata();
		if (this.bOperateFlag) {
			DataColumn[] dcs = new DataColumn[this.operateColumnOrders.length];
			Object[][] values = new Object[size()][meta.getColumns().length];
			for (int i = 0; i < this.operateColumnOrders.length; i++) {
				DataColumn dc = new DataColumn();
				dc.setColumnName(meta.getColumns()[this.operateColumnOrders[i]]
						.getColumnName());
				dc.setColumnType(meta.getColumns()[this.operateColumnOrders[i]]
						.getColumnType());
				dcs[i] = dc;
			}
			for (int i = 0; i < size(); i++) {
				for (int j = 0; j < this.operateColumnOrders.length; j++) {
					values[i][j] = ((DAO) this.elementData.get(i))
							.getV(this.operateColumnOrders[j]);
				}
			}
			DataTable dt = new DataTable(dcs, values);
			return dt;
		}
		DataColumn[] dcs = new DataColumn[meta.getColumns().length];
		Object[][] values = new Object[size()][meta.getColumns().length];
		for (int i = 0; i < meta.getColumns().length; i++) {
			DataColumn dc = new DataColumn();
			dc.setColumnName(meta.getColumns()[i].getColumnName());
			dc.setColumnType(meta.getColumns()[i].getColumnType());
			dcs[i] = dc;
		}
		for (int i = 0; i < size(); i++) {
			for (int j = 0; j < meta.getColumns().length; j++) {
				values[i][j] = ((DAO) this.elementData.get(i)).getV(j);
			}
		}
		DataTable dt = new DataTable(dcs, values);
		return dt;
	}

	public DAOSet<T> clone() {
		DAOSet set = newSet();
		for (int i = 0; i < size(); i++) {
			set.add(((DAO) this.elementData.get(i)).clone());
		}
		return set;
	}

	public void sort(Comparator<T> c) {
		this.elementData = ((ArrayList) ObjectUtil.sort(this.elementData, c));
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
		sort(new Comparator<T>() {
			public int compare(T obj1, T obj2) {
				DataRow dr1 = obj1.toDataRow();
				DataRow dr2 = obj2.toDataRow();
				Object v1 = dr1.get(cn);
				Object v2 = dr2.get(cn);
				if (((v1 instanceof Number)) && ((v2 instanceof Number))) {
					double d1 = ((Number) v1).doubleValue();
					double d2 = ((Number) v2).doubleValue();
					if (d1 == d2)
						return 0;
					if (d1 > d2) {
						return "asc".equalsIgnoreCase(od) ? 1 : -1;
					}
					return "asc".equalsIgnoreCase(od) ? -1 : 1;
				}
				if (((v1 instanceof Date)) && ((v2 instanceof Date))) {
					Date d1 = (Date) v1;
					Date d2 = (Date) v1;
					if ("asc".equalsIgnoreCase(od)) {
						return d1.compareTo(d2);
					}
					return -d1.compareTo(d2);
				}
				if (isNumber) {
					double d1 = 0.0D;
					double d2 = 0.0D;
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

	public DAOSet<T> filter(Filter<T> filter) {
		DAOSet set = newSet();
		for (T t : this.elementData) {
			if (filter.filter(t)) {
				set.add(t.clone());
			}
		}
		return this;
	}

	public DAOSet<T> newSet() {
		return new DAOSet();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (DAO t : this.elementData) {
			sb.append(t + "\n");
		}
		return sb.toString();
	}

	public Iterator<T> iterator() {
		return this.elementData.iterator();
	}
}
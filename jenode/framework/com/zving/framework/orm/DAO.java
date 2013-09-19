package com.zving.framework.orm;

import com.zving.framework.Config;
import com.zving.framework.User;
import com.zving.framework.collection.CaseIgnoreMapx;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DBConn;
import com.zving.framework.data.DBConnConfig;
import com.zving.framework.data.DBUtil;
import com.zving.framework.data.DataAccess;
import com.zving.framework.data.DataColumn;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.Q;
import com.zving.framework.data.QueryBuilder;
import com.zving.framework.data.QueryException;
import com.zving.framework.data.exception.DatabaseException;
import com.zving.framework.data.exception.DeleteException;
import com.zving.framework.data.exception.InsertException;
import com.zving.framework.data.exception.UpdateException;
import com.zving.framework.utility.DateUtil;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public abstract class DAO<T extends DAO<T>> {
	public static final String MEMO_DELETE = "Delete";
	protected boolean outerConnFlag = false;

	protected boolean operateColumnFlag = false;
	protected String[] operateColumns;
	protected int[] operateColumnOrders;
	protected Object[] oldValues;
	protected transient DataAccess da;
	protected transient DAOMetadata meta;

	protected void _init() {
		if (this.meta == null)
			this.meta = DAOMetadataManager.getMetadata(getClass());
	}

	public DAOMetadata metadata() {
		_init();
		return this.meta;
	}

	public DAOColumn[] columns() {
		_init();
		return this.meta.getColumns();
	}

	public String table() {
		_init();
		return this.meta.getTable();
	}

	public boolean insert() {
		if (!this.outerConnFlag) {
			this.da = new DataAccess();
		}
		_init();
		long start = System.currentTimeMillis();
		PreparedStatement pstmt = null;
		try {
			DBConn conn = this.da.getConnection();
			pstmt = conn.prepareStatement(this.meta.getInsertSQL(), 1003, 1007);
			ArrayList params = new ArrayList();
			int i = 0;
			for (DAOColumn sc : this.meta.getColumns()) {
				if ((sc.isMandatory())
						&& (this.meta.getV(this, sc.getColumnName()) == null)) {
					String message = "Error:" + this.meta.getTable()
							+ "'s column " + sc.getColumnName()
							+ " cann't be null";

					DataAccess.log(start, message, null);
					throw new SQLException(message);
				}

				Object v = this.meta.getV(this, sc.getColumnName());
				params.add(v);
				DAOUtil.setParam(sc, pstmt, conn, i++, v);
			}
			int count = pstmt.executeUpdate();
			conn.setLastSuccessExecuteTime(System.currentTimeMillis());
			if (count == 1)
				return true;
			return false;
		} catch (Exception e) {
			String message = "DAO insert to " + this.meta.getTable()
					+ " failed:" + e.getMessage();
			LogUtil.warn(message);
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
			DataAccess.log(start, this.meta.getInsertSQL(), null);
			if (!this.outerConnFlag)
				this.da.close();
		}
	}

	private boolean isChanged(int order) {
		if ((this.oldValues == null) || (this.oldValues.length <= order)) {
			return true;
		}
		Object v1 = getV(order);
		Object v2 = this.oldValues[order];
		if (v1 == null) {
			if (v2 == null) {
				return false;
			}
			return true;
		}

		return !v1.equals(v2);
	}

	public boolean update() {
		_init();
		if (!this.outerConnFlag) {
			this.da = new DataAccess();
		}
		long start = System.currentTimeMillis();
		DAOColumn[] columns = this.meta.getColumns();
		StringBuilder sb = new StringBuilder("update ");
		sb.append(this.meta.getTable());
		sb.append(" set ");
		boolean first = true;
		if (this.operateColumnFlag)
			for (int i = 0; i < this.operateColumnOrders.length; i++)
				if (isChanged(this.operateColumnOrders[i])) {
					if (!first)
						sb.append(",");
					else {
						first = false;
					}
					String columnName = columns[this.operateColumnOrders[i]]
							.getColumnName();
					sb.append(columnName);
					sb.append("=?");
				} else {
					for ( i = 0; i < columns.length; i++)
						if (isChanged(i)) {
							if (!first)
								sb.append(",");
							else {
								first = false;
							}
							String columnName = columns[i].getColumnName();
							sb.append(columnName);
							sb.append("=?");
						}
				}
		if (first) {
			return true;
		}
		sb.append(this.meta.getPrimaryKeyConditions());
		String sql = sb.toString();
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
			ArrayList params = new ArrayList();
			pstmt = conn.prepareStatement(sql, 1003, 1007);
			int j = 0;
			if (this.operateColumnFlag)
				for (int i = 0; i < this.operateColumnOrders.length; i++)
					if (isChanged(this.operateColumnOrders[i])) {
						Object v = getV(this.operateColumnOrders[i]);
						DAOUtil.setParam(columns[this.operateColumnOrders[i]],
								pstmt, conn, j++, v);
						params.add(v);
					} else
						for (i = 0; i < columns.length; i++)
							if (isChanged(i)) {
								Object v = getV(i);
								DAOUtil.setParam(columns[i], pstmt, conn, j++,
										v);
								params.add(v);
							}
			int i;
			for ( i = 0; i < columns.length; i++) {
				DAOColumn sc = columns[i];
				if (sc.isPrimaryKey()) {
					Object v = getV(i);
					if (this.oldValues != null) {
						v = this.oldValues[i];
					}
					if (v == null) {
						String message = this.meta.getTable()
								+ "'s primary key column " + sc.getColumnName()
								+ " can't be null";
						DataAccess.log(start, message, null);
						LogUtil.warn(message);
						return false;
					}
					if (this.operateColumnFlag)
						DAOUtil.setParam(columns[i], pstmt, conn, j++, v);
					else {
						DAOUtil.setParam(columns[i], pstmt, conn, j++, v);
					}
					params.add(v);
				}
			}

			pstmt.executeUpdate();
			conn.setLastSuccessExecuteTime(System.currentTimeMillis());
			return true;
		} catch (Exception e) {
			String message = "DAO update to " + this.meta.getTable()
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
			if (!this.outerConnFlag)
				this.da.close();
		}
	}

	public boolean delete() {
		_init();
		if (!this.outerConnFlag) {
			this.da = new DataAccess();
		}
		long start = System.currentTimeMillis();
		DAOColumn[] columns = this.meta.getColumns();
		PreparedStatement pstmt = null;
		ArrayList params = new ArrayList();
		try {
			DBConn conn = this.da.getConnection();
			pstmt = conn.prepareStatement(this.meta.getDeleteSQL(), 1003, 1007);
			int i = 0;
			for (int j = 0; i < columns.length; i++) {
				DAOColumn sc = columns[i];
				if (sc.isPrimaryKey()) {
					Object v = getV(i);
					if (this.oldValues != null) {
						v = this.oldValues[i];
					}
					if (v == null) {
						String message = "Error:" + this.meta.getTable()
								+ "'s primary key column " + sc.getColumnName()
								+ " can't be null";
						DataAccess.log(start, message, null);
						LogUtil.warn(message);
						return false;
					}
					DAOUtil.setParam(columns[i], pstmt, conn, j, v);
					params.add(v);

					j++;
				}
			}
			pstmt.executeUpdate();
			conn.setLastSuccessExecuteTime(System.currentTimeMillis());
			return true;
		} catch (Exception e) {
			String message = "DAO delete from " + this.meta.getTable()
					+ " failed:" + e.getMessage();
			DataAccess.log(start, message, null);
			LogUtil.warn(message);
			throw new DeleteException(e);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pstmt = null;
			}
			DataAccess.log(start, this.meta.getDeleteSQL(), params);
			if (!this.outerConnFlag)
				this.da.close();
		}
	}

	public String backup() {
		return backup(null, null);
	}

	public String backup(String backupOperator, String backupMemo) {
		_init();
		backupOperator = StringUtil.isEmpty(backupOperator) ? User
				.getUserName() : backupOperator;
		backupOperator = StringUtil.isEmpty(backupOperator) ? "SYSTEM"
				: backupOperator;

		BackupDAO bDao = new BackupDAO(getClass());
		String backupSQL = bDao.getMetadata().getInsertSQL();
		if (!this.outerConnFlag) {
			this.da = new DataAccess();
		}
		long start = System.currentTimeMillis();
		DAOColumn[] columns = this.meta.getColumns();
		PreparedStatement pstmt = null;
		try {
			DBConn conn = this.da.getConnection();
			pstmt = conn.prepareStatement(backupSQL, 1003, 1007);
			ArrayList params = new ArrayList();
			int i;
			for ( i = 0; i < columns.length; i++) {
				DAOColumn sc = columns[i];
				if ((sc.isMandatory()) && (getV(i) == null)) {
					String message = "Error:" + this.meta.getTable()
							+ "'s column " + sc.getColumnName()
							+ " can't be null";
					DataAccess.log(start, message, null);
					LogUtil.warn(message);
					throw new SQLException(message);
				}

				Object v = getV(i);
				DAOUtil.setParam(sc, pstmt, conn, i, v);
				params.add(v);
			}
			String backupNo = DAOUtil.getBackupNo();
			DAOColumn sc = new DAOColumn("BackupNo", 1, 15, 0, true, true);
			DAOUtil.setParam(sc, pstmt, conn, i, backupNo);
			params.add(backupNo);

			sc = new DAOColumn("BackupOperator", 1, 200, 0, true, false);
			DAOUtil.setParam(sc, pstmt, conn, i + 1, backupOperator);
			params.add(backupOperator);

			sc = new DAOColumn("BackupTime", 12, 0, 0, true, false);
			DAOUtil.setParam(sc, pstmt, conn, i + 2, new Date());
			params.add(new Date());

			sc = new DAOColumn("BackupMemo", 1, 50, 0, false, false);
			DAOUtil.setParam(sc, pstmt, conn, i + 3, backupMemo);
			params.add(backupMemo);

			int count = pstmt.executeUpdate();
			conn.setLastSuccessExecuteTime(System.currentTimeMillis());

			if (count == 1)
				return backupNo;
			return null;
		} catch (Exception e) {
			String message = "DAO backup from " + this.meta.getTable()
					+ " failed:" + e.getMessage();
			DataAccess.log(start, message, null);
			LogUtil.warn(message);
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
			if (!this.outerConnFlag)
				this.da.close();
		}
	}

	public boolean deleteAndInsert() {
		_init();
		if (this.outerConnFlag) {
			if (!delete()) {
				return false;
			}
			return insert();
		}
		this.da = new DataAccess();
		this.outerConnFlag = true;
		try {
			this.da.setAutoCommit(false);
			delete();
			insert();
			this.da.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			this.da.rollback();
			return false;
		} finally {
			this.da.setAutoCommit(true);
			this.da.close();
			this.da = null;
			this.outerConnFlag = false;
		}
	}

	public boolean deleteAndBackup() {
		return deleteAndBackup(null, "Delete");
	}

	public boolean deleteAndBackup(String backupOperator, String backupMemo) {
		_init();
		if (ObjectUtil.empty(backupMemo)) {
			backupMemo = "Delete";
		}
		backupOperator = ObjectUtil.empty(backupOperator) ? User.getUserName()
				: backupOperator;
		backupOperator = ObjectUtil.empty(backupOperator) ? "SYSTEM"
				: backupOperator;
		if (!this.outerConnFlag) {
			this.da = new DataAccess();
			this.outerConnFlag = true;
			try {
				this.da.setAutoCommit(false);
				backup(backupOperator, backupMemo);
				delete();
				this.da.commit();
				return true;
			} catch (DatabaseException e) {
				LogUtil.warn("DAO deleteAndBackup from " + this.meta.getTable()
						+ " failed:" + e.getMessage());
				e.printStackTrace();
				this.da.rollback();
				return false;
			} finally {
				this.outerConnFlag = false;
				try {
					this.da.setAutoCommit(true);
				} catch (DatabaseException e) {
					e.printStackTrace();
				}
				this.da.close();
			}
		}

		if (backup(backupOperator, backupMemo) == null) {
			return false;
		}
		return delete();
	}

	public boolean fill() {
		_init();

		String sql = this.meta.getFillAllSQL();
		DAOColumn[] columns = this.meta.getColumns();
		if (this.operateColumnFlag) {
			StringBuilder sb = new StringBuilder("select ");
			for (int i = 0; i < this.operateColumnOrders.length; i++) {
				int order = this.operateColumnOrders[i];
				if (i == 0) {
					sb.append(columns[order].getColumnName());
				} else {
					sb.append(",");
					sb.append(columns[order].getColumnName());
				}
			}
			sb.append(sql.substring(sql.indexOf(" from")));
			sql = sb.toString();
		}
		if (!this.outerConnFlag) {
			this.da = new DataAccess();
		}
		long start = System.currentTimeMillis();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		ArrayList params = new ArrayList();
		try {
			DBConn conn = this.da.getConnection();
			pstmt = conn.prepareStatement(sql, 1003, 1007);
			int i = 0;
			for (int j = 0; i < columns.length; i++) {
				DAOColumn sc = columns[i];
				if (sc.isPrimaryKey()) {
					Object v = getV(i);
					if (v == null) {
						String message = "Error:" + this.meta.getTable()
								+ "'s primary key column " + sc.getColumnName()
								+ " can't be null";
						DataAccess.log(start, message, null);
						throw new DAOException(message);
					}
					params.add(v);
					DAOUtil.setParam(columns[i], pstmt, conn, j, v);

					j++;
				}
			}
			rs = pstmt.executeQuery();
			conn.setLastSuccessExecuteTime(System.currentTimeMillis());

			if (rs.next()) {
				setVAll(conn, this, rs);
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			String message = "DAO fill from " + this.meta.getTable()
					+ " failed:" + e.getMessage();
			DataAccess.log(start, message, null);
			LogUtil.warn(message);
			throw new QueryException(message);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pstmt = null;
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				rs = null;
			}
			DataAccess.log(start, sql, params);
			if (!this.outerConnFlag)
				this.da.close();
		}
	}

	public DAOSet<T> query(QueryBuilder wherePart, int pageSize, int pageIndex) {
		return fetch(wherePart, pageSize, pageIndex);
	}

	public DAOSet<T> query(QueryBuilder wherePart) {
		return fetch(wherePart);
	}

	public DAOSet<T> query() {
		return fetch(null);
	}

	public DAOSet<T> query(int pageSize, int pageIndex) {
		return fetch(null, pageSize, pageIndex);
	}

	public DAOSet<T> fetch(QueryBuilder wherePart) {
		return fetch(wherePart, -1, -1);
	}

	public DAOSet<T> fetch(QueryBuilder wherePart, int pageSize, int pageIndex) {
		_init();

		long start = System.currentTimeMillis();
		DAOColumn[] columns = this.meta.getColumns();
		if ((wherePart != null)
				&& (!wherePart.getSQL().trim().toLowerCase()
						.startsWith("where"))) {
			String message = "Error:QueryBuilder SQL must starts with where";
			DataAccess.log(start, message, null);
			throw new DAOException(message);
		}

		if (!this.outerConnFlag) {
			this.da = new DataAccess();
		}
		DBConn conn = this.da.getConnection();
		StringBuilder sb = new StringBuilder("select ");
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		Q qb = new Q();
		String pageSQL = null;
		try {
			if (this.operateColumnFlag) {
				for (int i = 0; i < this.operateColumnOrders.length; i++) {
					if (i != 0) {
						sb.append(",");
					}
					sb.append(columns[this.operateColumnOrders[i]]
							.getColumnName());
				}
			} else if (conn.getDBConfig().isSQLServer()) {
				for (int i = 0; i < columns.length; i++) {
					if (i != 0) {
						sb.append(",");
					}
					sb.append(columns[i].getColumnName());
				}
			} else
				sb.append("*");

			sb.append(" from " + this.meta.getTable());
			if (wherePart == null) {
				boolean firstFlag = true;
				for (int i = 0; i < columns.length; i++) {
					DAOColumn sc = columns[i];
					if (!isNull(sc)) {
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
						Object v = getV(i);
						qb.add(new Object[] { v });
					}
				}
			} else {
				sb.append(" ");
				sb.append(wherePart.getSQL());
			}
			qb.setSQL(sb.toString());
			if (wherePart != null) {
				qb.getParams().addAll(wherePart.getParams());
			}

			pageSQL = qb.getSQL();
			if (pageSize > 0) {
				pageSQL = DataAccess.getPagedSQL(conn, qb, pageSize, pageIndex);
			}
			pageIndex = pageIndex < 0 ? 0 : pageIndex;
			pstmt = conn.prepareStatement(pageSQL, 1003, 1007);
			DataAccess.setParams(pstmt, qb, conn);
			rs = pstmt.executeQuery();

			conn.setLastSuccessExecuteTime(System.currentTimeMillis());
			DAOSet set = new DAOSet();
			while (rs.next()) {
				DAO dao = newInstance();
				setVAll(conn, dao, rs);
				set.addDAO(dao);
			}
			set.setOperateColumns(this.operateColumns);
			return set;
		} catch (Exception e) {
			String message = "DAO query from " + this.meta.getTable()
					+ " failed:" + e.getMessage();
			LogUtil.error(message);
			DataAccess.log(start, message, null);
			e.printStackTrace();
			return null;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pstmt = null;
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				rs = null;
			}
			DataAccess.log(start, qb.getSQL(), qb.getParams());
			if (!this.outerConnFlag) {
				conn = null;
				this.da.close();
			}
		}
	}

	private void setVAll(DBConn conn, DAO<?> dao, ResultSet rs)
			throws Exception {
		ArrayList oldValueList = new ArrayList(4);
		boolean latin1Flag = (conn.getDBConfig().isLatin1Charset)
				&& (conn.getDBConfig().isOracle());
		DAOColumn[] columns = this.meta.getColumns();
		if (this.operateColumnFlag) {
			for (int i = 0; i < this.operateColumnOrders.length; i++) {
				int order = this.operateColumnOrders[i];
				int index = rs.findColumn(columns[order].getColumnName());
				if (columns[order].getColumnType() == 10) {
					if ((conn.getDBConfig().isOracle())
							|| (conn.getDBConfig().isDB2())) {
						dao.setV(order, DBUtil.clobToString(rs.getClob(index)));
					} else if (conn.getDBConfig().isSybase()) {
						String str = rs.getString(index);
						if (" ".equals(str)) {
							str = "";
						}
						dao.setV(order, str);
					} else {
						dao.setV(order, rs.getObject(index));
					}
				} else if (columns[order].getColumnType() == 2)
					dao.setV(order, DBUtil.blobToBytes(rs.getBlob(index)));
				else {
					dao.setV(order, rs.getObject(index));
				}
				if ((columns[order].getColumnType() == 10)
						|| (columns[order].getColumnType() == 1)) {
					String str = (String) dao.getV(order);
					if ((latin1Flag) && (StringUtil.isNotEmpty(str))) {
						try {
							str = new String(str.getBytes("ISO-8859-1"),
									Config.getGlobalCharset());
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					dao.setV(order, str);
				}
				if (columns[order].isPrimaryKey())
					oldValueList.add(dao.getV(order));
			}
		} else {
			for (int i = 0; i < columns.length; i++) {
				int index = rs.findColumn(columns[i].getColumnName());
				if (columns[i].getColumnType() == 10) {
					if ((conn.getDBConfig().isOracle())
							|| (conn.getDBConfig().isDB2())) {
						dao.setV(i, DBUtil.clobToString(rs.getClob(index)));
					} else if (conn.getDBConfig().isSybase()) {
						String str = rs.getString(index);
						if (" ".equals(str)) {
							str = "";
						}
						dao.setV(i, str);
					} else {
						dao.setV(i, rs.getObject(index));
					}
				} else if (columns[i].getColumnType() == 2)
					dao.setV(i, DBUtil.blobToBytes(rs.getBlob(index)));
				else {
					dao.setV(i, rs.getObject(index));
				}
				if ((columns[i].getColumnType() == 10)
						|| (columns[i].getColumnType() == 1)) {
					String str = (String) dao.getV(i);
					if ((latin1Flag) && (StringUtil.isNotEmpty(str))) {
						try {
							str = new String(str.getBytes("ISO-8859-1"),
									Config.getGlobalCharset());
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					dao.setV(i, str);
				}
				oldValueList.add(dao.getV(i));
			}
		}
		dao.oldValues = oldValueList.toArray();
	}

	public void setOperateColumns(String[] colNames) {
		_init();
		if ((colNames == null) || (colNames.length == 0)) {
			this.operateColumnFlag = false;
			return;
		}
		DAOColumn[] columns = this.meta.getColumns();
		this.operateColumns = colNames;
		this.operateColumnOrders = new int[colNames.length];
		int i = 0;
		for (int k = 0; i < colNames.length; i++) {
			boolean flag = false;
			for (int j = 0; j < columns.length; j++) {
				if (colNames[i].toString().equalsIgnoreCase(
						columns[j].getColumnName())) {
					this.operateColumnOrders[k] = j;
					k++;
					flag = true;
					break;
				}
			}
			if (!flag) {
				throw new DAOException("Column not found in table "
						+ this.meta.getTable() + ":" + colNames[i]);
			}
		}
		this.operateColumnFlag = true;
	}

	public void setDataAccess(DataAccess dAccess) {
		this.da = dAccess;
		this.outerConnFlag = true;
	}

	protected boolean isNull(DAOColumn sc) {
		_init();
		Object obj = this.meta.getV(this, sc.getColumnName());
		if (obj == null) {
			return true;
		}
		if ((obj instanceof Integer))
			return ((Integer) obj).intValue() == 0;
		if ((obj instanceof Long))
			return ((Long) obj).longValue() == 0L;
		if ((obj instanceof Float))
			return ((Float) obj).floatValue() == 0.0F;
		if ((obj instanceof Double))
			return ((Double) obj).doubleValue() == 0.0D;
		if ((obj instanceof Short))
			return ((Short) obj).shortValue() == 0;
		if ((obj instanceof Byte)) {
			return ((Byte) obj).byteValue() == 0;
		}
		return false;
	}

	public void setValue(Mapx<?, ?> map) {
		setValue(map, "");
	}

	public void setValue(Mapx<?, ?> map, String prefix) {
		_init();
		CaseIgnoreMapx cim = new CaseIgnoreMapx();
		for (Iterator localIterator = map.keySet().iterator(); localIterator
				.hasNext();) {
			Object k = localIterator.next();
			cim.put(k == null ? null : String.valueOf(k), map.get(k));
		}
		if (prefix == null) {
			prefix = "";
		}
		DAOColumn[] columns = this.meta.getColumns();
		for (int j = 0; j < columns.length; j++) {
			DAOColumn sc = columns[j];
			if (cim.containsKey(prefix + sc.getColumnName()))
				try {
					Object value = cim.get(prefix + sc.getColumnName());
					int type = sc.getColumnType();
					if (type == 12) {
						if ((value instanceof Date))
							setV(j, (Date) value);
						else if ((value != null) && (!"".equals(value))) {
							if (DateUtil.isTime(value.toString()))
								setV(j, DateUtil.parseDateTime(
										value.toString(), "HH:mm:ss"));
							else
								setV(j,
										DateUtil.parseDateTime(value.toString()));
						}
					} else if (type == 6)
						setV(j, new Double(value.toString()));
					else if (type == 5)
						setV(j, new Float(value.toString()));
					else if (type == 7)
						setV(j, new Long(value.toString()));
					else if (type == 8)
						setV(j, new Integer(value.toString()));
					else
						setV(j, value);
				} catch (Exception localException) {
				}
		}
	}

	public void setValue(DataRow dr) {
		_init();
		DAOColumn[] columns = this.meta.getColumns();
		Object value = null;
		String key = null;
		boolean webMode = dr.isWebMode();
		dr.setWebMode(false);
		for (int i = 0; i < dr.getColumnCount(); i++) {
			value = dr.get(i);
			key = dr.getDataColumns()[i].getColumnName();
			for (int j = 0; j < columns.length; j++) {
				DAOColumn sc = columns[j];
				if (key.equalsIgnoreCase(sc.getColumnName())) {
					try {
						int type = sc.getColumnType();
						if (type == 12) {
							if ((value instanceof Date))
								setV(j, (Date) value);
							else if ((value != null) && (!"".equals(value)))
								setV(j,
										DateUtil.parseDateTime(value.toString()));
						} else if (type == 6)
							setV(j, new Double(value.toString()));
						else if (type == 5)
							setV(j, new Float(value.toString()));
						else if (type == 7)
							setV(j, new Long(value.toString()));
						else if (type == 8)
							setV(j, new Integer(value.toString()));
						else {
							setV(j, value);
						}
					} catch (Exception localException) {
					}
				}
			}
		}
		dr.setWebMode(webMode);
	}

	public Object getOldValue(int order) {
		if ((this.oldValues == null) || (this.oldValues.length <= order)) {
			return null;
		}
		return this.oldValues[order];
	}

	public String toString() {
		_init();
		DAOColumn[] columns = this.meta.getColumns();
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (int i = 0; i < columns.length; i++) {
			sb.append(columns[i].getColumnName());
			sb.append(":");
			sb.append(getV(i));
			sb.append(" ");
		}
		sb.append("}");
		return sb.toString();
	}

	public T clone() {
		T s = newInstance();
		DAOUtil.copyFieldValue(this, s);
		return s;
	}

	public Mapx<String, Object> toMapx() {
		_init();
		DAOColumn[] columns = this.meta.getColumns();
		Mapx map = new Mapx();
		for (int i = 0; i < columns.length; i++) {
			map.put(columns[i].getColumnName(), getV(i));
		}
		return map;
	}

	public Mapx<String, Object> toCaseIgnoreMapx() {
		return new CaseIgnoreMapx(toMapx());
	}

	public DataRow toDataRow() {
		_init();
		DAOColumn[] columns = this.meta.getColumns();
		int len = columns.length;
		DataColumn[] dcs = new DataColumn[len];
		Object[] values = new Object[len];
		for (int i = 0; i < len; i++) {
			DataColumn dc = new DataColumn();
			dc.setColumnName(columns[i].getColumnName());
			dc.setColumnType(columns[i].getColumnType());
			dcs[i] = dc;
			values[i] = getV(i);
		}
		return new DataRow(dcs, values);
	}

	public int columnCount() {
		_init();
		return this.meta.getColumns().length;
	}

	public boolean hasLoaded() {
		return this.oldValues != null;
	}

	public DAOColumn getColumn(String name) {
		_init();
		DAOColumn[] columns = this.meta.getColumns();
		for (DAOColumn sc : columns) {
			if (sc.getColumnName().equalsIgnoreCase(name)) {
				return sc;
			}
		}
		return null;
	}

	public void setV(String columnName, Object v) {
		_init();
		this.meta.setV(this, columnName, v);
	}

	public void setV(int i, Object v) {
		_init();
		if ((i < 0) || (i >= this.meta.getColumns().length)) {
			throw new DAOException("DAO.setV() failed,index out of range:"
					+ this.meta.getTable());
		}
		DAOColumn c = this.meta.getColumns()[i];
		setV(c.getColumnName(), v);
	}

	public Object getV(int i) {
		_init();
		if ((i < 0) || (i >= this.meta.getColumns().length)) {
			throw new DAOException("DAO.setV() failed,index out of range:"
					+ this.meta.getTable());
		}
		DAOColumn c = this.meta.getColumns()[i];
		return getV(c.getColumnName());
	}

	public Object getV(String columnName) {
		_init();
		return this.meta.getV(this, columnName);
	}

	public T newInstance() {
		try {
			return (T) getClass().newInstance();
		} catch (Exception e) {
			throw new DAOException(e.getMessage());
		}
	}

	public DAOSet<T> newSet() {
		return new DAOSet();
	}

	public String indexInfo() {
		_init();
		return this.meta.getIndexes();
	}
}
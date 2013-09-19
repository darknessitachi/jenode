package com.zving.framework.data;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.nativejdbc.CommonsDbcpNativeJdbcExtractor;
import com.zving.framework.data.nativejdbc.JBossNativeJdbcExtractor;
import com.zving.framework.data.nativejdbc.WebLogicNativeJdbcExtractor;
import com.zving.framework.data.nativejdbc.WebSphereNativeJdbcExtractor;
import com.zving.framework.utility.commons.ArrayUtils;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

public class DBConn implements Connection {
	protected boolean LongTimeFlag = false;
	protected Connection Conn;
	protected long LastApplyTime;
	protected boolean isUsing = false;

	protected StackTraceElement[] CallerStackTrace = null;

	protected String LastSQL = null;

	protected int ConnID = 0;
	protected long LastWarnTime;
	protected long LastSuccessExecuteTime = System.currentTimeMillis();
	protected boolean isBlockingTransactionStarted;
	protected DBConnConfig DBConfig;
	private static Mapx<String, String> IDMap = new Mapx();

	private static Object mutex = new Object();

	public Connection getPhysicalConnection() {
		if (this.DBConfig.isJNDIPool) {
			try {
				if (Config.isTomcat())
					return CommonsDbcpNativeJdbcExtractor
							.doGetNativeConnection(this.Conn);
				if (Config.isWeblogic())
					return WebLogicNativeJdbcExtractor
							.doGetNativeConnection(this.Conn);
				if (Config.isWebSphere()) {
					return WebSphereNativeJdbcExtractor
							.doGetNativeConnection(this.Conn);
				}
				return JBossNativeJdbcExtractor
						.doGetNativeConnection(this.Conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return this.Conn;
	}

	public int getHoldability() throws SQLException {
		return this.Conn.getHoldability();
	}

	public int getTransactionIsolation() throws SQLException {
		return this.Conn.getTransactionIsolation();
	}

	public void clearWarnings() throws SQLException {
		this.Conn.clearWarnings();
	}

	public void close() throws SQLException {
		this.isUsing = false;
		this.LastApplyTime = 0L;
		setAutoCommit(true);
		if (this.DBConfig.isJNDIPool)
			this.Conn.close();
	}

	public void closeReally() throws SQLException {
		DBConnPoolImpl pool = (DBConnPoolImpl) DBConnPool.PoolMap
				.get(this.DBConfig.DBName + ".");
		removeConnID(this.ConnID);
		if (pool != null) {
			DBConn[] arr = (DBConn[]) ArrayUtils
					.removeElement(pool.conns, this);
			DBConn[] arr2 = new DBConn[arr.length + 1];
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = arr[i];
			}
			pool.conns = arr2;
			pool.getDBConnConfig().ConnCount -= 1;
		}
		this.Conn.close();
	}

	public void commit() throws SQLException {
		if (!this.Conn.getAutoCommit())
			this.Conn.commit();
	}

	public void rollback() throws SQLException {
		this.Conn.rollback();
	}

	public boolean getAutoCommit() throws SQLException {
		return this.Conn.getAutoCommit();
	}

	public boolean isClosed() throws SQLException {
		return this.Conn.isClosed();
	}

	public boolean isReadOnly() throws SQLException {
		return this.Conn.isReadOnly();
	}

	public void setHoldability(int holdability) throws SQLException {
		this.Conn.setHoldability(holdability);
	}

	public void setTransactionIsolation(int level) throws SQLException {
		this.Conn.setTransactionIsolation(level);
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		if (this.Conn.getAutoCommit() != autoCommit)
			this.Conn.setAutoCommit(autoCommit);
	}

	public void setReadOnly(boolean readOnly) throws SQLException {
		this.Conn.setReadOnly(readOnly);
	}

	public String getCatalog() throws SQLException {
		return this.Conn.getCatalog();
	}

	public void setCatalog(String catalog) throws SQLException {
		this.Conn.setCatalog(catalog);
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return this.Conn.getMetaData();
	}

	public SQLWarning getWarnings() throws SQLException {
		return this.Conn.getWarnings();
	}

	public Savepoint setSavepoint() throws SQLException {
		return this.Conn.setSavepoint();
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		this.Conn.releaseSavepoint(savepoint);
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		this.Conn.rollback(savepoint);
	}

	public Statement createStatement() throws SQLException {
		return this.Conn.createStatement();
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return this.Conn.createStatement(resultSetType, resultSetConcurrency);
	}

	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return this.Conn.createStatement(resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return this.Conn.getTypeMap();
	}

	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		this.Conn.setTypeMap(map);
	}

	public String nativeSQL(String sql) throws SQLException {
		return this.Conn.nativeSQL(sql);
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		return this.Conn.prepareCall(sql);
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		return this.Conn.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return this.Conn.prepareCall(sql, resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return this.Conn.prepareStatement(sql);
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		return this.Conn.prepareStatement(sql, autoGeneratedKeys);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		this.LastSQL = sql;
		return this.Conn.prepareStatement(sql, resultSetType,
				resultSetConcurrency);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return this.Conn.prepareStatement(sql, resultSetType,
				resultSetConcurrency, resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {
		return this.Conn.prepareStatement(sql, columnIndexes);
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		return this.Conn.setSavepoint(name);
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException {
		return this.Conn.prepareStatement(sql, columnNames);
	}

	public long getLastSuccessExecuteTime() {
		return this.LastSuccessExecuteTime;
	}

	public void setLastSuccessExecuteTime(long lastSuccessExecuteTime) {
		this.LastSuccessExecuteTime = lastSuccessExecuteTime;
	}

	public DBConnConfig getDBConfig() {
		return this.DBConfig;
	}

	public void setPoolName(DBConnConfig dbcc) {
		this.DBConfig = dbcc;
	}

	/** @deprecated */
	public String getDBType() {
		return this.DBConfig.DBType;
	}

	public static int getConnID() {
		synchronized (mutex) {
			for (int i = 1; i <= 2000; i++) {
				if (!IDMap.containsKey(i)) {
					IDMap.put(i+"", "1");
					return i;
				}
			}
		}
		return 0;
	}

	public static void removeConnID(int id) {
		synchronized (mutex) {
			IDMap.remove(id);
		}
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return Conn.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return Conn.isWrapperFor(iface);
	}

	@Override
	public Clob createClob() throws SQLException {
		return Conn.createClob();
	}

	@Override
	public Blob createBlob() throws SQLException {
		return Conn.createBlob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return Conn.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return Conn.createSQLXML();
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return Conn.isValid(timeout);
	}

	@Override
	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		Conn.setClientInfo(name, value);
	}

	@Override
	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
		Conn.setClientInfo(properties);
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return Conn.getClientInfo(name);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return Conn.getClientInfo();
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		return Conn.createArrayOf(typeName, elements);
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		return Conn.createStruct(typeName, attributes);
	}
}
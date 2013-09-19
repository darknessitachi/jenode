package com.zving.framework.data;

import com.zving.framework.collection.Executor;
import com.zving.framework.data.exception.DatabaseException;
import com.zving.framework.orm.DAO;
import com.zving.framework.orm.DAOSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Transaction {
	public static final int INSERT = 1;
	public static final int UPDATE = 2;
	public static final int DELETE = 3;
	public static final int BACKUP = 4;
	public static final int DELETE_AND_BACKUP = 5;
	public static final int DELETE_AND_INSERT = 6;
	public static final int SQL = 7;
	protected boolean outerConnFlag = false;
	protected DataAccess dataAccess;
	protected ArrayList<Object> list = new ArrayList();
	protected String backupOperator;
	protected String backupMemo;
	protected String exceptionMessage;
	protected ArrayList<Executor> executorList = new ArrayList(4);
	protected String poolName;

	public Transaction() {
	}

	public Transaction(String poolName) {
		this.poolName = poolName;
	}

	public Transaction setDataAccess(DataAccess dAccess) {
		this.dataAccess = dAccess;
		this.outerConnFlag = true;
		return this;
	}

	public Transaction add(QueryBuilder qb) {
		this.list.add(new Object[] { qb, new Integer(7) });
		return this;
	}

	public Transaction add(DAO<?> dao, int type) {
		this.list.add(new Object[] { dao, new Integer(type) });
		return this;
	}

	public Transaction add(DAOSet<?> set, int type) {
		this.list.add(new Object[] { set, new Integer(type) });
		return this;
	}

	public Transaction insert(DAO<?> dao) {
		return add(dao, 1);
	}

	public Transaction insert(DAOSet<?> set) {
		return add(set, 1);
	}

	public Transaction update(DAO<?> dao) {
		return add(dao, 2);
	}

	public Transaction update(DAOSet<?> set) {
		return add(set, 2);
	}

	public Transaction delete(DAO<?> dao) {
		return add(dao, 3);
	}

	public Transaction delete(DAOSet<?> set) {
		return add(set, 3);
	}

	public Transaction backup(DAO<?> dao) {
		return add(dao, 4);
	}

	public Transaction backup(DAOSet<?> set) {
		return add(set, 4);
	}

	public Transaction deleteAndBackup(DAO<?> dao) {
		return add(dao, 5);
	}

	public Transaction deleteAndBackup(DAOSet<?> set) {
		return add(set, 5);
	}

	public Transaction deleteAndInsert(DAO<?> dao) {
		return add(dao, 6);
	}

	public Transaction deleteAndInsert(DAOSet<?> set) {
		return add(set, 6);
	}

	public boolean commit() {
		return commit(true);
	}

	public boolean commit(boolean setAutoCommitStatus) {
		if (!this.outerConnFlag) {
			this.dataAccess = new DataAccess(
					DBConnPool.getConnection(this.poolName));
		}
		boolean NoErrFlag = true;
		try {
			if ((!this.outerConnFlag) || (setAutoCommitStatus))
				this.dataAccess.setAutoCommit(false);
			for (int i = 0; i < this.list.size(); i++) {
				Object[] arr = (Object[]) this.list.get(i);
				Object obj = arr[0];
				int type = ((Integer) arr[1]).intValue();
				if (!executeObject(obj, type)) {
					NoErrFlag = false;
					return false;
				}
			}
			this.dataAccess.commit();
			this.list.clear();
		} catch (Exception e) {
			e.printStackTrace();
			this.exceptionMessage = e.getMessage();
			NoErrFlag = false;
			return false;
		} finally {
			if (!NoErrFlag)
				try {
					this.dataAccess.rollback();
				} catch (DatabaseException e1) {
					e1.printStackTrace();
				}
			try {
				if ((!this.outerConnFlag) || (setAutoCommitStatus))
					this.dataAccess.setAutoCommit(true);
			} catch (DatabaseException e1) {
				e1.printStackTrace();
			}
			if (!this.outerConnFlag)
				try {
					this.dataAccess.close();
				} catch (DatabaseException e) {
					e.printStackTrace();
				}
		}
		if (!NoErrFlag)
			try {
				this.dataAccess.rollback();
			} catch (DatabaseException e1) {
				e1.printStackTrace();
			}
		try {
			if ((!this.outerConnFlag) || (setAutoCommitStatus))
				this.dataAccess.setAutoCommit(true);
		} catch (DatabaseException e1) {
			e1.printStackTrace();
		}
		if (!this.outerConnFlag) {
			try {
				this.dataAccess.close();
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < this.executorList.size(); i++) {
			Executor executor = (Executor) this.executorList.get(i);
			executor.execute();
		}
		return true;
	}

	protected boolean executeObject(Object obj, int type) throws SQLException {
		if ((obj instanceof QueryBuilder)) {
			this.dataAccess.executeNoQuery((QueryBuilder) obj);
		} else if ((obj instanceof DAO)) {
			DAO s = (DAO) obj;
			s.setDataAccess(this.dataAccess);
			if (type == 1) {
				if (!s.insert())
					return false;
			} else if (type == 2) {
				if (!s.update())
					return false;
			} else if (type == 3) {
				if (!s.delete())
					return false;
			} else if (type == 4) {
				if (s.backup(this.backupOperator, this.backupMemo) == null)
					return false;
			} else if (type == 5) {
				if (!s.deleteAndBackup(this.backupOperator, this.backupMemo))
					return false;
			} else if ((type == 6) && (!s.deleteAndInsert())) {
				return false;
			}
		} else if ((obj instanceof DAOSet)) {
			DAOSet s = (DAOSet) obj;
			s.setDataAccess(this.dataAccess);
			if (type == 1) {
				if (!s.insert())
					return false;
			} else if (type == 2) {
				if (!s.update())
					return false;
			} else if (type == 3) {
				if (!s.delete())
					return false;
			} else if (type == 4) {
				if (!s.backup(this.backupOperator, this.backupMemo))
					return false;
			} else if (type == 5) {
				if (!s.deleteAndBackup(this.backupOperator, this.backupMemo))
					return false;
			} else if ((type == 6) && (!s.deleteAndInsert())) {
				return false;
			}
		}

		return true;
	}

	public void clear() {
		this.list.clear();
	}

	public String getExceptionMessage() {
		return this.exceptionMessage;
	}

	public String getBackupMemo() {
		return this.backupMemo;
	}

	public Transaction setBackupMemo(String backupMemo) {
		this.backupMemo = backupMemo;
		return this;
	}

	public String getBackupOperator() {
		return this.backupOperator;
	}

	public Transaction setBackupOperator(String backupOperator) {
		this.backupOperator = backupOperator;
		return this;
	}

	public ArrayList<Object> getOperateList() {
		return this.list;
	}

	public Transaction addExecutor(Executor executor) {
		this.executorList.add(executor);
		return this;
	}
}
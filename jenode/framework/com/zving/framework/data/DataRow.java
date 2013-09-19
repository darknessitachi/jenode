package com.zving.framework.data;

import com.zving.framework.collection.CaseIgnoreMapx;
import com.zving.framework.collection.Mapx;
import com.zving.framework.json.JSON;
import com.zving.framework.json.JSONAware;
import com.zving.framework.utility.DateUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.framework.utility.commons.ArrayUtils;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class DataRow implements Serializable, JSONAware {
	private static final long serialVersionUID = 1L;
	private boolean isWebMode;
	protected DataColumn[] columns;
	protected Object[] values;

	public DataRow(DataColumn[] types, Object[] values) {
		this.columns = types;
		this.values = values;
	}

	public Object get(int index) {
		if (this.values == null) {
			return null;
		}
		if ((index < 0) || (index >= this.columns.length)) {
			throw new RuntimeException("DataRow ：" + index);
		}
		return this.values[index];
	}

	public Object get(String columnName) {
		if ((columnName == null) || (columnName.equals(""))) {
			throw new RuntimeException("Column name can't be empty");
		}
		for (int i = 0; i < this.columns.length; i++) {
			if (this.columns[i].getColumnName().equalsIgnoreCase(columnName)) {
				return this.values[i];
			}
		}

		return null;
	}

	public String getString(int index) {
		if (this.values[index] != null) {
			if ((!"".equals(this.values[index]))
					&& (this.columns[index].getColumnType() == 12)) {
				if (!(this.values[index] instanceof Date)) {
					return String.valueOf(this.values[index]);
				}
				if (StringUtil.isNotEmpty(this.columns[index].getDateFormat())) {
					return DateUtil.toString((Date) this.values[index],
							this.columns[index].getDateFormat());
				}
				return DateUtil.toString((Date) this.values[index],
						"yyyy-MM-dd HH:mm:ss");
			}

			String t = String.valueOf(this.values[index]).trim();
			if ((this.isWebMode) && ((t == null) || (t.equals("")))) {
				return "&nbsp;";
			}

			return t;
		}
		if (this.isWebMode) {
			return "&nbsp;";
		}
		return "";
	}

	public String getString(String columnName) {
		if ((columnName == null) || (columnName.equals(""))) {
			throw new RuntimeException("Column name can't be empty");
		}
		for (int i = 0; i < this.columns.length; i++) {
			if (this.columns[i].getColumnName().equalsIgnoreCase(columnName)) {
				return getString(i);
			}
		}
		return "";
	}

	public Date getDate(int index) {
		Object obj = get(index);
		if (obj == null) {
			return null;
		}
		if ((obj instanceof Date)) {
			return (Date) obj;
		}
		return DateUtil.parseDateTime(obj.toString());
	}

	public Date getDate(String columnName) {
		if ((columnName == null) || (columnName.equals(""))) {
			throw new RuntimeException("Column name can't be empty");
		}
		for (int i = 0; i < this.columns.length; i++) {
			if (this.columns[i].getColumnName().equalsIgnoreCase(columnName)) {
				return getDate(i);
			}
		}
		return null;
	}

	public double getDouble(int index) {
		Object obj = get(index);
		if (obj == null) {
			return 0.0D;
		}
		if ((obj instanceof Number)) {
			return ((Number) obj).doubleValue();
		}
		String str = obj.toString();
		if (StringUtil.isEmpty(str)) {
			return 0.0D;
		}
		return Double.parseDouble(str);
	}

	public double getDouble(String columnName) {
		if ((columnName == null) || (columnName.equals(""))) {
			throw new RuntimeException("Column name can't be empty");
		}
		for (int i = 0; i < this.columns.length; i++) {
			if (this.columns[i].getColumnName().equalsIgnoreCase(columnName)) {
				return getDouble(i);
			}
		}
		return 0.0D;
	}

	public long getLong(int index) {
		Object obj = get(index);
		if (obj == null) {
			return 0L;
		}
		if ((obj instanceof Number)) {
			return ((Number) obj).longValue();
		}
		String str = obj.toString();
		if (StringUtil.isEmpty(str)) {
			return 0L;
		}
		return Long.parseLong(str);
	}

	public long getLong(String columnName) {
		if ((columnName == null) || (columnName.equals(""))) {
			throw new RuntimeException("Column name can't be empty");
		}
		for (int i = 0; i < this.columns.length; i++) {
			if (this.columns[i].getColumnName().equalsIgnoreCase(columnName)) {
				return getLong(i);
			}
		}
		return 0L;
	}

	public int getInt(int index) {
		Object obj = get(index);
		if (obj == null) {
			return 0;
		}
		if ((obj instanceof Number)) {
			return ((Number) obj).intValue();
		}
		String str = obj.toString();
		if (StringUtil.isEmpty(str)) {
			return 0;
		}
		return Integer.parseInt(str);
	}

	public int getInt(String columnName) {
		if ((columnName == null) || (columnName.equals(""))) {
			throw new RuntimeException("Column name can't be empty");
		}
		for (int i = 0; i < this.columns.length; i++) {
			if (this.columns[i].getColumnName().equalsIgnoreCase(columnName)) {
				return getInt(i);
			}
		}
		return 0;
	}

	public boolean isNull(int index) {
		return get(index) == null;
	}

	public boolean isNull(String columnName) {
		return get(columnName) == null;
	}

	public DataRow set(int index, Object value) {
		if (this.values == null) {
			return this;
		}
		if ((index < 0) || (index >= this.values.length)) {
			throw new RuntimeException("Index is out of range:" + index);
		}
		this.values[index] = value;
		return this;
	}

	public DataRow set(String columnName, Object value) {
		if ((columnName == null) || (columnName.equals(""))) {
			throw new RuntimeException("Column name can't be empty");
		}
		for (int i = 0; i < this.values.length; i++) {
			if (this.columns[i].getColumnName().equalsIgnoreCase(columnName)) {
				this.values[i] = value;
				return this;
			}
		}
		throw new RuntimeException("Column name not found：" + columnName);
	}

	public DataColumn getDataColumn(int index) {
		if ((index < 0) || (index >= this.columns.length)) {
			throw new RuntimeException("Index is out of range：" + index);
		}
		return this.columns[index];
	}

	public DataColumn getDataColumn(String columnName) {
		if ((columnName == null) || (columnName.equals(""))) {
			throw new RuntimeException("Column name can't be empty");
		}
		for (int i = 0; i < this.columns.length; i++) {
			if (this.columns[i].getColumnName().equalsIgnoreCase(columnName)) {
				return this.columns[i];
			}
		}
		return null;
	}

	public int getColumnCount() {
		return this.columns.length;
	}

	public Object[] getDataValues() {
		return this.values;
	}

	public DataColumn[] getDataColumns() {
		return this.columns;
	}

	public DataRow insertColumn(String columnName, Object columnValue) {
		return insertColumn(new DataColumn(columnName, 1), columnValue);
	}

	public DataRow insertColumn(String columnName, Object columnValue, int index) {
		return insertColumn(new DataColumn(columnName, 1), columnValue, index);
	}

	public DataRow insertColumn(DataColumn dc, Object columnValue) {
		return insertColumn(dc, columnValue, this.values.length);
	}

	public DataRow insertColumn(DataColumn dc, Object columnValue, int index) {
		if ((index < 0) || (index > this.columns.length)) {
			throw new RuntimeException(index + " is out of range,max is "
					+ this.columns.length);
		}
		this.columns = ((DataColumn[]) ArrayUtils.add(this.columns, index, dc));
		this.values = ArrayUtils.add(this.values, index, columnValue);
		return this;
	}

	public boolean isWebMode() {
		return this.isWebMode;
	}

	public DataRow setWebMode(boolean isWebMode) {
		this.isWebMode = isWebMode;
		return this;
	}

	public Mapx<String, Object> toMapx() {
		Mapx map = new Mapx();
		for (int i = 0; i < this.columns.length; i++) {
			Object v = this.values[i];
			if ((this.columns[i].getColumnType() == 12)
					&& ((v instanceof Date))) {
				if (StringUtil.isNotEmpty(this.columns[i].getDateFormat()))
					v = DateUtil.toString((Date) v,
							this.columns[i].getDateFormat());
				else {
					v = DateUtil.toString((Date) v, "yyyy-MM-dd HH:mm:ss");
				}
			}

			map.put(this.columns[i].getColumnName(), v);
		}
		return map;
	}

	public CaseIgnoreMapx<String, Object> toCaseIgnoreMapx() {
		CaseIgnoreMapx map = new CaseIgnoreMapx();
		for (int i = 0; i < this.columns.length; i++) {
			map.put(this.columns[i].getColumnName(), this.values[i]);
		}
		return map;
	}

	public DataRow fill(Mapx<?, ?> map) {
		if (map == null) {
			return this;
		}
		for (Iterator localIterator = map.keySet().iterator(); localIterator
				.hasNext();) {
			Object key = localIterator.next();
			if (key != null) {
				Object v = map.get(key);
				for (int j = 0; j < this.columns.length; j++) {
					if (key.toString().equalsIgnoreCase(
							this.columns[j].getColumnName())) {
						if ((v != null) && (this.columns[j].ColumnType == 12)
								&& (!Date.class.isInstance(v))) {
							Date d = DateUtil.parseDateTime(v.toString());
							if (d == null) {
								throw new RuntimeException(
										"Invalid date string:" + v);
							}
							v = d;
						}

						set(j, v);
					}
				}
			}
		}
		return this;
	}

	public DataRow fill(Object[] values) {
		if (values == null) {
			return this;
		}
		if (values.length != getColumnCount()) {
			throw new RuntimeException("Parameter's length is " + values.length
					+ "，bit DataRow's length is " + getColumnCount());
		}
		for (int i = 0; i < values.length; i++) {
			Object v = values[i];
			if ((v != null) && (this.columns[i].ColumnType == 12)
					&& (!Date.class.isInstance(v))) {
				Date d = DateUtil.parseDateTime(v.toString());
				if (d == null) {
					throw new RuntimeException("Invalid date string:" + v);
				}
				v = d;
			}

			set(i, v);
		}
		return this;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.columns.length; i++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append(this.columns[i].getColumnName());
			sb.append(":");
			sb.append(this.values[i]);
		}
		return sb.toString();
	}

	public String toJSONString() {
		return JSON.toJSONString(ObjectUtil.toList(this.values));
	}
}
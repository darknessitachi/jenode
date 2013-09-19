package com.zving.framework.data;

public class Q extends QueryBuilder {
	
	public Q() {
	}

	public Q(String sql, Object[] args) {
		super(sql, args);
	}
}
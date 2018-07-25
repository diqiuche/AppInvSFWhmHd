package com.invengo.rfd6c.sfwhmhd.bean;

import tk.ziniulian.util.dao.BaseBean;

import static tk.ziniulian.util.Str.meg;

/**
 * 表版本信息
 * Created by 李泽荣 on 2018/7/23.
 */

public class TableVersion extends BaseBean {
	private String TableName;	// 表名称
	private int Version;		// 表版本

	public int getVersion() {
		return Version;
	}

	public String getTableName() {

		return TableName;
	}

	@Override
	public String getAddSql() {
		String r = "insert into TableVersion values('<0>', <1>)";
		return meg(r, TableName, Version + "");
	}

	@Override
	public String getDelSql() {
		String r = "delete from TableVersion where TableName='<0>'";
		return meg(r, TableName);
	}

	@Override
	public String getSetSql() {
		String r = "update TableVersion set Version=<1> where TableName='<0>'";
		return meg(r, TableName, Version + "");
	}
}

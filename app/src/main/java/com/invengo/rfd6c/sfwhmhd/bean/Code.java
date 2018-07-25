package com.invengo.rfd6c.sfwhmhd.bean;

import tk.ziniulian.util.dao.BaseBean;

import static tk.ziniulian.util.Str.meg;

/**
 * 数据字典信息
 * Created by 李泽荣 on 2018/7/23.
 */

public class Code extends BaseBean {
	private String dbType;		// 类型
	private String dbCode;		// ID
	private String dbName;		// 名称
	private String dbTypeBeyond;	// 父类型
	private String dbCodeBeyond;	// 父ID

	public Code setKey(String s) {
		String[] sa = s.split(",");
		this.dbType = sa[0];
		this.dbCode = sa[1];
		return this;
	}

	public String getDbType() {
		return dbType;
	}

	public String getDbCode() {
		return dbCode;
	}

	public String getDbName() {
		return dbName;
	}

	public String getDbTypeBeyond() {
		return dbTypeBeyond;
	}

	public String getDbCodeBeyond() {
		return dbCodeBeyond;
	}

	@Override
	public String getAddSql() {
		String r = "insert into Code values('<0>', '<1>', '<2>', '<3>', '<4>')";
		return meg(r, dbType, dbCode, dbName, dbTypeBeyond, dbCodeBeyond);
	}

	@Override
	public String getDelSql() {
		String r = "delete from Code where dbType='<0>' and dbCode='<1>'";
		return meg(r, dbType, dbCode);
	}

	@Override
	public String getSetSql() {
		String r = "update Code set dbName='<2>', dbTypeBeyond='<3>', dbCodeBeyond='<4>' where dbType='<0>' and dbCode='<1>'";
		return meg(r, dbType, dbCode, dbName, dbTypeBeyond, dbCodeBeyond);
	}
}

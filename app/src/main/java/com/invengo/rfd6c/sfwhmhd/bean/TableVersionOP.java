package com.invengo.rfd6c.sfwhmhd.bean;

/**
 * 表版本操作信息
 * Created by 李泽荣 on 2018/7/23.
 */

public class TableVersionOP {
	private String TableName;	// 表名称
	private int TableVersion;	// 表版本
	private String OpType;	// 操作类型
	private String OpTablePK;	// 操作表主键
	private String OpTime;	// 操作时间
	private String Info;	// 对应的修改表信息，json格式字符

	public String getTableName() {
		return TableName;
	}

	public int getTableVersion() {
		return TableVersion;
	}

	public String getOpType() {
		return OpType;
	}

	public String getOpTablePK() {
		return OpTablePK;
	}

	public String getOpTime() {
		return OpTime;
	}

	public String getInfo() {
		return Info;
	}
}

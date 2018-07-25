package com.invengo.rfd6c.sfwhmhd.bean;

/**
 * 盘点信息
 * Created by 李泽荣 on 2018/7/23.
 */

public class Check {
	private String CheckCode;		// 盘点单号
	private String CheckPartsType;	// 盘点类型
	private String AddUser;		// 创建人
	private String AddTime;		// 创建时间
	private String Remark;		// 备注
	private String IsFinish;	// 是否完成

	public String getCheckCode() {
		return CheckCode;
	}

	public String getCheckPartsType() {
		return CheckPartsType;
	}

	public String getAddUser() {
		return AddUser;
	}

	public String getAddTime() {
		return AddTime;
	}

	public String getRemark() {
		return Remark;
	}

	public String getIsFinish() {
		return IsFinish;
	}
}

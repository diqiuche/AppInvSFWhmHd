package com.invengo.rfd6c.sfwhmhd.bean;

/**
 * 盘点明细信息
 * Created by 李泽荣 on 2018/7/23.
 */

public class CheckDetail {
	private String CheckCode;	// 盘点单号
	private String PartsCode;	// 物料编号
	private String CheckUser;	// 盘点人
	private String CheckTime;	// 盘点时间
	private String IsFind;		// 是否完成
	private String StorageLocation;		// 库位

	public String getCheckCode() {
		return CheckCode;
	}

	public String getPartsCode() {
		return PartsCode;
	}

	public String getCheckUser() {
		return CheckUser;
	}

	public String getCheckTime() {
		return CheckTime;
	}

	public String getStorageLocation() {
		return StorageLocation;
	}

	public String getIsFind() {
		return IsFind;
	}
}

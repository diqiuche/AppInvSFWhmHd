package com.invengo.rfd6c.sfwhmhd.bean;

import tk.ziniulian.util.dao.BaseBean;

import static tk.ziniulian.util.Str.meg;

/**
 * 库存信息
 * Created by 李泽荣 on 2018/7/20.
 */

public class Inventory extends BaseBean {
	private String StorageLocation;	// 库位编码
	private String PartsCode;	// 物料编码
	private String BatchNo;	// 批次号
	private int Num;	// 数量
	private String TagCode;	// TID

	public Inventory setKey(String s) {
		String[] sa = s.split(",");
		this.PartsCode = sa[0];
		this.BatchNo = sa[1];
		return this;
	}

	public Inventory setStorageLocation(String storageLocation) {
		StorageLocation = storageLocation;
		return this;
	}

	public Inventory setNum(int num) {
		Num = num;
		return this;
	}

	public Inventory setTagCode(String tagCode) {
		TagCode = tagCode;
		return this;
	}

	public String getPartsCode() {
		return PartsCode;
	}

	public String getStorageLocation() {
		return StorageLocation;
	}

	public String getBatchNo() {
		return BatchNo;
	}

	public int getNum() {
		return Num;
	}

	public String getTagCode() {
		return TagCode;
	}

	@Override
	public String getAddSql() {
		String r = "insert into Inventory values('<0>', '<1>', '<2>', <3>, '<4>')";
		return meg(r, StorageLocation, PartsCode, BatchNo, Num + "", TagCode);
	}

	@Override
	public String getDelSql() {
		String r = "delete from Inventory where PartsCode='<0>' and BatchNo='<1>'";
		return meg(r, PartsCode, BatchNo);
	}

	@Override
	public String getSetSql() {
		String r = "update Inventory set StorageLocation='<0>', Num=<3>, TagCode='<4>' where PartsCode='<1>' and BatchNo='<2>'";
		return meg(r, StorageLocation, PartsCode, BatchNo, Num + "", TagCode);
	}
}

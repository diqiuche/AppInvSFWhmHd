package com.invengo.rfd6c.sfwhmhd.bean;

import tk.ziniulian.util.dao.BaseBean;

import static tk.ziniulian.util.Str.meg;

/**
 * 库位信息
 * Created by 李泽荣 on 2018/7/20.
 */

public class Location extends BaseBean {
	private String LocationCode;	// 库位编码
	private String PartAllow;	// 允许存放的物料编号
	private int MaxVolumn;	// 最大存放数量
	private String IsEnable;	// 是否可用
	private String TagCode;	// TID

	public Location setLocationCode(String locationCode) {
		LocationCode = locationCode;
		return this;
	}

	public String getLocationCode() {
		return LocationCode;
	}

	public String getPartAllow() {
		return PartAllow;
	}

	public int getMaxVolumn() {
		return MaxVolumn;
	}

	public String getIsEnable() {
		return IsEnable;
	}

	public String getTagCode() {
		return TagCode;
	}

	@Override
	public String getAddSql() {
		String r = "insert into Location values('<0>', '<1>', <2>, '<3>', '<4>')";
		return meg(r, LocationCode, PartAllow, MaxVolumn + "", IsEnable, TagCode);
	}

	@Override
	public String getDelSql() {
		String r = "delete from Location where LocationCode='<0>'";
		return meg(r, LocationCode);
	}

	@Override
	public String getSetSql() {
		String r = "update Location set PartAllow='<1>', MaxVolumn=<2>, IsEnable='<3>', TagCode='<4>' where LocationCode='<0>'";
		return meg(r, LocationCode, PartAllow, MaxVolumn + "", IsEnable, TagCode);
	}
}

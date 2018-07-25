package com.invengo.rfd6c.sfwhmhd.bean;

import tk.ziniulian.util.dao.BaseBean;

import static tk.ziniulian.util.Str.meg;

/**
 * 层架区域
 * Created by 李泽荣 on 2018/7/23.
 */

public class Area extends BaseBean {
	private String AreaCode;	// 区域编码
	private String TagCode;		// TID

	public Area setAreaCode(String areaCode) {
		AreaCode = areaCode;
		return this;
	}

	public String getAreaCode() {
		return AreaCode;
	}

	public String getTagCode() {
		return TagCode;
	}

	@Override
	public String getAddSql() {
		String r = "insert into Area values('<0>', '<1>')";
		return meg(r, AreaCode, TagCode);
	}

	@Override
	public String getDelSql() {
		String r = "delete from Area where AreaCode='<0>'";
		return meg(r, AreaCode);
	}

	@Override
	public String getSetSql() {
		String r = "update Area set TagCode='<1>' where AreaCode='<0>'";
		return meg(r, AreaCode, TagCode);
	}
}

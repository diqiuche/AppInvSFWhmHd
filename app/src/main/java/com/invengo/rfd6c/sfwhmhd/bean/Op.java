package com.invengo.rfd6c.sfwhmhd.bean;

import tk.ziniulian.util.dao.BaseBean;

import static tk.ziniulian.util.Str.meg;

/**
 * 操作记录
 * Created by 李泽荣 on 2018/7/24.
 */

public class Op extends BaseBean {
	private String PartsCode;	// 物料编号
	private String OpType;		// 操作类型
	private String Info;		// 信息

	public Op setPartsCode(String partsCode) {
		PartsCode = partsCode;
		return this;
	}

	public Op setOpType(String opType) {
		OpType = opType;
		return this;
	}

	public Op setInfo(String info) {
		Info = info;
		return this;
	}

	// 获取向服务器上传时的数据格式
	public String getPushStr() {
		StringBuilder r = new StringBuilder();
		r.append(PartsCode);
		r.append(",");
		r.append(OpType);
		r.append(",");
		r.append(Info);
		return r.toString().replace("-", "===");
	}

	@Override
	public String getAddSql() {
		String r = "insert into Op values('<0>', '<1>', '<2>')";
		return meg(r, PartsCode, OpType, Info);
	}

	@Override
	public String getDelSql() {
		String r = "delete from Op where PartsCode='<0>' and OpType='<1>' and Info='<2>'";
		return meg(r, PartsCode, OpType, Info);
	}

	@Override
	public String getSetSql() {
		return null;
	}
}

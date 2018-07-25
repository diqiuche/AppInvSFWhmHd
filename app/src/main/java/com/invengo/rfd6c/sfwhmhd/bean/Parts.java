package com.invengo.rfd6c.sfwhmhd.bean;

import tk.ziniulian.util.dao.BaseBean;

import static tk.ziniulian.util.Str.meg;

/**
 * 物料信息
 * Created by 李泽荣 on 2018/7/20.
 */

public class Parts extends BaseBean {
	private String PartCode;	// 物料编码
	private String PartName;	// 物料名称
	private String PartSort;	// 规格型号
	private String FactoryCode;	// 主机厂编码
	private String Unit;	// 单位
	private String Status;	// 物料状态
	private String Remark;	// 备注

	public Parts setPartCode(String partCode) {
		PartCode = partCode;
		return this;
	}

	public String getPartCode() {
		return PartCode;
	}

	public String getRemark() {
		return Remark;
	}

	public String getStatus() {
		return Status;
	}

	public String getUnit() {
		return Unit;
	}

	public String getFactoryCode() {
		return FactoryCode;
	}

	public String getPartSort() {
		return PartSort;
	}

	public String getPartName() {
		return PartName;
	}

	@Override
	public String getAddSql() {
		String r = "insert into Parts values('<0>', '<1>', '<2>', '<3>', '<4>', '<5>', '<6>')";
		return meg(r, PartCode, PartName, PartSort, FactoryCode, Unit, Status, Remark);
	}

	@Override
	public String getDelSql() {
		String r = "delete from Parts where PartCode='<0>'";
		return meg(r, PartCode);
	}

	@Override
	public String getSetSql() {
		String r = "update Parts set PartName='<1>', PartSort='<2>', FactoryCode='<3>', Unit='<4>', Status='<5>', Remark='<6>' where PartCode='<0>'";
		return meg(r, PartCode, PartName, PartSort, FactoryCode, Unit, Status, Remark);
	}
}

package com.invengo.rfd6c.sfwhmhd.entity;

/**
 * 物料标签
 * Created by 李泽荣 on 2018/7/20.
 */

public class TagP {
	private String typ = "M";	// 类型
	private String cod = "";	// 物料编码
	private String nam = "";	// 物料名称
	private String PartSort = "";	// 规格型号
	private String codF = "";	// 主机厂编码
	private String bn = "";	// 批次号
	private int num = 0;	// 数量
	private String codL = "";	// 库位编码
	private String tid = "";	// TID

	public TagP setCodF(String codF) {
		this.codF = codF;
		return this;
	}

	public TagP setTyp(String typ) {
		this.typ = typ;
		return this;
	}

	public TagP setCod(String cod) {
		this.cod = cod;
		return this;
	}

	public TagP setNam(String nam) {
		this.nam = nam;
		return this;
	}

	public TagP setPartSort(String partSort) {
		PartSort = partSort;
		return this;
	}

	public TagP setBn(String bn) {
		this.bn = bn;
		return this;
	}

	public TagP setCodL(String codL) {
		this.codL = codL;
		return this;
	}

	public TagP setNum(int num) {
		this.num = num;
		return this;
	}

	public TagP setTid(String tid) {
		this.tid = tid;
		return this;
	}

}

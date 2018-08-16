package com.invengo.rfd6c.sfwhmhd.entity;

/**
 * 库位标签
 * Created by LZR on 2018/8/16.
 */

public class TagL {
	private String typ = "L";	// 类型
	private String cod = "";	// 物料编码

	public TagL setTyp(String typ) {
		this.typ = typ;
		return this;
	}

	public TagL setCod(String cod) {
		this.cod = cod;
		return this;
	}
}

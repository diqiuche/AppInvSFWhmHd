package com.invengo.rfd6c.sfwhmhd.enums;

/**
 * 操作类型
 * Created by 李泽荣 on 2018/7/23.
 */

public enum EmOpTyp {
	In(1),
	Out(2);

	private final int id;
	EmOpTyp(int i) {
		id = i;
	}

	@Override
	public String toString() {
		return id + "";
	}
}

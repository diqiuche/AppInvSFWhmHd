package com.invengo.rfd6c.sfwhmhd.enums;

/**
 * 页面信息
 * Created by 李泽荣 on 2018/7/17.
 */

public enum EmUrl {
	// RFID 测试
	RfScaning("javascript: rfid.scan();"),
	RfStoped("javascript: rfid.stop();"),
	RfWrtOk("javascript: rfid.hdWrt(true);"),
	RfWrtErr("javascript: rfid.hdWrt(false);"),
	ScanTt("file:///android_asset/testRFID/scanDemo.html"),
	WrtTt("file:///android_asset/testRFID/writeDemo.html"),

	// 条码、二维码 测试
	QrOnRead("javascript: qr.hdScan(<0>);"),
	QrTt("file:///android_asset/testQR/qrDemo.html"),

	// 主页
	Home("file:///android_asset/web/home.html"),
	About("file:///android_asset/web/about.html"),
	WhIn("file:///android_asset/web/whIn.html"),
	WhOut("file:///android_asset/web/whOut.html"),
	WhQry("file:///android_asset/web/whQry.html"),
	WhPan("file:///android_asset/web/whPan.html"),
	Setting("file:///android_asset/web/setting.html"),
	Back("javascript: dat.back();"),
	Err("file:///android_asset/web/err.html");

	private final String url;
	EmUrl(String u) {
		url = u;
	}

	@Override
	public String toString() {
		return url;
	}
}

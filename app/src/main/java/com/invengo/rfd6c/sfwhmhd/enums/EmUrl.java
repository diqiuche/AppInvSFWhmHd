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
	Back("javascript: dat.back();"),
	SignIn("file:///android_asset/web/s01/signIn.html"),
	Home("file:///android_asset/web/s01/home.html"),
	About("file:///android_asset/web/s01/about.html"),
	WhIn("file:///android_asset/web/s01/whIn.html"),
	WhInRd("file:///android_asset/web/s01/whInRfid.html"),
	WhOut("file:///android_asset/web/s01/whOut.html"),
	WhOutRd("file:///android_asset/web/s01/whOutRfid.html"),
	WhQry("file:///android_asset/web/s01/whQry.html"),
	WhQryRd("file:///android_asset/web/s01/whQryRfid.html"),
	WhPan("file:///android_asset/web/s01/whPan.html"),
	Setting("file:///android_asset/web/s01/setting.html"),
	HomeRd("file:///android_asset/web/s01/homeRfid.html"),
	Err("file:///android_asset/web/s01/err.html");

	private final String url;
	EmUrl(String u) {
		url = u;
	}

	@Override
	public String toString() {
		return url;
	}
}

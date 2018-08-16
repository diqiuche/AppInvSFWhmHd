package com.invengo.rfd6c.sfwhmhd.entity;

import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.invengo.rfd6c.sfwhmhd.Ma;
import com.invengo.rfd6c.sfwhmhd.bean.Inventory;
import com.invengo.rfd6c.sfwhmhd.bean.User;
import com.invengo.rfd6c.sfwhmhd.enums.EmUh;
import com.invengo.rfd6c.sfwhmhd.enums.EmUrl;

import tk.ziniulian.job.qr.EmQrCb;
import tk.ziniulian.job.qr.InfQrListener;
import tk.ziniulian.job.qr.xc2910.Qrd;
import tk.ziniulian.job.rfid.EmCb;
import tk.ziniulian.job.rfid.EmPushMod;
import tk.ziniulian.job.rfid.InfTagListener;
import tk.ziniulian.job.rfid.tag.T6C;
import tk.ziniulian.job.rfid.xc2910.Rd;
import tk.ziniulian.util.Encry;

/**
 * 业务接口
 * Created by 李泽荣 on 2018/7/17.
 */

public class Web {
	private Rd rfd = new Rd();
	private Qrd qr = new Qrd();
	private Gson gson = new Gson();
	private Db ldao = null;
	private Synchro syn = null;
	private Ma ma;

	public Web (Ma m) {
		this.ma = m;
		ldao = new Db(m);
		syn = new Synchro(ldao);
	}

	// 读写器设置
	public void initRd () {
		rfd.setPwd(new byte[] {0x20, 0x26, 0x31, 0x07});
		rfd.setHex(true);
		rfd.setPm(EmPushMod.Catch);
		rfd.setTagListenter(new InfTagListener() {
			@Override
			public void onReadTag(T6C bt, InfTagListener itl) {}

			@Override
			public void onWrtTag(T6C bt, InfTagListener itl) {
				ma.sendUrl(EmUrl.RfWrtOk);
			}

			@Override
			public void cb(EmCb e, String[] args) {
				//Log.i("--rfd--", e.name());
				switch (e) {
					case Scanning:
						ma.sendUrl(EmUrl.RfScaning);
						break;
					case Stopped:
						ma.sendUrl(EmUrl.RfStoped);
						break;
					case ErrWrt:
						ma.sendUrl(EmUrl.RfWrtErr);
						break;
					case ErrConnect:
						ma.sendUrl(EmUrl.Err);
						break;
					case Connected:
						ma.sendUh(EmUh.Connected);
						break;
				}
			}
		});
		rfd.init();
	}

	// 二维码设置
	public void initQr() {
		qr.setQrListenter(new InfQrListener() {
			@Override
			public void onRead(String content) {
				ma.sendUrl(EmUrl.QrOnRead, gson.toJson(content));
			}

			@Override
			public void cb(EmQrCb e, String[] args) {
				//Log.i("--qr--", e.name());
				switch (e) {
					case ErrConnect:
						ma.sendUrl(EmUrl.Err);
						break;
					case Connected:
						ma.sendUh(EmUh.Connected);
						break;
				}
			}
		});
		qr.init();
	}

	public void open() {
		rfd.open();
		qr.open();
	}

	public void close() {
		rfd.close();
		qr.close();
	}

	public void qrDestroy() {
		qr.destroy();
	}

/*------------------- RFID ---------------------*/

	@JavascriptInterface
	public boolean isRfidBusy () {
		return rfd.isBusy();
	}

	@JavascriptInterface
	public void rfidScan() {
		rfd.scan();
	}

	@JavascriptInterface
	public void rfidStop() {
		rfd.stop();
	}

	@JavascriptInterface
	public void rfidWrt (String bankNam, String dat, String tid) {
		rfd.wrt(bankNam, dat, tid);
	}

	@JavascriptInterface
	public String rfidCatchScanning() {
		return rfd.catchScanning();
	}

/*------------------- 二维码 ---------------------*/

	@JavascriptInterface
	public boolean isQrBusy() {
		return qr.isBusy();
	}

	@JavascriptInterface
	public void qrScan() {
		qr.scan();
	}

	@JavascriptInterface
	public void qrStop() {
		qr.stop();
	}

/*------------------- 数据库 ---------------------*/

	@JavascriptInterface
	public String kvGet(String k) {
		return ldao.kvGet(k);
	}

	@JavascriptInterface
	public void kvSet(String k, String v) {
		ldao.kvSet(k, v);
	}

	@JavascriptInterface
	public void kvDel(String k) {
		ldao.kvDel(k);
	}

	// 解析物料标签
	@JavascriptInterface
	public String getTagP(String p, String bn) {
		TagP o = ldao.getTagP(p, bn);
		return gson.toJson(o);
	}

	// 用 TID 解析标签
	@JavascriptInterface
	public String getTag(String tid) {
		String r;
//Log.i("----", tid);
		TagP p = ldao.getPTagByTid(tid);
		if (p == null) {
			TagL l = ldao.getLTagByTid(tid);
			r = gson.toJson(l);
		} else {
			r = gson.toJson(p);
		}
		return r;
	}

	// 入库保存
	@JavascriptInterface
	public boolean savIn (String cod, String bn, String codL, int num, String tid) {
		Inventory it = new Inventory();
		it.setKey(cod + "," + bn);
		it.setNum(num);
		it.setStorageLocation(codL);
		it.setTagCode(tid);

		String user = ldao.kvGet("userId");
		String remark = "";
		return ldao.in(user, remark, it);
	}

	// 入库保存
	@JavascriptInterface
	public boolean savOut (String cod, String bn, int num) {
		Inventory it = new Inventory();
		it.setKey(cod + "," + bn);
		it.setNum(num);

		String user = ldao.kvGet("userId");
		String remark = "";
		return ldao.out(user, remark, it);
	}

/*------------------- 其它 ---------------------*/

	// 设置IP、端口
	@JavascriptInterface
	public boolean setUrl(String ip, String port) {
		return syn.setUrl(ip, port);
	}

	// 同步
	@JavascriptInterface
	public boolean syn () {
		return syn.syn();
	}

	// 用户登录
	@JavascriptInterface
	public String signIn (String uid, String pw) {
		String r = "";
		User u = ldao.getUser(
			uid,
			Encry.getSha1(pw.trim())
		);
		if (u != null) {
			r = gson.toJson(u);
			kvSet("userId", u.getUserId());
			kvSet("user", r);
		}
		return r;
	}

	// 注销用户
	@JavascriptInterface
	public void signOut () {
		ldao.kvDel("user");
	}

/*------------------- 测试 ---------------------*/

//	// 数据同步测试
//	public void testSyn() {
//		syn.pullDat(
//				"[{\"TableName\":\"TB_USER\",\"Version\":1},{\"TableName\":\"TB_CODE\",\"Version\":0},{\"TableN" +
//				"ame\":\"TB_STORAGE_LOCATION\",\"Version\":41},{\"TableName\":\"TB_INVENTORY\",\"Version\":8" +
//				"7},{\"TableName\":\"TB_AREA\",\"Version\":70},{\"TableName\":\"TB_PARTS\",\"Version\":23}]"
//		);
//	}
}

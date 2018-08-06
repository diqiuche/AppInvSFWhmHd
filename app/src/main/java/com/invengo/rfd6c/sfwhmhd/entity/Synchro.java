package com.invengo.rfd6c.sfwhmhd.entity;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.invengo.rfd6c.sfwhmhd.bean.Area;
import com.invengo.rfd6c.sfwhmhd.bean.Code;
import com.invengo.rfd6c.sfwhmhd.bean.Inventory;
import com.invengo.rfd6c.sfwhmhd.bean.Location;
import com.invengo.rfd6c.sfwhmhd.bean.Op;
import com.invengo.rfd6c.sfwhmhd.bean.Parts;
import com.invengo.rfd6c.sfwhmhd.bean.TableVersion;
import com.invengo.rfd6c.sfwhmhd.bean.TableVersionOP;
import com.invengo.rfd6c.sfwhmhd.bean.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tk.ziniulian.util.dao.BaseBean;
import tk.ziniulian.util.net.WebServiceBase;

/**
 * 数据同步
 * Created by 李泽荣 on 2018/7/23.
 */

public class Synchro {
	private Gson gson = new Gson();
	private WebServiceBase ws;
	private Db db;
	private String sn;

	private Type clsInventory = new TypeToken<List<Inventory>>(){}.getType();
	private Type clsPart = new TypeToken<List<Parts>>(){}.getType();
	private Type clsLocation = new TypeToken<List<Location>>(){}.getType();
	private Type clsUser = new TypeToken<List<User>>(){}.getType();
	private Type clsCode = new TypeToken<List<Code>>(){}.getType();
	private Type clsArea = new TypeToken<List<Area>>(){}.getType();
	private Type clsTbv = new TypeToken<List<TableVersion>>(){}.getType();
	private Type clsTvop = new TypeToken<List<TableVersionOP>>(){}.getType();

	private String[] GetTableVersionOpKeys = new String[] {"tableName", "versionsFrom", "versionsTo"};
	private String[] SavaCheckOpKeys = new String[] {"checkInfo", "checkDetailInfo"};
	private String[] SavaPartsOpKeys = new String[] {"opInfo"};

	public Synchro (Db d) {
		this.db = d;
		String po, npc, mn;
		String pp = this.db.kvGet("synUrlIp");
		if (pp == null) {
			pp = "192.168.7.55";
			po = "8004";
			this.sn = "Service.svc";
			npc = "http://tempuri.org/";
			mn = "IService/";
			this.db.kvSet("synUrlIp", pp);
			this.db.kvSet("synUrlPort", po);
			this.db.kvSet("synUrlSrvNam", sn);
			this.db.kvSet("synUrlNpc", npc);
			this.db.kvSet("synUrlMidNam", mn);
		} else {
			po = this.db.kvGet("synUrlPort");
			this.sn = this.db.kvGet("synUrlSrvNam");
			npc = this.db.kvGet("synUrlNpc");
			mn = this.db.kvGet("synUrlMidNam");
		}
		this.ws = new WebServiceBase(getUrl(pp, po), npc, mn);
	}

	private String getUrl (String ip, String port) {
		return "http://" + ip + ":" + port + "/" + sn;
	}

	public boolean setUrl (String ip, String port) {
		db.kvSet("synUrlIp", ip);
		db.kvSet("synUrlPort", port);
		ws.setUrl(getUrl(ip, port));
		return true;
	}

	// 同步
	public boolean syn() {
		boolean r = false;
		// 测试连接
		if ("HelloWorld!".equals(ws.qry("HelloWorld"))) {
			r = pushDat();	// 上传数据
			if (r) {
				r = pullDat();	// 下载数据
			}
		}
		return r;
	}

	// 上传数据
	private boolean pushDat() {
		boolean r = true;

		List<Op> ops = db.getOps();
		if (ops.size() > 0) {
			StringBuilder sb = new StringBuilder();
			List<String> sql = new ArrayList<String>();
			for (Op op : ops) {
				sb.append(op.getPushStr());
				sb.append("-");
				sql.add(op.getDelSql());
			}
			sb.deleteCharAt(sb.length() - 1);

			// 数据上传服务器
			if (ws.qry("SavaPartsOp", SavaPartsOpKeys, new Object[] {sb.toString()}).equals("0")) {
				db.exe(sql);	// 删除本地数据
			} else {
				r = false;
			}
		}

		return r;
	}

	// 下载数据
	private boolean pullDat() {
		return pullDat(ws.qry("GetTableVersion"));
	}
	// 下载数据
//	protected boolean pullDat(String nd) {	// 测试用
	private boolean pullDat(String nd) {
		boolean r = true;

		if (nd != null) {
			List<String> sqls = new ArrayList<String>();
			Map<String, Integer> od = db.getTabVers();	// 旧版本
			List<TableVersion> ls = gson.fromJson(nd, clsTbv);	// 新版本
			TableVersion tv;
			String nam;
			int v;
//Log.i("---", "001");

			// 版本比对
			for (int i = 0; i < ls.size(); i ++) {
				tv = ls.get(i);
				nam = tv.getTableName();
				v = tv.getVersion();
				if (od.containsKey(nam)) {
					if (v > od.get(nam)) {
//						if (testHdat(nam)) {	// 测试用
						if (hdDat(nam, od.get(nam), v)) {
							sqls.add(tv.getSetSql());	// 更新版本
						} else {
							r = false;
							break;
						}

					}
				} else {
//					if (testHdat(nam)) {	// 测试用
					if (hdDat(nam, 0, v)) {
						sqls.add(tv.getAddSql());	// 创建记录
					} else {
						r = false;
						break;
					}
				}
			}
//Log.i("---", "002 , ");
			db.exe((sqls));
		}

		return r;
	}

	// 数据处理
	private boolean hdDat (String tbNam, int vf, int vt) {
		return hdDat(ws.qry("GetTableVersionOP", GetTableVersionOpKeys, new Object[] {tbNam, vf, vt}));
	}
	// 数据处理
	private boolean hdDat (String req) {
		if (req == null) {
			return false;
		} else {
			List<TableVersionOP> ls = gson.fromJson(req, clsTvop);
			List<String> sqls = new ArrayList<String>();
//Log.i("---", "003 , " + tbNam);
//Log.i("---", req);
			for (TableVersionOP tvo : ls) {
				BaseBean bb = null;
				boolean isDel = tvo.getOpType().equals("D");
//Log.i("------" + tvo.getOpType() + "------", tvo.getTableName() + " , " + tvo.getOpTablePK());
//Log.i("---", "." + tvo.getInfo());
				switch (tvo.getTableName()) {
					case "TB_INVENTORY":
						bb = gson.fromJson(tvo.getInfo(), Inventory.class);
						if (isDel && bb == null) {
							bb = new Inventory().setKey(tvo.getOpTablePK());
						}
						break;
					case "TB_PARTS":
						bb = gson.fromJson(tvo.getInfo(), Parts.class);
						if (isDel && bb == null) {
							bb = new Parts().setPartCode(tvo.getOpTablePK());
						}
						break;
					case "TB_STORAGE_LOCATION":
						bb = gson.fromJson(tvo.getInfo(), Location.class);
						if (isDel && bb == null) {
							bb = new Location().setLocationCode(tvo.getOpTablePK());
						}
						break;
					case "TB_AREA":
						bb = gson.fromJson(tvo.getInfo(), Area.class);
						if (isDel && bb == null) {
							bb = new Area().setAreaCode(tvo.getOpTablePK());
						}
						break;
					case "TB_CODE":
						bb = gson.fromJson(tvo.getInfo(), Code.class);
						if (isDel && bb == null) {
							bb = new Code().setKey(tvo.getOpTablePK());
						}
						break;
					case "TB_USER":
						bb = gson.fromJson(tvo.getInfo(), User.class);
						if (isDel && bb == null) {
							bb = new User().setUserId(tvo.getOpTablePK());
						}
						break;
				}
				if (bb != null) {
					sqls.add(bb.getDelSql());
					if (!isDel) {
						sqls.add(bb.getAddSql());
					}
				}
			}
			db.exe((sqls));
//Log.i("---", "004 , ");
			return true;
		}
	}

	// 数据处理的测试
	private boolean testHdat (String nam) {
		String r = null;
		switch (nam) {
			case "TB_INVENTORY":
				Log.i("--- --- ---", "001");
				r = null;
				break;
			case "TB_PARTS":
				Log.i("--- --- ---", "002");
				r = "[{\"TableName\":\"TB_PARTS\",\"TableVersion\":1,\"OpType\":\"A\",\"OpTablePK\":\"979000050201\"" +
						",\"OpTime\":\"2018/07/30 00:00:00\",\"Info\":null},{\"TableName\":\"TB_PARTS\",\"TableVersi" +
						"on\":2,\"OpType\":\"A\",\"OpTablePK\":\"973900050143\",\"OpTime\":\"2018/07/31 14:27:49\",\"In" +
						"fo\":\"{\\\"PartCode\\\":\\\"973900050143\\\",\\\"PartName\\\":\\\"973900050143\\\",\\\"PartSort\\\":\\" +
						"\"15X13\\\",\\\"FactoryCode\\\":\\\"19010006509\\\",\\\"Unit\\\":\\\"件\\\",\\\"Status\\\":\\\"Y\\\",\\\"Remar" +
						"k\\\":\\\"\\\"}\"},{\"TableName\":\"TB_PARTS\",\"TableVersion\":3,\"OpType\":\"A\",\"OpTablePK\":\"9" +
						"79100120000\",\"OpTime\":\"2018/07/31 14:27:49\",\"Info\":\"{\\\"PartCode\\\":\\\"979100120000" +
						"\\\",\\\"PartName\\\":\\\"979100120000\\\",\\\"PartSort\\\":\\\"直径13\\\",\\\"FactoryCode\\\":\\\"2830211" +
						"0008\\\",\\\"Unit\\\":\\\"EA\\\",\\\"Status\\\":\\\"Y\\\",\\\"Remark\\\":\\\"\\\"}\"},{\"TableName\":\"TB_PART" +
						"S\",\"TableVersion\":23,\"OpType\":\"D\",\"OpTablePK\":\"979000050201\",\"OpTime\":\"2018/07/3" +
						"1 14:31:52\",\"Info\":null},{\"TableName\":\"TB_PARTS\",\"TableVersion\":4,\"OpType\":\"A\",\"" +
						"OpTablePK\":\"975300070013\",\"OpTime\":\"2018/07/31 14:27:49\",\"Info\":\"{\\\"PartCode\\\":\\" +
						"\"975300070013\\\",\\\"PartName\\\":\\\"975300070013\\\",\\\"PartSort\\\":\\\"\\\",\\\"FactoryCode\\\":" +
						"\\\"35000072977\\\",\\\"Unit\\\":\\\"件\\\",\\\"Status\\\":\\\"Y\\\",\\\"Remark\\\":\\\"\\\"}\"},{\"TableName\":" +
						"\"TB_PARTS\",\"TableVersion\":5,\"OpType\":\"A\",\"OpTablePK\":\"970200050041\",\"OpTime\":\"20" +
						"18/07/31 14:27:49\",\"Info\":\"{\\\"PartCode\\\":\\\"970200050041\\\",\\\"PartName\\\":\\\"9702000" +
						"50041\\\",\\\"PartSort\\\":\\\"8X7\\\",\\\"FactoryCode\\\":\\\"24752100011\\\",\\\"Unit\\\":\\\"件\\\",\\\"St" +
						"atus\\\":\\\"Y\\\",\\\"Remark\\\":\\\"\\\"}\"},{\"TableName\":\"TB_PARTS\",\"TableVersion\":6,\"OpType" +
						"\":\"A\",\"OpTablePK\":\"970500050174\",\"OpTime\":\"2018/07/31 14:27:49\",\"Info\":\"{\\\"PartC" +
						"ode\\\":\\\"970500050174\\\",\\\"PartName\\\":\\\"970500050174\\\",\\\"PartSort\\\":\\\"32*5\\\",\\\"Fac" +
						"toryCode\\\":\\\"19020035315\\\",\\\"Unit\\\":\\\"件\\\",\\\"Status\\\":\\\"Y\\\",\\\"Remark\\\":\\\"\\\"}\"},{\"" +
						"TableName\":\"TB_PARTS\",\"TableVersion\":7,\"OpType\":\"A\",\"OpTablePK\":\"976100110031\",\"" +
						"OpTime\":\"2018/07/31 14:27:49\",\"Info\":\"{\\\"PartCode\\\":\\\"976100110031\\\",\\\"PartName\\" +
						"\":\\\"976100110031\\\",\\\"PartSort\\\":\\\"\\\",\\\"FactoryCode\\\":\\\"19010031011\\\",\\\"Unit\\\":\\\"" +
						"EA\\\",\\\"Status\\\":\\\"Y\\\",\\\"Remark\\\":\\\"\\\"}\"},{\"TableName\":\"TB_PARTS\",\"TableVersion\":" +
						"8,\"OpType\":\"A\",\"OpTablePK\":\"973600070073\",\"OpTime\":\"2018/07/31 14:27:49\",\"Info\":" +
						"\"{\\\"PartCode\\\":\\\"973600070073\\\",\\\"PartName\\\":\\\"973600070073\\\",\\\"PartSort\\\":\\\"\\\"," +
						"\\\"FactoryCode\\\":\\\"19010001739\\\",\\\"Unit\\\":\\\"EA\\\",\\\"Status\\\":\\\"Y\\\",\\\"Remark\\\":\\\"\\\"" +
						"}\"},{\"TableName\":\"TB_PARTS\",\"TableVersion\":9,\"OpType\":\"A\",\"OpTablePK\":\"978600051" +
						"058\",\"OpTime\":\"2018/07/31 14:27:49\",\"Info\":\"{\\\"PartCode\\\":\\\"978600051058\\\",\\\"Par" +
						"tName\\\":\\\"978600051058\\\",\\\"PartSort\\\":\\\"14X10\\\",\\\"FactoryCode\\\":\\\"19010047199\\\"," +
						"\\\"Unit\\\":\\\"件\\\",\\\"Status\\\":\\\"Y\\\",\\\"Remark\\\":\\\"\\\"}\"},{\"TableName\":\"TB_PARTS\",\"Tabl" +
						"eVersion\":10,\"OpType\":\"A\",\"OpTablePK\":\"972000120019\",\"OpTime\":\"2018/07/31 14:27:" +
						"49\",\"Info\":\"{\\\"PartCode\\\":\\\"972000120019\\\",\\\"PartName\\\":\\\"972000120019\\\",\\\"PartS" +
						"ort\\\":\\\"\\\",\\\"FactoryCode\\\":\\\"35000064007\\\",\\\"Unit\\\":\\\"件\\\",\\\"Status\\\":\\\"Y\\\",\\\"Rem" +
						"ark\\\":\\\"\\\"}\"},{\"TableName\":\"TB_PARTS\",\"TableVersion\":11,\"OpType\":\"A\",\"OpTablePK\"" +
						":\"977000110009\",\"OpTime\":\"2018/07/31 14:27:49\",\"Info\":\"{\\\"PartCode\\\":\\\"977000110" +
						"009\\\",\\\"PartName\\\":\\\"977000110009\\\",\\\"PartSort\\\":\\\"60X10\\\",\\\"FactoryCode\\\":\\\"350" +
						"00059281\\\",\\\"Unit\\\":\\\"件\\\",\\\"Status\\\":\\\"Y\\\",\\\"Remark\\\":\\\"\\\"}\"},{\"TableName\":\"TB_P" +
						"ARTS\",\"TableVersion\":12,\"OpType\":\"A\",\"OpTablePK\":\"970900110005\",\"OpTime\":\"2018/0" +
						"7/31 14:27:49\",\"Info\":\"{\\\"PartCode\\\":\\\"970900110005\\\",\\\"PartName\\\":\\\"97090011000" +
						"5\\\",\\\"PartSort\\\":\\\"15X9\\\",\\\"FactoryCode\\\":\\\"19010024345\\\",\\\"Unit\\\":\\\"件\\\",\\\"Statu" +
						"s\\\":\\\"Y\\\",\\\"Remark\\\":\\\"\\\"}\"},{\"TableName\":\"TB_PARTS\",\"TableVersion\":13,\"OpType\":" +
						"\"A\",\"OpTablePK\":\"972000050127\",\"OpTime\":\"2018/07/31 14:27:50\",\"Info\":\"{\\\"PartCod" +
						"e\\\":\\\"972000050127\\\",\\\"PartName\\\":\\\"972000050127\\\",\\\"PartSort\\\":\\\"53X7\\\",\\\"Facto" +
						"ryCode\\\":\\\"35000072637\\\",\\\"Unit\\\":\\\"件\\\",\\\"Status\\\":\\\"Y\\\",\\\"Remark\\\":\\\"\\\"}\"},{\"Ta" +
						"bleName\":\"TB_PARTS\",\"TableVersion\":14,\"OpType\":\"A\",\"OpTablePK\":\"972000050129\",\"O" +
						"pTime\":\"2018/07/31 14:27:50\",\"Info\":\"{\\\"PartCode\\\":\\\"972000050129\\\",\\\"PartName\\\"" +
						":\\\"972000050129\\\",\\\"PartSort\\\":\\\"\\\",\\\"FactoryCode\\\":\\\"35000072638\\\",\\\"Unit\\\":\\\"件" +
						"\\\",\\\"Status\\\":\\\"Y\\\",\\\"Remark\\\":\\\"\\\"}\"},{\"TableName\":\"TB_PARTS\",\"TableVersion\":15" +
						",\"OpType\":\"A\",\"OpTablePK\":\"977000070002\",\"OpTime\":\"2018/07/31 14:27:50\",\"Info\":\"" +
						"{\\\"PartCode\\\":\\\"977000070002\\\",\\\"PartName\\\":\\\"977000070002\\\",\\\"PartSort\\\":\\\"60X1" +
						"3\\\",\\\"FactoryCode\\\":\\\"19010033328\\\",\\\"Unit\\\":\\\"件\\\",\\\"Status\\\":\\\"Y\\\",\\\"Remark\\\":\\" +
						"\"\\\"}\"},{\"TableName\":\"TB_PARTS\",\"TableVersion\":16,\"OpType\":\"A\",\"OpTablePK\":\"97200" +
						"0050133\",\"OpTime\":\"2018/07/31 14:27:50\",\"Info\":\"{\\\"PartCode\\\":\\\"972000050133\\\",\\" +
						"\"PartName\\\":\\\"972000050133\\\",\\\"PartSort\\\":\\\"16X13\\\",\\\"FactoryCode\\\":\\\"3500004007" +
						"3\\\",\\\"Unit\\\":\\\"EA\\\",\\\"Status\\\":\\\"Y\\\",\\\"Remark\\\":\\\"\\\"}\"},{\"TableName\":\"TB_PARTS\"," +
						"\"TableVersion\":17,\"OpType\":\"A\",\"OpTablePK\":\"972000050269\",\"OpTime\":\"2018/07/31 1" +
						"4:27:50\",\"Info\":\"{\\\"PartCode\\\":\\\"972000050269\\\",\\\"PartName\\\":\\\"972000050269\\\",\\\"" +
						"PartSort\\\":\\\"16X10\\\",\\\"FactoryCode\\\":\\\"35000040075\\\",\\\"Unit\\\":\\\"EA\\\",\\\"Status\\\":" +
						"\\\"Y\\\",\\\"Remark\\\":\\\"\\\"}\"},{\"TableName\":\"TB_PARTS\",\"TableVersion\":18,\"OpType\":\"A\"," +
						"\"OpTablePK\":\"973600050000\",\"OpTime\":\"2018/07/31 14:27:50\",\"Info\":\"{\\\"PartCode\\\":" +
						"\\\"973600050000\\\",\\\"PartName\\\":\\\"973600050000\\\",\\\"PartSort\\\":\\\"38X13\\\",\\\"FactoryC" +
						"ode\\\":\\\"19010001245\\\",\\\"Unit\\\":\\\"个\\\",\\\"Status\\\":\\\"Y\\\",\\\"Remark\\\":\\\"\\\"}\"},{\"Table" +
						"Name\":\"TB_PARTS\",\"TableVersion\":19,\"OpType\":\"A\",\"OpTablePK\":\"970500130032\",\"OpTi" +
						"me\":\"2018/07/31 14:27:50\",\"Info\":\"{\\\"PartCode\\\":\\\"970500130032\\\",\\\"PartName\\\":\\\"" +
						"970500130032\\\",\\\"PartSort\\\":\\\"\\\",\\\"FactoryCode\\\":\\\"19010030954\\\",\\\"Unit\\\":\\\"个\\\"," +
						"\\\"Status\\\":\\\"Y\\\",\\\"Remark\\\":\\\"\\\"}\"},{\"TableName\":\"TB_PARTS\",\"TableVersion\":20,\"O" +
						"pType\":\"A\",\"OpTablePK\":\"975200130003\",\"OpTime\":\"2018/07/31 14:27:50\",\"Info\":\"{\\\"" +
						"PartCode\\\":\\\"975200130003\\\",\\\"PartName\\\":\\\"975200130003\\\",\\\"PartSort\\\":\\\"\\\",\\\"Fa" +
						"ctoryCode\\\":\\\"19010021784\\\",\\\"Unit\\\":\\\"个\\\",\\\"Status\\\":\\\"Y\\\",\\\"Remark\\\":\\\"\\\"}\"},{" +
						"\"TableName\":\"TB_PARTS\",\"TableVersion\":21,\"OpType\":\"A\",\"OpTablePK\":\"978900120160\"" +
						",\"OpTime\":\"2018/07/31 14:27:50\",\"Info\":\"{\\\"PartCode\\\":\\\"978900120160\\\",\\\"PartNam" +
						"e\\\":\\\"978900120160\\\",\\\"PartSort\\\":\\\"\\\",\\\"FactoryCode\\\":\\\"19010052836\\\",\\\"Unit\\\":" +
						"\\\"个\\\",\\\"Status\\\":\\\"Y\\\",\\\"Remark\\\":\\\"\\\"}\"},{\"TableName\":\"TB_PARTS\",\"TableVersion\"" +
						":22,\"OpType\":\"A\",\"OpTablePK\":\"978500130198\",\"OpTime\":\"2018/07/31 14:27:50\",\"Info" +
						"\":\"{\\\"PartCode\\\":\\\"978500130198\\\",\\\"PartName\\\":\\\"978500130198\\\",\\\"PartSort\\\":\\\"\\" +
						"\",\\\"FactoryCode\\\":\\\"19010053047\\\",\\\"Unit\\\":\\\"个\\\",\\\"Status\\\":\\\"Y\\\",\\\"Remark\\\":\\\"\\" +
						"\"}\"}]";
				break;
			case "TB_STORAGE_LOCATION":
				Log.i("--- --- ---", "003");
				r = "[{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":11,\"OpType\":\"A\",\"OpTablePK\":\"W" +
						"01-13-02\",\"OpTime\":\"2018/07/31 15:24:59\",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-13-02\\\"" +
						",\\\"PartAllow\\\":\\\"973600070073\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\" +
						"\"\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":12,\"OpType\":\"A\",\"OpTabl" +
						"ePK\":\"W01-04-01\",\"OpTime\":\"2018/07/31 15:25:30\",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-" +
						"04-01\\\",\\\"PartAllow\\\":\\\"976100110031\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagC" +
						"ode\\\":\\\"E28011602000643168DD08E7\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableV" +
						"ersion\":13,\"OpType\":\"A\",\"OpTablePK\":\"W01-05-03\",\"OpTime\":\"2018/07/31 15:25:59\",\"" +
						"Info\":\"{\\\"LocationCode\\\":\\\"W01-05-03\\\",\\\"PartAllow\\\":\\\"970500050174\\\",\\\"MaxVolum" +
						"n\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"E28011602000742168DD08E7\\\"}\"},{\"TableName" +
						"\":\"TB_STORAGE_LOCATION\",\"TableVersion\":14,\"OpType\":\"A\",\"OpTablePK\":\"W01-13-03\",\"" +
						"OpTime\":\"2018/07/31 15:26:33\",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-13-03\\\",\\\"PartAllo" +
						"w\\\":\\\"975300070013\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"\\\"}\"},{\"Ta" +
						"bleName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":15,\"OpType\":\"A\",\"OpTablePK\":\"W01-0" +
						"5-04\",\"OpTime\":\"2018/07/31 15:27:20\",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-05-04\\\",\\\"P" +
						"artAllow\\\":\\\"970200050041\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"E28" +
						"011602000724168DD08E7\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":26," +
						"\"OpType\":\"U\",\"OpTablePK\":\"W01-03-01\",\"OpTime\":\"2018/07/31 15:53:52\",\"Info\":\"{\\\"L" +
						"ocationCode\\\":\\\"W01-03-01\\\",\\\"PartAllow\\\":\\\"973900050143\\\",\\\"MaxVolumn\\\":0,\\\"IsE" +
						"nable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"E2801160200064C168CF08E7\\\"}\"},{\"TableName\":\"TB_STORA" +
						"GE_LOCATION\",\"TableVersion\":4,\"OpType\":\"A\",\"OpTablePK\":\"W01-03-01\",\"OpTime\":\"201" +
						"8/07/31 15:19:00\",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-03-01\\\",\\\"PartAllow\\\":\\\"973900" +
						"050143\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"E2801160200064C168CF08" +
						"E7\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":25,\"OpType\":\"D\",\"OpTab" +
						"lePK\":\"W01-1\",\"OpTime\":\"2018/07/31 15:34:53\",\"Info\":null},{\"TableName\":\"TB_STORA" +
						"GE_LOCATION\",\"TableVersion\":28,\"OpType\":\"U\",\"OpTablePK\":\"W01-03-01\",\"OpTime\":\"20" +
						"18/07/31 16:07:50\",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-03-01\\\",\\\"PartAllow\\\":\\\"97390" +
						"0050143\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"E2801160200064C168CF0" +
						"8E7\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":29,\"OpType\":\"U\",\"OpTa" +
						"blePK\":\"W01-04-01\",\"OpTime\":\"2018/07/31 16:07:52\",\"Info\":\"{\\\"LocationCode\\\":\\\"W0" +
						"1-04-01\\\",\\\"PartAllow\\\":\\\"976100110031\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"Ta" +
						"gCode\\\":\\\"E28011602000643168DD08E7\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"Tabl" +
						"eVersion\":30,\"OpType\":\"U\",\"OpTablePK\":\"W01-05-01\",\"OpTime\":\"2018/07/31 16:07:59\"" +
						",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-05-01\\\",\\\"PartAllow\\\":\\\"970900110005\\\",\\\"MaxVol" +
						"umn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"E28011602000625168DD08E7\\\"}\"},{\"TableNa" +
						"me\":\"TB_STORAGE_LOCATION\",\"TableVersion\":31,\"OpType\":\"U\",\"OpTablePK\":\"W01-05-02\"" +
						",\"OpTime\":\"2018/07/31 16:08:05\",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-05-02\\\",\\\"PartAl" +
						"low\\\":\\\"978600051058\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"E2801160" +
						"2000620168DD08E7\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":32,\"OpTy" +
						"pe\":\"U\",\"OpTablePK\":\"W01-05-03\",\"OpTime\":\"2018/07/31 16:08:10\",\"Info\":\"{\\\"Locati" +
						"onCode\\\":\\\"W01-05-03\\\",\\\"PartAllow\\\":\\\"970500050174\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable" +
						"\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"E28011602000742168DD08E7\\\"}\"},{\"TableName\":\"TB_STORAGE_LO" +
						"CATION\",\"TableVersion\":33,\"OpType\":\"U\",\"OpTablePK\":\"W01-05-04\",\"OpTime\":\"2018/07" +
						"/31 16:08:16\",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-05-04\\\",\\\"PartAllow\\\":\\\"9702000500" +
						"41\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"E28011602000724168DD08E7\\\"" +
						"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":16,\"OpType\":\"A\",\"OpTablePK" +
						"\":\"W02-06-01\",\"OpTime\":\"2018/07/31 15:28:07\",\"Info\":\"{\\\"LocationCode\\\":\\\"W02-06-" +
						"01\\\",\\\"PartAllow\\\":\\\"970500130032\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode" +
						"\\\":\\\"\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":17,\"OpType\":\"A\",\"Op" +
						"TablePK\":\"W01-06-01\",\"OpTime\":\"2018/07/31 15:28:44\",\"Info\":\"{\\\"LocationCode\\\":\\\"" +
						"W01-06-01\\\",\\\"PartAllow\\\":\\\"973600050000\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"" +
						"TagCode\\\":\\\"\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":18,\"OpType\":" +
						"\"A\",\"OpTablePK\":\"W01-05-05\",\"OpTime\":\"2018/07/31 15:29:15\",\"Info\":\"{\\\"LocationCo" +
						"de\\\":\\\"W01-05-05\\\",\\\"PartAllow\\\":\\\"972000050269\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\" +
						"\"Y\\\",\\\"TagCode\\\":\\\"E2801160200060B168E408E7\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATI" +
						"ON\",\"TableVersion\":19,\"OpType\":\"A\",\"OpTablePK\":\"W01-05-06\",\"OpTime\":\"2018/07/31 " +
						"15:29:42\",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-05-06\\\",\\\"PartAllow\\\":\\\"972000050133\\\"" +
						",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"E2801160200070C168E408E7\\\"}\"}," +
						"{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":20,\"OpType\":\"A\",\"OpTablePK\":\"W" +
						"02-05-01\",\"OpTime\":\"2018/07/31 15:30:20\",\"Info\":\"{\\\"LocationCode\\\":\\\"W02-05-01\\\"" +
						",\\\"PartAllow\\\":\\\"978500130198\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\" +
						"\"\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":21,\"OpType\":\"A\",\"OpTabl" +
						"ePK\":\"W01-08-01\",\"OpTime\":\"2018/07/31 15:30:57\",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-" +
						"08-01\\\",\\\"PartAllow\\\":\\\"972000050129\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagC" +
						"ode\\\":\\\"\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":22,\"OpType\":\"A\"," +
						"\"OpTablePK\":\"W02-04-01\",\"OpTime\":\"2018/07/31 15:31:24\",\"Info\":\"{\\\"LocationCode\\\"" +
						":\\\"W02-04-01\\\",\\\"PartAllow\\\":\\\"978900120160\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\"" +
						",\\\"TagCode\\\":\\\"\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":23,\"OpTyp" +
						"e\":\"A\",\"OpTablePK\":\"W02-03-01\",\"OpTime\":\"2018/07/31 15:31:53\",\"Info\":\"{\\\"Locatio" +
						"nCode\\\":\\\"W02-03-01\\\",\\\"PartAllow\\\":\\\"975200130003\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\" +
						"\":\\\"Y\\\",\\\"TagCode\\\":\\\"\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":24" +
						",\"OpType\":\"A\",\"OpTablePK\":\"W01-08-02\",\"OpTime\":\"2018/07/31 15:32:20\",\"Info\":\"{\\\"" +
						"LocationCode\\\":\\\"W01-08-02\\\",\\\"PartAllow\\\":\\\"977000070002\\\",\\\"MaxVolumn\\\":0,\\\"Is" +
						"Enable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVers" +
						"ion\":5,\"OpType\":\"A\",\"OpTablePK\":\"W01-13-01\",\"OpTime\":\"2018/07/31 15:19:47\",\"Info" +
						"\":\"{\\\"LocationCode\\\":\\\"W01-13-01\\\",\\\"PartAllow\\\":\\\"979100120000\\\",\\\"MaxVolumn\\\":" +
						"0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"Tab" +
						"leVersion\":6,\"OpType\":\"A\",\"OpTablePK\":\"W01-09-01\",\"OpTime\":\"2018/07/31 15:20:32\"" +
						",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-09-01\\\",\\\"PartAllow\\\":\\\"972000050127\\\",\\\"MaxVol" +
						"umn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION" +
						"\",\"TableVersion\":7,\"OpType\":\"A\",\"OpTablePK\":\"W01-05-01\",\"OpTime\":\"2018/07/31 15:" +
						"21:10\",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-05-01\\\",\\\"PartAllow\\\":\\\"970900110005\\\",\\\"" +
						"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"E28011602000625168DD08E7\\\"}\"},{\"T" +
						"ableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":8,\"OpType\":\"A\",\"OpTablePK\":\"W01-0" +
						"7-01\",\"OpTime\":\"2018/07/31 15:21:52\",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-07-01\\\",\\\"P" +
						"artAllow\\\":\\\"977000110009\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"\\\"}" +
						"\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":9,\"OpType\":\"A\",\"OpTablePK\":" +
						"\"W02-01-01\",\"OpTime\":\"2018/07/31 15:23:11\",\"Info\":\"{\\\"LocationCode\\\":\\\"W02-01-01" +
						"\\\",\\\"PartAllow\\\":\\\"972000120019\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\"" +
						":\\\"\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":10,\"OpType\":\"A\",\"OpTa" +
						"blePK\":\"W01-05-02\",\"OpTime\":\"2018/07/31 15:24:10\",\"Info\":\"{\\\"LocationCode\\\":\\\"W0" +
						"1-05-02\\\",\\\"PartAllow\\\":\\\"978600051058\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"Ta" +
						"gCode\\\":\\\"E28011602000620168DD08E7\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"Tabl" +
						"eVersion\":27,\"OpType\":\"U\",\"OpTablePK\":\"W01-03-01\",\"OpTime\":\"2018/07/31 16:05:46\"" +
						",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-03-01\\\",\\\"PartAllow\\\":\\\"973900050143\\\",\\\"MaxVol" +
						"umn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"E2801160200064C168CF08E7\\\"}\"},{\"TableNa" +
						"me\":\"TB_STORAGE_LOCATION\",\"TableVersion\":34,\"OpType\":\"U\",\"OpTablePK\":\"W01-03-01\"" +
						",\"OpTime\":\"2018/07/31 16:19:40\",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-03-01\\\",\\\"PartAl" +
						"low\\\":\\\"973900050143\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"E2801160" +
						"200064C168CF08E7\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":35,\"OpTy" +
						"pe\":\"U\",\"OpTablePK\":\"W01-04-01\",\"OpTime\":\"2018/07/31 16:19:52\",\"Info\":\"{\\\"Locati" +
						"onCode\\\":\\\"W01-04-01\\\",\\\"PartAllow\\\":\\\"976100110031\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable" +
						"\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"E28011602000643168DD08E7\\\"}\"},{\"TableName\":\"TB_STORAGE_LO" +
						"CATION\",\"TableVersion\":36,\"OpType\":\"U\",\"OpTablePK\":\"W01-05-01\",\"OpTime\":\"2018/07" +
						"/31 16:19:58\",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-05-01\\\",\\\"PartAllow\\\":\\\"9709001100" +
						"05\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"E28011602000625168DD08E7\\\"" +
						"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":37,\"OpType\":\"U\",\"OpTablePK" +
						"\":\"W01-05-02\",\"OpTime\":\"2018/07/31 16:20:03\",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-05-" +
						"02\\\",\\\"PartAllow\\\":\\\"978600051058\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode" +
						"\\\":\\\"E28011602000620168DD08E7\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVers" +
						"ion\":38,\"OpType\":\"U\",\"OpTablePK\":\"W01-05-03\",\"OpTime\":\"2018/07/31 16:20:08\",\"Inf" +
						"o\":\"{\\\"LocationCode\\\":\\\"W01-05-03\\\",\\\"PartAllow\\\":\\\"970500050174\\\",\\\"MaxVolumn\\\"" +
						":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"E28011602000742168DD08E7\\\"}\"},{\"TableName\":\"" +
						"TB_STORAGE_LOCATION\",\"TableVersion\":39,\"OpType\":\"U\",\"OpTablePK\":\"W01-05-04\",\"OpT" +
						"ime\":\"2018/07/31 16:20:14\",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-05-04\\\",\\\"PartAllow\\\"" +
						":\\\"970200050041\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"E280116020007" +
						"24168DD08E7\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":40,\"OpType\":\"" +
						"U\",\"OpTablePK\":\"W01-05-05\",\"OpTime\":\"2018/07/31 16:20:23\",\"Info\":\"{\\\"LocationCod" +
						"e\\\":\\\"W01-05-05\\\",\\\"PartAllow\\\":\\\"972000050269\\\",\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"" +
						"Y\\\",\\\"TagCode\\\":\\\"E2801160200060B168E408E7\\\"}\"},{\"TableName\":\"TB_STORAGE_LOCATIO" +
						"N\",\"TableVersion\":41,\"OpType\":\"U\",\"OpTablePK\":\"W01-05-06\",\"OpTime\":\"2018/07/31 1" +
						"6:20:31\",\"Info\":\"{\\\"LocationCode\\\":\\\"W01-05-06\\\",\\\"PartAllow\\\":\\\"972000050133\\\"," +
						"\\\"MaxVolumn\\\":0,\\\"IsEnable\\\":\\\"Y\\\",\\\"TagCode\\\":\\\"E2801160200070C168E408E7\\\"}\"},{" +
						"\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":1,\"OpType\":\"A\",\"OpTablePK\":\"w0-" +
						"1\",\"OpTime\":\"2018/07/31 14:34:53\",\"Info\":null},{\"TableName\":\"TB_STORAGE_LOCATION" +
						"\",\"TableVersion\":2,\"OpType\":\"D\",\"OpTablePK\":\"w0-1\",\"OpTime\":\"2018/07/31 14:35:06" +
						"\",\"Info\":null},{\"TableName\":\"TB_STORAGE_LOCATION\",\"TableVersion\":3,\"OpType\":\"A\"," +
						"\"OpTablePK\":\"W01-1\",\"OpTime\":\"2018/07/31 14:35:42\",\"Info\":null}]";
				break;
			case "TB_AREA":
				Log.i("--- --- ---", "004");
				r = "[{\"TableName\":\"TB_AREA\",\"TableVersion\":1,\"OpType\":\"A\",\"OpTablePK\":\"W01\",\"OpTime\":" +
						"\"2018/07/30 15:35:10\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01\\\",\\\"TagCode\\\":\\\"E2801105200071" +
						"56DE6108D8\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":70,\"OpType\":\"U\",\"OpTablePK" +
						"\":\"W01-02\",\"OpTime\":\"2018/08/01 13:44:12\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-02\\\",\\\"Tag" +
						"Code\\\":\\\"E28011602000602068A208E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":27," +
						"\"OpType\":\"U\",\"OpTablePK\":\"W01-01\",\"OpTime\":\"2018/07/31 15:05:45\",\"Info\":\"{\\\"Area" +
						"Code\\\":\\\"W01-01\\\",\\\"TagCode\\\":\\\"E2801160200070B2689B08E7\\\"}\"},{\"TableName\":\"TB_A" +
						"REA\",\"TableVersion\":3,\"OpType\":\"U\",\"OpTablePK\":\"W01\",\"OpTime\":\"2018/07/30 16:25:" +
						"01\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01\\\",\\\"TagCode\\\":\\\"E280110520007156DE6108D8\\\"}\"},{\"" +
						"TableName\":\"TB_AREA\",\"TableVersion\":50,\"OpType\":\"U\",\"OpTablePK\":\"W01-02\",\"OpTime" +
						"\":\"2018/07/31 16:48:08\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-02\\\",\\\"TagCode\\\":\\\"E28011602" +
						"000602068A208E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":2,\"OpType\":\"A\",\"OpTab" +
						"lePK\":\"W02\",\"OpTime\":\"2018/07/30 15:35:23\",\"Info\":\"{\\\"AreaCode\\\":\\\"W02\\\",\\\"TagCo" +
						"de\\\":\\\"E280110520007116DE6108D8\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":4,\"Op" +
						"Type\":\"U\",\"OpTablePK\":\"W01\",\"OpTime\":\"2018/07/31 13:35:16\",\"Info\":\"{\\\"AreaCode\\\"" +
						":\\\"W01\\\",\\\"TagCode\\\":\\\"E280110520007156DE6108D8\\\"}\"},{\"TableName\":\"TB_AREA\",\"Tab" +
						"leVersion\":6,\"OpType\":\"A\",\"OpTablePK\":\"W01-01\",\"OpTime\":\"2018/07/31 15:01:21\",\"I" +
						"nfo\":\"{\\\"AreaCode\\\":\\\"W01-01\\\",\\\"TagCode\\\":\\\"E2801160200070B2689B08E7\\\"}\"},{\"Tab" +
						"leName\":\"TB_AREA\",\"TableVersion\":49,\"OpType\":\"U\",\"OpTablePK\":\"W01-09\",\"OpTime\":\"" +
						"2018/07/31 16:17:51\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-09\\\",\\\"TagCode\\\":\\\"E28011602000" +
						"74D068F108E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":51,\"OpType\":\"U\",\"OpTable" +
						"PK\":\"W01-03\",\"OpTime\":\"2018/07/31 16:48:14\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-03\\\",\\\"T" +
						"agCode\\\":\\\"E28011602000647168F108E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":5" +
						"2,\"OpType\":\"U\",\"OpTablePK\":\"W01-04\",\"OpTime\":\"2018/07/31 16:48:19\",\"Info\":\"{\\\"Ar" +
						"eaCode\\\":\\\"W01-04\\\",\\\"TagCode\\\":\\\"E28011602000729168F408E7\\\"}\"},{\"TableName\":\"TB" +
						"_AREA\",\"TableVersion\":53,\"OpType\":\"U\",\"OpTablePK\":\"W01-05\",\"OpTime\":\"2018/07/31 " +
						"16:48:25\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-05\\\",\\\"TagCode\\\":\\\"E28011602000624168F408E" +
						"7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":54,\"OpType\":\"U\",\"OpTablePK\":\"W01-06" +
						"\",\"OpTime\":\"2018/07/31 16:48:31\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-06\\\",\\\"TagCode\\\":\\\"" +
						"E28011602000746168F108E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":55,\"OpType\":" +
						"\"U\",\"OpTablePK\":\"W01-07\",\"OpTime\":\"2018/07/31 16:48:37\",\"Info\":\"{\\\"AreaCode\\\":\\\"" +
						"W01-07\\\",\\\"TagCode\\\":\\\"E28011602000628168F408E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"Tab" +
						"leVersion\":56,\"OpType\":\"U\",\"OpTablePK\":\"W01-08\",\"OpTime\":\"2018/07/31 16:48:42\",\"" +
						"Info\":\"{\\\"AreaCode\\\":\\\"W01-08\\\",\\\"TagCode\\\":\\\"E2801160200072E068F408E7\\\"}\"},{\"Ta" +
						"bleName\":\"TB_AREA\",\"TableVersion\":57,\"OpType\":\"U\",\"OpTablePK\":\"W01-09\",\"OpTime\":" +
						"\"2018/07/31 16:48:47\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-09\\\",\\\"TagCode\\\":\\\"E2801160200" +
						"074D068F108E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":58,\"OpType\":\"U\",\"OpTabl" +
						"ePK\":\"W01-10\",\"OpTime\":\"2018/07/31 16:48:52\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-10\\\",\\\"" +
						"TagCode\\\":\\\"E2801160200064D168F108E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":" +
						"59,\"OpType\":\"U\",\"OpTablePK\":\"W01-11\",\"OpTime\":\"2018/07/31 16:48:57\",\"Info\":\"{\\\"A" +
						"reaCode\\\":\\\"W01-11\\\",\\\"TagCode\\\":\\\"E2801160200072D068F408E7\\\"}\"},{\"TableName\":\"T" +
						"B_AREA\",\"TableVersion\":60,\"OpType\":\"U\",\"OpTablePK\":\"W01-12\",\"OpTime\":\"2018/07/31" +
						" 16:49:02\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-12\\\",\\\"TagCode\\\":\\\"E2801160200074B068F108" +
						"E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":61,\"OpType\":\"U\",\"OpTablePK\":\"W01-1" +
						"3\",\"OpTime\":\"2018/07/31 16:49:06\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-13\\\",\\\"TagCode\\\":\\" +
						"\"E28011602000604068EF08E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":62,\"OpType\"" +
						":\"U\",\"OpTablePK\":\"W02-01\",\"OpTime\":\"2018/07/31 16:49:10\",\"Info\":\"{\\\"AreaCode\\\":\\" +
						"\"W02-01\\\",\\\"TagCode\\\":\\\"E28011602000722068F408E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"Ta" +
						"bleVersion\":63,\"OpType\":\"U\",\"OpTablePK\":\"W02-02\",\"OpTime\":\"2018/07/31 16:50:49\"," +
						"\"Info\":\"{\\\"AreaCode\\\":\\\"W02-02\\\",\\\"TagCode\\\":\\\"E2801160200064E068DD08E7\\\"}\"},{\"T" +
						"ableName\":\"TB_AREA\",\"TableVersion\":64,\"OpType\":\"U\",\"OpTablePK\":\"W02-03\",\"OpTime\"" +
						":\"2018/07/31 16:50:55\",\"Info\":\"{\\\"AreaCode\\\":\\\"W02-03\\\",\\\"TagCode\\\":\\\"E280116020" +
						"0070E068E408E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":65,\"OpType\":\"U\",\"OpTab" +
						"lePK\":\"W02-04\",\"OpTime\":\"2018/07/31 16:50:59\",\"Info\":\"{\\\"AreaCode\\\":\\\"W02-04\\\",\\" +
						"\"TagCode\\\":\\\"E28011602000623068EA08E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\"" +
						":66,\"OpType\":\"U\",\"OpTablePK\":\"W02-05\",\"OpTime\":\"2018/07/31 16:51:04\",\"Info\":\"{\\\"" +
						"AreaCode\\\":\\\"W02-05\\\",\\\"TagCode\\\":\\\"E28011602000701068E408E7\\\"}\"},{\"TableName\":\"" +
						"TB_AREA\",\"TableVersion\":67,\"OpType\":\"U\",\"OpTablePK\":\"W02-06\",\"OpTime\":\"2018/07/3" +
						"1 16:51:09\",\"Info\":\"{\\\"AreaCode\\\":\\\"W02-06\\\",\\\"TagCode\\\":\\\"E2801160200060F068E40" +
						"8E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":68,\"OpType\":\"U\",\"OpTablePK\":\"W02-" +
						"07\",\"OpTime\":\"2018/07/31 16:51:13\",\"Info\":\"{\\\"AreaCode\\\":\\\"W02-07\\\",\\\"TagCode\\\":" +
						"\\\"E28011602000726068DD08E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":69,\"OpType" +
						"\":\"U\",\"OpTablePK\":\"W02-08\",\"OpTime\":\"2018/07/31 16:51:18\",\"Info\":\"{\\\"AreaCode\\\":" +
						"\\\"W02-08\\\",\\\"TagCode\\\":\\\"E28011602000644068DD08E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"T" +
						"ableVersion\":31,\"OpType\":\"U\",\"OpTablePK\":\"W01-06\",\"OpTime\":\"2018/07/31 15:09:31\"" +
						",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-06\\\",\\\"TagCode\\\":\\\"E28011602000746168F108E7\\\"}\"},{\"" +
						"TableName\":\"TB_AREA\",\"TableVersion\":32,\"OpType\":\"U\",\"OpTablePK\":\"W01-07\",\"OpTime" +
						"\":\"2018/07/31 15:09:35\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-07\\\",\\\"TagCode\\\":\\\"E28011602" +
						"000628168F408E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":33,\"OpType\":\"U\",\"OpTa" +
						"blePK\":\"W01-08\",\"OpTime\":\"2018/07/31 15:09:38\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-08\\\"," +
						"\\\"TagCode\\\":\\\"E2801160200072E068F408E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion" +
						"\":34,\"OpType\":\"U\",\"OpTablePK\":\"W01-05\",\"OpTime\":\"2018/07/31 15:09:46\",\"Info\":\"{\\" +
						"\"AreaCode\\\":\\\"W01-05\\\",\\\"TagCode\\\":\\\"E28011602000624168F408E7\\\"}\"},{\"TableName\":" +
						"\"TB_AREA\",\"TableVersion\":35,\"OpType\":\"U\",\"OpTablePK\":\"W01-09\",\"OpTime\":\"2018/07/" +
						"31 15:10:21\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-09\\\",\\\"TagCode\\\":\\\"E2801160200074D068F1" +
						"08E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":36,\"OpType\":\"U\",\"OpTablePK\":\"W01" +
						"-10\",\"OpTime\":\"2018/07/31 15:10:27\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-10\\\",\\\"TagCode\\\"" +
						":\\\"E2801160200064D168F108E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":37,\"OpTyp" +
						"e\":\"U\",\"OpTablePK\":\"W01-11\",\"OpTime\":\"2018/07/31 15:10:32\",\"Info\":\"{\\\"AreaCode\\\"" +
						":\\\"W01-11\\\",\\\"TagCode\\\":\\\"E2801160200072D068F408E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"" +
						"TableVersion\":38,\"OpType\":\"U\",\"OpTablePK\":\"W01-12\",\"OpTime\":\"2018/07/31 15:10:37" +
						"\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-12\\\",\\\"TagCode\\\":\\\"E2801160200074B068F108E7\\\"}\"},{" +
						"\"TableName\":\"TB_AREA\",\"TableVersion\":39,\"OpType\":\"U\",\"OpTablePK\":\"W01-13\",\"OpTim" +
						"e\":\"2018/07/31 15:10:40\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-13\\\",\\\"TagCode\\\":\\\"E2801160" +
						"2000604068EF08E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":40,\"OpType\":\"U\",\"OpT" +
						"ablePK\":\"W02-01\",\"OpTime\":\"2018/07/31 15:10:44\",\"Info\":\"{\\\"AreaCode\\\":\\\"W02-01\\\"" +
						",\\\"TagCode\\\":\\\"E28011602000722068F408E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersio" +
						"n\":41,\"OpType\":\"U\",\"OpTablePK\":\"W02-01\",\"OpTime\":\"2018/07/31 15:10:53\",\"Info\":\"{" +
						"\\\"AreaCode\\\":\\\"W02-01\\\",\\\"TagCode\\\":\\\"E28011602000722068F408E7\\\"}\"},{\"TableName\"" +
						":\"TB_AREA\",\"TableVersion\":42,\"OpType\":\"U\",\"OpTablePK\":\"W02-02\",\"OpTime\":\"2018/07" +
						"/31 15:10:57\",\"Info\":\"{\\\"AreaCode\\\":\\\"W02-02\\\",\\\"TagCode\\\":\\\"E2801160200064E068D" +
						"D08E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":43,\"OpType\":\"U\",\"OpTablePK\":\"W0" +
						"2-03\",\"OpTime\":\"2018/07/31 15:11:02\",\"Info\":\"{\\\"AreaCode\\\":\\\"W02-03\\\",\\\"TagCode\\" +
						"\":\\\"E2801160200070E068E408E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":44,\"OpTy" +
						"pe\":\"U\",\"OpTablePK\":\"W02-04\",\"OpTime\":\"2018/07/31 15:11:05\",\"Info\":\"{\\\"AreaCode\\" +
						"\":\\\"W02-04\\\",\\\"TagCode\\\":\\\"E28011602000623068EA08E7\\\"}\"},{\"TableName\":\"TB_AREA\"," +
						"\"TableVersion\":45,\"OpType\":\"U\",\"OpTablePK\":\"W02-05\",\"OpTime\":\"2018/07/31 15:11:0" +
						"8\",\"Info\":\"{\\\"AreaCode\\\":\\\"W02-05\\\",\\\"TagCode\\\":\\\"E28011602000701068E408E7\\\"}\"}," +
						"{\"TableName\":\"TB_AREA\",\"TableVersion\":46,\"OpType\":\"U\",\"OpTablePK\":\"W02-06\",\"OpTi" +
						"me\":\"2018/07/31 15:11:10\",\"Info\":\"{\\\"AreaCode\\\":\\\"W02-06\\\",\\\"TagCode\\\":\\\"E280116" +
						"0200060F068E408E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":47,\"OpType\":\"U\",\"Op" +
						"TablePK\":\"W02-07\",\"OpTime\":\"2018/07/31 15:11:13\",\"Info\":\"{\\\"AreaCode\\\":\\\"W02-07\\" +
						"\",\\\"TagCode\\\":\\\"E28011602000726068DD08E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersi" +
						"on\":48,\"OpType\":\"U\",\"OpTablePK\":\"W02-08\",\"OpTime\":\"2018/07/31 15:11:18\",\"Info\":\"" +
						"{\\\"AreaCode\\\":\\\"W02-08\\\",\\\"TagCode\\\":\\\"E28011602000644068DD08E7\\\"}\"},{\"TableName" +
						"\":\"TB_AREA\",\"TableVersion\":5,\"OpType\":\"U\",\"OpTablePK\":\"W02\",\"OpTime\":\"2018/07/31" +
						" 13:35:32\",\"Info\":\"{\\\"AreaCode\\\":\\\"W02\\\",\\\"TagCode\\\":\\\"E280110520007116DE6108D8\\" +
						"\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":7,\"OpType\":\"A\",\"OpTablePK\":\"W01-02\",\"" +
						"OpTime\":\"2018/07/31 15:01:30\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-02\\\",\\\"TagCode\\\":\\\"E28" +
						"011602000602068A208E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":8,\"OpType\":\"A\"," +
						"\"OpTablePK\":\"W01-03\",\"OpTime\":\"2018/07/31 15:01:44\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-" +
						"03\\\",\\\"TagCode\\\":\\\"E28011602000647168F108E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVe" +
						"rsion\":9,\"OpType\":\"A\",\"OpTablePK\":\"W01-04\",\"OpTime\":\"2018/07/31 15:01:53\",\"Info\"" +
						":\"{\\\"AreaCode\\\":\\\"W01-04\\\",\\\"TagCode\\\":\\\"E28011602000729168F408E7\\\"}\"},{\"TableNa" +
						"me\":\"TB_AREA\",\"TableVersion\":10,\"OpType\":\"A\",\"OpTablePK\":\"W01-05\",\"OpTime\":\"2018" +
						"/07/31 15:02:01\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-05\\\",\\\"TagCode\\\":\\\"E280116020006241" +
						"68F408E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":11,\"OpType\":\"A\",\"OpTablePK\":" +
						"\"W01-06\",\"OpTime\":\"2018/07/31 15:02:08\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-06\\\",\\\"TagCo" +
						"de\\\":\\\"E28011602000746168F108E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":12,\"O" +
						"pType\":\"A\",\"OpTablePK\":\"W01-07\",\"OpTime\":\"2018/07/31 15:02:26\",\"Info\":\"{\\\"AreaCo" +
						"de\\\":\\\"W01-07\\\",\\\"TagCode\\\":\\\"E28011602000628168F408E7\\\"}\"},{\"TableName\":\"TB_ARE" +
						"A\",\"TableVersion\":13,\"OpType\":\"A\",\"OpTablePK\":\"W01-08\",\"OpTime\":\"2018/07/31 15:0" +
						"2:37\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-08\\\",\\\"TagCode\\\":\\\"E2801160200072E068F408E7\\\"}" +
						"\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":14,\"OpType\":\"A\",\"OpTablePK\":\"W01-10\",\"O" +
						"pTime\":\"2018/07/31 15:02:46\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-10\\\",\\\"TagCode\\\":\\\"E280" +
						"1160200064D168F108E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":15,\"OpType\":\"A\"," +
						"\"OpTablePK\":\"W01-09\",\"OpTime\":\"2018/07/31 15:03:12\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-" +
						"09\\\",\\\"TagCode\\\":\\\"E2801160200074D068F108E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVe" +
						"rsion\":16,\"OpType\":\"A\",\"OpTablePK\":\"W01-11\",\"OpTime\":\"2018/07/31 15:03:20\",\"Info" +
						"\":\"{\\\"AreaCode\\\":\\\"W01-11\\\",\\\"TagCode\\\":\\\"E2801160200072D068F408E7\\\"}\"},{\"TableN" +
						"ame\":\"TB_AREA\",\"TableVersion\":17,\"OpType\":\"A\",\"OpTablePK\":\"W01-12\",\"OpTime\":\"201" +
						"8/07/31 15:03:26\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-12\\\",\\\"TagCode\\\":\\\"E2801160200074B" +
						"068F108E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":18,\"OpType\":\"A\",\"OpTablePK\"" +
						":\"W01-13\",\"OpTime\":\"2018/07/31 15:03:36\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-13\\\",\\\"TagC" +
						"ode\\\":\\\"E28011602000604068EF08E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":19,\"" +
						"OpType\":\"A\",\"OpTablePK\":\"W02-01\",\"OpTime\":\"2018/07/31 15:03:59\",\"Info\":\"{\\\"AreaC" +
						"ode\\\":\\\"W02-01\\\",\\\"TagCode\\\":\\\"E28011602000722068F408E7\\\"}\"},{\"TableName\":\"TB_AR" +
						"EA\",\"TableVersion\":20,\"OpType\":\"A\",\"OpTablePK\":\"W02-02\",\"OpTime\":\"2018/07/31 15:" +
						"04:21\",\"Info\":\"{\\\"AreaCode\\\":\\\"W02-02\\\",\\\"TagCode\\\":\\\"E2801160200064E068DD08E7\\\"" +
						"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":21,\"OpType\":\"A\",\"OpTablePK\":\"W02-03\",\"" +
						"OpTime\":\"2018/07/31 15:04:40\",\"Info\":\"{\\\"AreaCode\\\":\\\"W02-03\\\",\\\"TagCode\\\":\\\"E28" +
						"01160200070E068E408E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":22,\"OpType\":\"A\"" +
						",\"OpTablePK\":\"W02-04\",\"OpTime\":\"2018/07/31 15:04:48\",\"Info\":\"{\\\"AreaCode\\\":\\\"W02" +
						"-04\\\",\\\"TagCode\\\":\\\"E28011602000623068EA08E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableV" +
						"ersion\":23,\"OpType\":\"A\",\"OpTablePK\":\"W02-05\",\"OpTime\":\"2018/07/31 15:04:54\",\"Inf" +
						"o\":\"{\\\"AreaCode\\\":\\\"W02-05\\\",\\\"TagCode\\\":\\\"E28011602000701068E408E7\\\"}\"},{\"Table" +
						"Name\":\"TB_AREA\",\"TableVersion\":24,\"OpType\":\"A\",\"OpTablePK\":\"W02-06\",\"OpTime\":\"20" +
						"18/07/31 15:05:00\",\"Info\":\"{\\\"AreaCode\\\":\\\"W02-06\\\",\\\"TagCode\\\":\\\"E2801160200060" +
						"F068E408E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":25,\"OpType\":\"A\",\"OpTablePK" +
						"\":\"W02-07\",\"OpTime\":\"2018/07/31 15:05:06\",\"Info\":\"{\\\"AreaCode\\\":\\\"W02-07\\\",\\\"Tag" +
						"Code\\\":\\\"E28011602000726068DD08E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":26," +
						"\"OpType\":\"A\",\"OpTablePK\":\"W02-08\",\"OpTime\":\"2018/07/31 15:05:11\",\"Info\":\"{\\\"Area" +
						"Code\\\":\\\"W02-08\\\",\\\"TagCode\\\":\\\"E28011602000644068DD08E7\\\"}\"},{\"TableName\":\"TB_A" +
						"REA\",\"TableVersion\":28,\"OpType\":\"U\",\"OpTablePK\":\"W01-02\",\"OpTime\":\"2018/07/31 15" +
						":05:53\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-02\\\",\\\"TagCode\\\":\\\"E28011602000602068A208E7\\" +
						"\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":29,\"OpType\":\"U\",\"OpTablePK\":\"W01-03\"," +
						"\"OpTime\":\"2018/07/31 15:05:56\",\"Info\":\"{\\\"AreaCode\\\":\\\"W01-03\\\",\\\"TagCode\\\":\\\"E2" +
						"8011602000647168F108E7\\\"}\"},{\"TableName\":\"TB_AREA\",\"TableVersion\":30,\"OpType\":\"U" +
						"\",\"OpTablePK\":\"W01-04\",\"OpTime\":\"2018/07/31 15:05:58\",\"Info\":\"{\\\"AreaCode\\\":\\\"W0" +
						"1-04\\\",\\\"TagCode\\\":\\\"E28011602000729168F408E7\\\"}\"}]";
				break;
			case "TB_CODE":
				Log.i("--- --- ---", "005");
				r = null;
				break;
			case "TB_USER":
				Log.i("--- --- ---", "006");
				r = "[{\"TableName\":\"TB_USER\",\"TableVersion\":1,\"OpType\":\"A\",\"OpTablePK\":\"admin\",\"OpTime\":\"2018/07/30 00:00:00\",\"Info\":\"{\"userId\":\"admin\",\"password\":\"7C4A8D09CA3762AF61E59520943DC26494F8941B\",\"userName\":\"管理员\",\"deptCode\":\"01\",\"deptName\":\"四方武汉配件中心\",\"groupCode\":\"01\",\"groupName\":\"仓储部\",\"postCode\":\"01\",\"postName\":\"仓储员\",\"tel\":\"\",\"isEnable\":\"Y\"}\"}]";
				break;
			default:
				Log.i("--- 00 ---", nam);
				break;
		}
		return hdDat(r);
	}

}

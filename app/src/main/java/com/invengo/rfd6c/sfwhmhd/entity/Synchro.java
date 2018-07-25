package com.invengo.rfd6c.sfwhmhd.entity;

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
		boolean r = true;

		String nd = ws.qry("GetTableVersion");
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
						if (hdDat(nam, od.get(nam), v)) {
							sqls.add(tv.getSetSql());	// 更新版本
						} else {
							r = false;
							break;
						}

					}
				} else {
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
		String req = ws.qry("GetTableVersionOP", GetTableVersionOpKeys, new Object[] {tbNam, vf, vt});
		if (req == null) {
			return false;
		} else {
			List<TableVersionOP> ls = gson.fromJson(req, clsTvop);
			List<String> sqls = new ArrayList<String>();
//Log.i("---", "003 , " + tbNam);
			for (TableVersionOP tvo : ls) {
				BaseBean bb = null;
//Log.i("------" + tvo.getOpType() + "------", tvo.getTableName() + " , " + tvo.getOpTablePK());
//Log.i("---", tvo.getInfo());
				switch (tvo.getTableName()) {
					case "TB_INVENTORY":
						bb = gson.fromJson(tvo.getInfo(), Inventory.class);
						if (bb == null) {
							bb = new Inventory().setKey(tvo.getOpTablePK());
						}
						break;
					case "TB_PARTS":
						bb = gson.fromJson(tvo.getInfo(), Parts.class);
						if (bb == null) {
							bb = new Parts().setPartCode(tvo.getOpTablePK());
						}
						break;
					case "TB_STORAGE_LOCATION":
						bb = gson.fromJson(tvo.getInfo(), Location.class);
						if (bb == null) {
							bb = new Location().setLocationCode(tvo.getOpTablePK());
						}
						break;
					case "TB_AREA":
						bb = gson.fromJson(tvo.getInfo(), Area.class);
						if (bb == null) {
							bb = new Area().setAreaCode(tvo.getOpTablePK());
						}
						break;
					case "TB_CODE":
						bb = gson.fromJson(tvo.getInfo(), Code.class);
						if (bb == null) {
							bb = new Code().setKey(tvo.getOpTablePK());
						}
						break;
					case "TB_USER":
						bb = gson.fromJson(tvo.getInfo(), User.class);
						if (bb == null) {
							bb = new User().setUserId(tvo.getOpTablePK());
						}
						break;
				}
				if (bb != null) {
					sqls.add(bb.getDelSql());
					if (!tvo.getOpType().equals("D")) {
						sqls.add(bb.getAddSql());
					}
				}
			}
			db.exe((sqls));
//Log.i("---", "004 , ");
			return true;
		}
	}

}

package com.invengo.rfd6c.sfwhmhd.entity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.invengo.rfd6c.sfwhmhd.bean.Inventory;
import com.invengo.rfd6c.sfwhmhd.bean.Op;
import com.invengo.rfd6c.sfwhmhd.bean.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tk.ziniulian.util.dao.DbLocal;
import tk.ziniulian.util.dao.EmLocalSql;

import static tk.ziniulian.util.Str.meg;

/**
 * 数据库操作
 * Created by 李泽荣 on 2018/7/23.
 */

public class Db extends DbLocal {
	public static SimpleDateFormat datef = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public Db(Context c) {
		super(c);
	}

	// 获取同步版本
	public Map<String, Integer> getTabVers() {
		HashMap<String, Integer> r = new HashMap<String, Integer>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(EmLocalSql.GetTabVers.toString(), null);

		while (c.moveToNext()) {
			r.put(c.getString(0), c.getInt(1));
		}

		c.close();
		db.close();
		return r;
	}

	// 获取单个标签信息
	public TagP getTagP (String pcod, String bn) {
		TagP r = new TagP();
		r.setCod(pcod);
		r.setBn(bn);
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor c = db.rawQuery(meg(EmLocalSql.GetParts.toString(), pcod), null);
		if (c.moveToNext()) {
			r.setNam(c.getString(1));
			r.setPartSort(c.getString(2));
			r.setCodF(c.getString(3));
		}
		c.close();

		c = db.rawQuery(meg(EmLocalSql.GetIt.toString(), pcod, bn), null);
		if (c.moveToNext()) {
			r.setCodL(c.getString(0));
			r.setNum(c.getInt(3));
			r.setTid(c.getString(4));
		}
		c.close();

		db.close();
		return r;
	}

	// 获取单个物料标签信息 By TID
	public TagP getPTagByTid (String tid) {
		TagP r = null;
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor c = db.rawQuery(meg(EmLocalSql.GetItByTid.toString(), tid), null);
		if (c.moveToNext()) {
			r = new TagP();
			String pcod = c.getString(1);
			r.setCodL(c.getString(0));
			r.setCod(pcod);
			r.setBn(c.getString(2));
			r.setNum(c.getInt(3));
			r.setTid(c.getString(4));

			c.close();
			c = db.rawQuery(meg(EmLocalSql.GetParts.toString(), pcod), null);
			if (c.moveToNext()) {
				r.setNam(c.getString(1));
				r.setPartSort(c.getString(2));
				r.setCodF(c.getString(3));
			}
		}

		c.close();
		db.close();
		return r;
	}

	// 获取单个库位标签信息 By TID
	public TagL getLTagByTid (String tid) {
		TagL r = null;
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor c = db.rawQuery(meg(EmLocalSql.GetLocByTid.toString(), tid), null);
		if (c.moveToNext()) {
			r = new TagL();
			r.setCod(c.getString(0));
		}

		c.close();
		db.close();
		return r;
	}

	// 入库
	public boolean in (String user, String remark, Inventory it) {
		List<String> sqls = new ArrayList<String>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(meg(EmLocalSql.GetIt.toString(), it.getPartsCode(), it.getBatchNo()), null);

		sqls.add(meg(EmLocalSql.SavOp.toString(), it.getPartsCode(), "1",
				it.getStorageLocation() + "," +
				user + "," +
				datef.format(new Date()) + "," +
				remark + "," +
				it.getBatchNo() + "," +
				it.getNum() + "," +
				it.getTagCode()
		));
		if (c.moveToNext()) {
			it.setNum(c.getInt(3) + it.getNum());
			sqls.add(it.getSetSql());
		} else {
			sqls.add(it.getAddSql());
		}

		c.close();
		db.close();
		exe(sqls);
		return true;
	}

	// 出库
	public boolean out (String user, String remark, Inventory it) {
		boolean r = false;
		List<String> sqls = new ArrayList<String>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(meg(EmLocalSql.GetIt.toString(), it.getPartsCode(), it.getBatchNo()), null);

		if (c.moveToNext()) {
			sqls.add(meg(EmLocalSql.SavOp.toString(), it.getPartsCode(), "2",
					"," + user + "," +
					datef.format(new Date()) + "," +
					remark + "," +
					it.getBatchNo() + "," +
					it.getNum() + ","
			));
			it.setNum(c.getInt(3) - it.getNum());
			sqls.add(meg(EmLocalSql.Out.toString(), it.getNum() + "", it.getPartsCode(), it.getBatchNo()));
			r = true;
		}

		c.close();
		db.close();
		if (r) {
			exe(sqls);
		}
		return r;
	}

	// 获取上传数据
	public List<Op> getOps () {
		List<Op> r = new ArrayList<Op>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(EmLocalSql.GetOp.toString(), null);

		while(c.moveToNext()) {
			Op o = new Op();
			o.setPartsCode(c.getString(0));
			o.setOpType(c.getString(1));
			o.setInfo(c.getString(2));
			r.add(o);
		}

		c.close();
		db.close();
		return r;
	}

	// 用户登录
	public User getUser (String uid, String pw) {
		User r = null;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(meg(EmLocalSql.GetUser.toString(), uid, pw), null);

		if(c.moveToNext()) {
			r = new User();
			r.setUserId(c.getString(0));
			r.setPassword(c.getString(1));
			r.setUserName(c.getString(2));
			r.setDeptCode(c.getString(3));
			r.setDeptName(c.getString(4));
			r.setGroupCode(c.getString(5));
			r.setGroupName(c.getString(6));
			r.setPostCode(c.getString(7));
			r.setPostName(c.getString(8));
			r.setTel(c.getString(9));
			r.setIsEnable(c.getString(10));
		}

		c.close();
		db.close();
		return r;
	}

}

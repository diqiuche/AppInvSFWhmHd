package tk.ziniulian.util.dao;

/**
 * SQL语句
 * Created by 李泽荣 on 2018/7/19.
 */

public enum EmLocalSql {
	GetTabVers("select * from TableVersion"),
	GetParts("select * from Parts where PartCode='<0>'"),
	GetIt("select * from Inventory where PartsCode='<0>' and BatchNo='<1>'"),
	GetItByTid("select * from Inventory where TagCode='<0>'"),
	GetLocByTid("select * from Location where TagCode='<0>'"),
	SavOp("insert into Op values('<0>', '<1>', '<2>')"),
	GetOp("select * from Op"),
	Out("update Inventory set Num=<0> where PartsCode='<1>' and BatchNo='<2>'"),
	GetUser("select * from User where userId='<0>' and password='<1>'"),

	/***********************************************************/

	// 获取键值对
	KvGet("select v from Bkv where k = '<0>'"),

	// 设置键值对
	KvSet("update Bkv set v = '<1>' where k = '<0>'"),

	// 添加键值对
	KvAdd("insert into Bkv values('<0>', '<1>')"),

	// 删除键值对
	KvDel("delete from Bkv where k = '<0>'");

	private final String sql;
	EmLocalSql(String s) {
		sql = s;
	}

	@Override
	public String toString() {
		return sql;
	}
}

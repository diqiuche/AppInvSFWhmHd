package tk.ziniulian.util.dao;

/**
 * SQL建表语句
 * Created by 李泽荣 on 2018/7/19.
 */

public enum EmLocalCrtSql {
	sdDir("Invengo/Sfwh/DB/"),	// 数据库存储路径

	dbNam("Sfwhdb_1_0_0"),	// 数据库名

	/***********************************************************/

	Op(	// 操作表
		"create table Op(" +	// 表名
		"PartsCode text, " +	// 物料编号
		"OpType text, " +	// 操作类型
		"Info text)"),	// 信息

	Area(	// 层架区域表
		"create table Area(" +	// 表名
		"AreaCode text, " +	// 区域编码
		"TagCode text)"),	// TID

	Code(	// 数据字典表
		"create table Code(" +	// 表名
		"dbType text, " +	// 类型
		"dbCode text, " +	// ID
		"dbName text, " +	// 名称
		"dbTypeBeyond text, " +	// 父类型
		"dbCodeBeyond text)"),	// 父ID

	Inventory(	// 库存表
		"create table Inventory(" +	// 表名
		"StorageLocation text, " +	// 库位编码
		"PartsCode text, " +	// 物料编码
		"BatchNo text, " +	// 批次号
		"Num numeric, " +	// 数量
		"TagCode text)"),	// TID

	Location(	// 库位表
		"create table Location(" +	// 表名
		"LocationCode text, " +	// 库位编码
		"PartAllow text, " +	// 允许存放的物料编号
		"MaxVolumn numeric, " +	// 最大存放数量
		"IsEnable text, " +	// 是否可用
		"TagCode text)"),	// TID

	Parts(	// 物料信息表
		"create table Parts(" +	// 表名
		"PartCode text, " +	// 物料编码
		"PartName text, " +	// 物料名称
		"PartSort text, " +	// 规格型号
		"FactoryCode text, " +	// 主机厂编码
		"Unit text, " +	// 单位
		"Status text, " +	// 物料状态
		"Remark text)"),	// 备注

	User(	// 用户信息表
		"create table User(" +	// 表名
		"userId text, " +	// ID
		"password text, " +	// 密码
		"userName text, " +	// 用户名
		"deptCode text, " +	// 部门ID
		"deptName text, " +	// 部门名称
		"groupCode text, " +	// 班组ID
		"groupName text, " +	// 班组名称
		"postCode text, " +	// 岗位ID
		"postName text, " +	// 岗位名称
		"tel text, " +	// 电话
		"isEnable text)"),	// 是否可用

//	CheckDetail(	// 盘点明细表
//		"create table CheckDetail(" +	// 表名
//		"k text, " +	// 键
//		"v text)"),	// 是否完成

	TbCheck(	// 盘点表
		"create table TbCheck(" +	// 表名
		"CheckCode text, " +	// 盘点单号
		"CheckPartsType text, " +	// 盘点类型
		"AddUser text, " +	// 创建人
		"AddTime text, " +	// 创建时间
		"Remark text, " +	// 备注
		"IsFinish text)"),	// 是否完成

	TableVersion(	// 同步版本信息表
		"create table TableVersion(" +	// 表名
		"TableName text, " +	// 表名称
		"Version numeric)"),	// 表版本

	/***********************************************************/

	Bkv(	// 基本键值对表
		"create table Bkv(" +	// 表名
		"k text primary key not null, " +	// 键
		"v text)");	// 值

	private final String sql;
	EmLocalCrtSql(String s) {
		sql = s;
	}

	@Override
	public String toString() {
		return sql;
	}
}

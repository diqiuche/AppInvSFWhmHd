package tk.ziniulian.util.dao;

/**
 * bean基类
 * Created by 李泽荣 on 2018/7/24.
 */

public abstract class BaseBean {
	public abstract String getAddSql();
	public abstract String getDelSql();
	public abstract String getSetSql();
}

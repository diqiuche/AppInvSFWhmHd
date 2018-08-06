package com.invengo.rfd6c.sfwhmhd.bean;

import tk.ziniulian.util.dao.BaseBean;

import static tk.ziniulian.util.Str.meg;

/**
 * 用户信息
 * Created by 李泽荣 on 2018/7/23.
 */

public class User extends BaseBean {
	private String userId;		// ID
	private String password;	// 密码
	private String userName;	// 用户名
	private String deptCode;	// 部门ID
	private String deptName;	// 部门名称
	private String groupCode;	// 班组ID
	private String groupName;	// 班组名称
	private String postCode;	// 岗位ID
	private String postName;	// 岗位名称
	private String tel;			// 电话
	private String isEnable;	// 是否可用

	public User setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public User setPassword(String password) {
		this.password = password;
		return this;
	}

	public User setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public User setDeptCode(String deptCode) {
		this.deptCode = deptCode;
		return this;
	}

	public User setDeptName(String deptName) {
		this.deptName = deptName;
		return this;
	}

	public User setGroupCode(String groupCode) {
		this.groupCode = groupCode;
		return this;
	}

	public User setGroupName(String groupName) {
		this.groupName = groupName;
		return this;
	}

	public User setPostCode(String postCode) {
		this.postCode = postCode;
		return this;
	}

	public User setPostName(String postName) {
		this.postName = postName;
		return this;
	}

	public User setTel(String tel) {
		this.tel = tel;
		return this;
	}

	public User setIsEnable(String isEnable) {
		this.isEnable = isEnable;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public String getIsEnable() {
		return isEnable;
	}

	public String getTel() {
		return tel;
	}

	public String getPostName() {
		return postName;
	}

	public String getPostCode() {
		return postCode;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public String getDeptName() {
		return deptName;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String getAddSql() {
		String r = "insert into User values('<0>', '<1>', '<2>', '<3>', '<4>', '<5>', '<6>', '<7>', '<8>', '<9>', '<10>')";
		return meg(r, userId, password, userName, deptCode, deptName, groupCode, groupName, postCode, postName, tel, isEnable);
	}

	@Override
	public String getDelSql() {
		String r = "delete from User where userId='<0>'";
		return meg(r, userId);
	}

	@Override
	public String getSetSql() {
		String r = "update User set password='<1>', userName='<2>', deptCode='<3>', deptName='<4>', groupCode='<5>', groupName='<6>', postCode='<7>', postName='<8>', tel='<9>', isEnable='<10>' where userId='<0>'";
		return meg(r, userId, password, userName, deptCode, deptName, groupCode, groupName, postCode, postName, tel, isEnable);
	}
}

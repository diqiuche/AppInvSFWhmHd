function init() {
	if (mn.getUser()) {
		window.location.href = "home.html";
	} else {
		var s = mn.kvGet("userId");
		if (s) {
			uidDom.value = s;
		}
	}
}

dat = {
	signIn: function () {
		var uid = uidDom.value;
		if (uid) {
			var pw = pwDom.value;
			if (pw) {
				if (mn.signIn(uid, pw)) {
					window.location.href = "home.html";
				} else {
					tools.memo("用户名或密码错误！");
				}
			} else {
				tools.memo("密码不能为空！");
			}
		} else {
			tools.memo("用户名不能为空！");
		}
	},

	back: function () {
		window.history.back();
	}
};

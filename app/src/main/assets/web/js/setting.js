function init() {
	dat.flush();
}

dat = {
	// 刷新页面
	flush: function () {
		ipDom.value = mn.kvGet("synUrlIp");
		portDom.value = mn.kvGet("synUrlPort");
	},

	// 保存
	sav: function () {
		var ip = ipDom.value;
		var port = portDom.value;
		if (ip) {
			if (port) {
				if (mn.setUrl (ip, port)) {
					tools.memo("保存成功！正在进行数据同步 ...", -1);
					setTimeout(tools.syn, 100);
				}
			} else {
				tools.memo("端口不能为空！");
			}
		} else {
			tools.memo("IP不能为空！");
		}
	},

	back: function () {
		window.history.back();
	}
};

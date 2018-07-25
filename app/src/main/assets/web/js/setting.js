function init() {
	dat.flush();
}

dat = {
	// 刷新页面
	flush: function () {
		ipDom.value = qr.kvGet("synUrlIp");
		portDom.value = qr.kvGet("synUrlPort");
	},

	// 保存
	sav: function () {
		var ip = ipDom.value;
		var port = portDom.value;
		if (ip) {
			if (port) {
				if (qr.setUrl (ip, port)) {
					tools.memo("保存成功！正在进行数据同步 ...");
					setTimeout(dat.syn, 100);
				}
			} else {
				tools.memo("端口不能为空！");
			}
		} else {
			tools.memo("IP不能为空！");
		}
	},

	syn: function () {
		if (qr.syn ()) {
			tools.memo("完成！", 3000);
		} else {
			tools.memo("数据同步失败！");
		}
	},

	back: function () {
		window.history.back();
	}
};

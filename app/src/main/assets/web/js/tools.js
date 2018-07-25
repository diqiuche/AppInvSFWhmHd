// 通用工具
tools = {
	tid: "",		// TID 缓存

	// 解析标签
	parseTag: function(msg) {
		var r = null;
		tools.tid = "";
		if (msg) {
			var sa = msg.split(",");
			if (sa.length > 1) {
				// 用 类型、物料编号、批次解析
				if (sa[0] === "M") {
					// 物料解析
					r = JSON.parse(qr.getTagP(sa[1], sa[2]));
				} else if (sa[0] === "L") {
					// 库位解析
					r = {
						typ: "L",
						cod: sa[1]
					};
				}
			} else {
				// 用 tid 解析
				tools.tid = msg;
				r = JSON.parse(qr.getTag(msg));
			}
		}
		return r;
	},

	// 信息提示
	memoTim: 0,

	memo: function (msg, tim) {
		if (!tim) {
			tim = 2000;
		}
		tools.memoHid();
		memoDom.innerHTML = msg;
		tools.memoTim = setTimeout(function () {
			memoDom.innerHTML = "";
			tools.memoTim = 0;
		}, tim);
	},

	memoHid: function () {
		if (tools.memoTim) {
			clearTimeout(tools.memoTim);
			memoDom.innerHTML = "";
			tools.memoTim = 0;
		}
	}

};

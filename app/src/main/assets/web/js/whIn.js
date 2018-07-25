function init() {}

qr.hdScan = function (msg) {
	var o = tools.parseTag(msg);
	dat.setTid(tools.tid);
	dat.flushUI(o);
};

dat = {
	cod: "",	// 物料编号
	bn: "",		// 批次号
	codL: "",	// 库位
	num: 0,	// 数量
	tid: "",		// TID

	// 设置物料编号
	setCod: function (s) {
		if (dat.cod !== s) {
			dat.cod = s;
			sidDom.innerHTML = s;
		}
	},

	// 设置批次号
	setBn: function (s) {
		if (dat.bn !== s) {
			dat.bn = s;
			sbnDom.innerHTML = s;
		}
	},

	// 设置TID
	setTid: function (s) {
		if (s && dat.tid !== s) {
			dat.tid = s;
		}
	},

	// 设置库位
	setL: function (c) {
		if (c && dat.codL !== c) {
			if (dat.codL) {
				tools.memo("注：库位变动！", 1000);
			}
			dat.codL = c;
			sloDom.innerHTML = c;
		}
	},

	// 获取数量
	getNum: function () {
		dat.num = numDom.value - 0;
	},

	// 检查数量
	chkNum: function () {
		dat.getNum();
		if (dat.num <= 0) {
			dat.num = 0;
			numDom.value = 0;
		}
	},

	// 添加数量
	addNum: function (n) {
		dat.num += n;
		numDom.value = dat.num;
	},

	// 减少数量
	subNum: function (n) {
		dat.num -= n;
		if (dat.num < 0) {
			dat.num = 0;
		}
		numDom.value = dat.num;
	},

	// 刷新页面
	flushUI: function (o) {
		if (o) {
			switch(o.typ) {
				case "M":
					dat.setCod(o.cod);
					snamDom.innerHTML = o.nam;
					spartDom.innerHTML = o.PartSort;
					smfCodDom.innerHTML = o.codF;
					dat.setBn(o.bn);
					snumDom.innerHTML = o.num;
					numDom.value = "";
					dat.num = 0;
					dat.setL(o.codL);
					dat.setTid(o.tid);
					break;
				case "L":
					dat.setL(o.cod);
					break;
			}
		} else {
			dat.clearUI();
			tools.memo("不可识别的信息！", 1000);
		}
	},

	// 清空页面
	clearUI: function () {
		dat.setCod("");
		snamDom.innerHTML = "";
		spartDom.innerHTML = "";
		smfCodDom.innerHTML = "";
		dat.setBn("");
		snumDom.innerHTML = "";
		numDom.value = "";
		dat.num = 0;
	},

	// 保存
	sav: function () {
		dat.chkNum();
		if (dat.num > 0) {
			if (dat.cod && dat.bn) {
				if (dat.codL) {
					if (qr.savIn(dat.cod, dat.bn, dat.codL, dat.num, dat.tid)) {
						dat.clearUI();
						tools.memo("保存成功！");
					} else {
						tools.memo("失败：保存失败！");
					}
				} else {
					tools.memo("失败：库位不能为空！");
				}
			} else {
				tools.memo("失败：没有物料信息！");
			}
		} else {
			tools.memo("失败：入库数量必须大于零！");
		}
	},

	back: function () {
		window.history.back();
	}
};

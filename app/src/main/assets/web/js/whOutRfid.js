function init() {
	// tools.autoSynSart();
}

rfid.hdScan = function (arr) {
	var t, o;
	for (var i = 0; i < arr.length; i ++) {
		t = arr[i].tid;
		o = JSON.parse(qr.getTag(t));
		if (o !== null) {
			dat.setTid(t);
			dat.flushUI(o);
			rfid.scanStop();
			break;
		}
	}
};

dat = {
	cod: "",	// 物料编号
	bn: "",		// 批次号
	codL: "",	// 库位
	num: 0,	// 数量
	sn: 0,		// 库存数量
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
			if (s && s.indexOf("AUTO") !== 0) {
				sbnDom.innerHTML = s;
			} else {
				sbnDom.innerHTML = "";
			}
		}
	},

	// 设置库存数
	setSn: function (n) {
		if (dat.sn !== n) {
			dat.sn = n;
			if (n) {
				snumDom.innerHTML = n;
			}  else {
				snumDom.innerHTML = "";
			}
		}
	},

	// 设置库位
	setL: function (c) {
		if (dat.codL !== c) {
			dat.codL = c;
			sloDom.innerHTML = c;
		}
	},

	// 设置TID
	setTid: function (s) {
		if (s && dat.tid !== s) {
			dat.tid = s;
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
		if (dat.codL && dat.sn && dat.num > dat.sn) {
			dat.num = dat.sn;
			numDom.value = dat.sn;
		}
	},

	// 添加数量
	addNum: function (n) {
		dat.num += n;
		if (dat.codL && dat.sn && dat.num > dat.sn) {
			dat.num = dat.sn;
		}
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
		dat.clearUI();
		if (o && o.typ === "M") {
			if (o.codL) {
				if (o.num) {
					dat.setCod(o.cod);
					snamDom.innerHTML = o.nam;
					spartDom.innerHTML = o.PartSort;
					smfCodDom.innerHTML = o.codF;
					dat.setBn(o.bn);
					dat.setSn(o.num);
					dat.setL(o.codL);
					dat.setTid(o.tid);
				} else {
					tools.memo("没有库存，不可出货！");
				}
			} else {
				tools.memo("该物料尚未入库！");
			}
		} else {
			tools.memo("不可识别的信息！", 3000);
		}
	},

	// 清空页面
	clearUI: function () {
		dat.setCod("");
		snamDom.innerHTML = "";
		spartDom.innerHTML = "";
		smfCodDom.innerHTML = "";
		dat.setBn("");
		dat.setSn(0);
		dat.setL("");
		numDom.value = "";
		dat.num = 0;
	},

	// 保存
	sav: function () {
		dat.chkNum();
		if (dat.num > 0) {
			if (dat.cod && dat.bn && dat.codL) {
				if (qr.savOut(dat.cod, dat.bn, dat.num)) {
					dat.clearUI();
					tools.memo("保存成功！");
				} else {
					tools.memo("失败：保存失败！");
				}
			} else {
				tools.memo("失败：没有物料信息！");
			}
		} else {
			tools.memo("失败：出库数量必须大于零！");
		}
	},

	back: function () {
		// tools.autoSynStop();
		window.history.back();
	}
};

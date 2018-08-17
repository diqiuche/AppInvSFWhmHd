function init() {}

qr.hdScan = function (msg) {
	var o = tools.parseTag(msg);
	dat.setTid(tools.tid);
	dat.flushUI(o);
};

dat = {
	tid: "",		// TID

	// 设置TID
	setTid: function (s) {
		if (s && dat.tid !== s) {
			dat.tid = s;
		}
	},

	// 设置批次号
	setBn: function (s) {
		if (s && s.indexOf("AUTO") !== 0) {
			var n = s.lastIndexOf(".");
			if (n < 0) {
				sbnDom.innerHTML = s;
			} else {
				sbnDom.innerHTML = s.substring(0, n);
			}
		} else {
			sbnDom.innerHTML = "";
		}
	},

	// 刷新页面
	flushUI: function (o) {
		if (o) {
			switch(o.typ) {
				case "M":
					sidDom.innerHTML = o.cod;
					snamDom.innerHTML = o.nam;
					spartDom.innerHTML = o.PartSort;
					smfCodDom.innerHTML = o.codF;
					dat.setBn(o.bn);
					snumDom.innerHTML = o.num;
					sloDom.innerHTML = o.codL;
					soutDom.className = "in_out";
					loutDom.className = "in_out Lc_nosee";
					dat.show();
					break;
				case "L":
					lidDom.innerHTML = o.cod;
					dat.show(o.cod);
					break;
			}
		} else {
			dat.clearUI();
			tools.memo("不可识别的信息！");
		}
	},

	// 显示库位信息
	show: function (c) {
		if (c) {
			var r, d;
			var s = mn.qryLocDtl(c);
			var o = JSON.parse(mn.qryLocDtl(c));
			tbs.innerHTML = "";
			for (var i = 0; i < o.length; i ++) {
				r = document.createElement("tr");

				// 名称
				d = document.createElement("td");
				d.innerHTML = o[i].nam;
				r.appendChild(d);

				// 数量
				d = document.createElement("td");
				d.innerHTML = o[i].num;
				r.appendChild(d);

				tbs.appendChild(r);
			}
			soutDom.className = "in_out Lc_nosee";
			loutDom.className = "in_out";
		} else {
			soutDom.className = "in_out";
			loutDom.className = "in_out Lc_nosee";
		}
	},

	// 清空页面
	clearUI: function () {
		tools.memoHid();
		sidDom.innerHTML = "";
		snamDom.innerHTML = "";
		spartDom.innerHTML = "";
		smfCodDom.innerHTML = "";
		dat.setBn("");
		snumDom.innerHTML = "";
		sloDom.innerHTML = "";
		dat.tid = "";
		dat.show();
	},

	back: function () {
		window.history.back();
	}
};

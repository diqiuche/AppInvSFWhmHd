qr = {
	scan: function () {
		rfdo.qrScan();
	},
	stop: function () {
		rfdo.qrStop();
	},
	hdScan: function (obj) {
		// console.log(obj);
	},
	isBusy: function () {
		return rfod.isQrBusy();
	},

	getTagP: function (p, bn) {
		return rfdo.getTagP(p, bn);
	},
	getTag: function (tid) {
		return rfdo.getTag(tid);
	},

	savIn: function (cod, bn, codL, num, tid) {
		return rfdo.savIn (cod, bn, codL, num, tid);
	},
	savOut: function (cod, bn, num) {
		return rfdo.savOut (cod, bn, num);
	},

	setUrl: function (ip, port) {
		return rfdo.setUrl(ip, port);
	},
	syn: function () {
		return rfdo.syn();
	},

	kvGet: function (k) {
		return rfdo.kvGet(k);
	},
	kvSet: function (k, v) {
		return rfdo.kvSet(k, v);
	},
	kvDel: function (k) {
		return rfdo.kvDel(k);
	}
};

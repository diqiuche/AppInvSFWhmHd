mn = {
	getTagP: function (p, bn) {
		return rfdo.getTagP(p, bn);
	},
	getTag: function (tid) {
		return rfdo.getTag(tid);
	},
	qryLocDtl: function (codL) {
		return rfdo.qryLocDtl(codL);
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
	signIn: function (uid, pw) {
		return rfdo.signIn(uid, pw);
	},
	signOut: function () {
		rfdo.signOut();
	},
	getUser: function () {
		var u = rfdo.kvGet("user");
		if (u) {
			return JSON.parse(u);
		} else {
			return null;
		}
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

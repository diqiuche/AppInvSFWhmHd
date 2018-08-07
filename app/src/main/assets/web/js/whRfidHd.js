rfid.hdScan = function (arr) {
	var t, o;
	for (var i = 0; i < arr.length; i ++) {
		t = arr[i].tid;
		o = JSON.parse(mn.getTag(t));
		if (o !== null) {
			dat.setTid(t);
			dat.flushUI(o);
			rfid.scanStop();
			break;
		}
	}
};

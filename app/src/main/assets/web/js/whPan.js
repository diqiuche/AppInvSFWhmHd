function init() {}

qr.hdScan = function (msg) {
	cnt.innerHTML += msg;
	cnt.innerHTML += "<br/><br/>";
};

dat = {
	back: function () {
		window.history.back();
	}
};

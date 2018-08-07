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
	}
};

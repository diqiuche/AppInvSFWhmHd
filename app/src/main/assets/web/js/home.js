function init() {
	tools.autoSynSart();
}

dat = {
	signOut: function () {
		qr.signOut();
		window.location.href = "signIn.html";
	},

	back: function () {
		tools.autoSynStop();
		window.history.back();
	}
};

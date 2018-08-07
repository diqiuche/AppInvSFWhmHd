function init() {
	tools.autoSynSart();
}

dat = {
	signOut: function () {
		mn.signOut();
		window.location.href = "signIn.html";
	},

	back: function () {
		tools.autoSynStop();
		window.history.back();
	}
};

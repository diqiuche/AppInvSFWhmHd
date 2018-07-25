function init() {
	tools.autoSynSart();
}

dat = {
	back: function () {
		tools.autoSynStop();
		window.history.back();
	}
};

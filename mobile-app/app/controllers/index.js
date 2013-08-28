var Collector = require('collector');

var log = function (msg) {
	$.log.text = msg;
};

var c = new Collector(30, log);

function doClick(e) {  
    alert($.label.text);
}

function startCollection () {
	if(!c.running) {
		c.start();
		$.startCollector.title = "Stop collection";
	} else {
		c.stop();
		$.startCollector.title = "Collect Samples";
	}
}


$.index.open();

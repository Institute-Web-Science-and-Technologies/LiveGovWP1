var Collector = require('collector')
  , upload = require('upload');

$.log.editable = false;

function log ( msg ) {
	$.log.value += '\n' + msg;
}

var c = new Collector(30, log);
upload(log);


function startCollection () {
	if(!c.running) {
		c.start();
		$.startCollector.title = "Stop collection";
	} else {
		c.stop();
		$.startCollector.title = "Collect Samples";
	}
}

function doUpload() {
	upload();
}


$.index.open();

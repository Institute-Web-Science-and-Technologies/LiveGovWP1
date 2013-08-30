var Collector = require('collector')
  , upload = require('upload');

$.log.editable = false;


function log ( msg ) {	
	// Clear the log if we get more than 50k chars
	if($.log.text.length >= 50000) {
		$.log.text = "Cleared log...";
	}
	$.log.text += '\n' + msg;
	$.logScroll.scrollToBottom();
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

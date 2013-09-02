var Collector = require('collector')
  , upload = require('upload');

$.log.editable = false;

//var intent = Titanium.Android.createServiceIntent( { action: Ti.Android.ACTION_MAIN, url: 'acc.js' } );
// Service should run its code every 2 seconds.
//intent.putExtra('interval', 30);
//var service = Titanium.Android.createService(intent);
//service.start();

function log ( msg ) {	
	// Clear the log if we get more than 50k chars
	if($.log.text.length >= 50000) {
		$.log.text = "Cleared log...";
	}
	$.log.text += '\n' + msg;
	$.logScroll.scrollToBottom();
}

var c = new Collector(log);
upload(c, log);

function startCollection () {
	if(!c.running) {
		c.start();
		$.startCollector.title = "Stop collection";
	} else {
		c.stop();
		$.startCollector.title = "Collect Samples";
	}
}

function doUpload () {
	upload();
}

function sendTag () {
	var tag = $.tag.value;
	c.sendTag(tag);
}


$.index.open();

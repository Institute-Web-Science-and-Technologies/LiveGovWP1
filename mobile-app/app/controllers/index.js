var Collector = require('collector')
  , upload = require('upload');

$.log.text = Ti.Filesystem.tempDirectory;

var c = new Collector(30);

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

function doUpload() {
	upload();
}


$.index.open();

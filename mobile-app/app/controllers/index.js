var Collector = require('collector');

var c = new Collector(30);

function doClick(e) {  
    alert($.label.text);
}

function startCollection () {
	c.start();
}


$.index.open();

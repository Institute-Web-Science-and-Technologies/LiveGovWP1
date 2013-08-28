function Upload() {
	var uploadURL = "http://141.26.71.35:3001";
	var headers = {
		'Content-Type': 'multipart/form-data'
	};
	var uploadFile = Ti.Filesystem.getFile(Ti.Filesystem.tempDirectory, "sensor.log");
	var content = {
		'upfile': uploadFile  
	};
	var xhr = Ti.Network.createHTTPClient({
		onload: function(e) {
			Ti.API.info("Done");
		},
		onerror : function(e) {
        	Ti.API.debug(e.error);
        	alert('error');
    	},
	});
	xhr.open('POST', uploadURL);
	for(var key in headers) {
		xhr.setRequestHeader(key, headers[key]);
	}
	xhr.send(content);
	Ti.API.info("Send");
}

module.exports = Upload;

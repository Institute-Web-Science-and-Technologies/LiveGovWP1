var canUpload = false;
var _log = null;
var _collector = null;

function Upload(fileName) {
	var uploadURL = "http://dhcp129.uni-koblenz.de:8080/backend/upload";
	_log("Uploading samples to " + uploadURL);
	var headers = {
		'id': Ti.Platform.id	
	};
	var uploadFile = Ti.Filesystem.getFile(Ti.Filesystem.tempDirectory, fileName);
	if(! uploadFile.exists() || 0 === uploadFile.size ) {
		_log("Abort upload. File " + fileName + " does not exist.");
		return;
	}
	uploadFile.copy(Ti.Filesystem.tempDirectory + "/" + fileName + "upload.log");
	// Clear the file
	uploadFile.write("");
	var uFile = Ti.Filesystem.getFile(Ti.Filesystem.tempDirectory, fileName + "upload.log");
	_log("Upload samples... Size: " + uFile.size);
	var content = {
		'upfile': uFile.read()
	};
	var xhr = Ti.Network.createHTTPClient({
		onload: function(e) {
			Ti.API.info("Done");
			_log("Upload done. " + fileName);
		},
		onerror : function(e) {
        	Ti.API.debug(e.error);
        	_log("Error while uploading: " + e.error);
    	},
	});
	xhr.open('POST', uploadURL);
	for(var key in headers) {
		xhr.setRequestHeader(key, headers[key]);
	}
	xhr.send(content);
	Ti.API.info("Send");
}

function registerHandler(collector, log) {
	_log = log;
	_collector = collector;
	_log("Register Wifi Handler");
	var currentType = Ti.Network.getNetworkType();
	if(currentType === Ti.Network.NETWORK_WIFI || currentType === Ti.Network.NETWORK_LAN) {
		canUpload = true;
	}
	_log("canUpload: " + canUpload);
	doUpload();
	Ti.Network.addEventListener('change', function (e) {
		if(e.networkType === Ti.Network.NETWORK_WIFI || e.networkType === Ti.Network.NETWORK_LAN) {
			// If we are already uploading we dont want so start again
			if(canUpload) {
				return;
			}
			canUpload = true;
		} else {
			canUpload = false;
		}
		_log("Wifi status changed. canUpload: " + canUpload);
		doUpload();
	});
}

function doUpload() {
	if(!canUpload) {
		_log("Can't upload anymore.");
		return;
	}
	Upload("sensor.log");
	Upload("sensor2.log");
	setTimeout(doUpload, 30000);
}

module.exports = registerHandler;

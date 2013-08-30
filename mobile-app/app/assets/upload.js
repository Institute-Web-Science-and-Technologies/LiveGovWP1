var canUpload = false;
var _log = null;

function Upload() {
	var uploadURL = "http://141.26.71.84:3001";
	_log("Uploading samples to " + uploadURL);
	var headers = {
		'Content-Type': 'multipart/form-data'
	};
	var uploadFile = Ti.Filesystem.getFile(Ti.Filesystem.tempDirectory, "sensor.log");
	if(! uploadFile.exists() || 0 === uploadFile.size ) {
		_log("Abort upload. File does not exist.");
		return;
	}
	uploadFile.copy(Ti.Filesystem.tempDirectory + "/upload.log");
	// Clear the file
	uploadFile.write("");
	var uFile = Ti.Filesystem.getFile(Ti.Filesystem.tempDirectory, "upload.log");
	_log("Upload samples... Size: " + uFile.size);
	var content = {
		'upfile': uFile
	};
	var xhr = Ti.Network.createHTTPClient({
		onload: function(e) {
			Ti.API.info("Done");
			_log("Upload done.");
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

function registerHandler(log) {
	_log = log;
	
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
	Upload();
	setTimeout(doUpload, 30000);
}

module.exports = registerHandler;

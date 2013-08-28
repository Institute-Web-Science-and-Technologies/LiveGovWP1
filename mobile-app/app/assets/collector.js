
// Collector constructor
// FileName: String -> The fileName, which will be used to log the events
// wifiTestInterval: Number -> The number of secons we wait to check if wifi is available 
function Collector ( wifiTestInterval ) {
	this._wifiInterval = wifiTestInterval || 30;
	this._file = Ti.Filesystem.createTempFile();
	this.seperator = ',';
	this._deviceId = Ti.Platform.id;
}


// Starts the sensor collection and registers all callbacks
Collector.prototype.start = function () {
	var self = this;
	
	var accelerometerCallback = self._createAccCallback();
	
	// Register the accelerometer callback
	Ti.Accelerometer.addEventListener('update', accelerometerCallback);
	if (Ti.Platform.name === 'android'){
		Ti.Android.currentActivity.addEventListener('pause', function(e) {
	    	Ti.API.info("removing accelerometer callback on pause");
	    	Ti.Accelerometer.removeEventListener('update', accelerometerCallback);
	    });
	    Ti.Android.currentActivity.addEventListener('resume', function(e) {
	    	Ti.API.info("adding accelerometer callback on resume");
	    	Ti.Accelerometer.addEventListener('update', accelerometerCallback);
	    });
	}	
}

Collector.prototype._createAccCallback = function () {
	var self = this;
	var callback = function ( e ) {
		self.log('ACC', [e.x, e.y, e.z]);
	};
	return callback;
}

Collector.prototype.log = function ( sensorId, sensorValues ) {
	var s = this.seperator;
	var ts = new Date().getTime();
	var msg = sensorId + s + ts + s + this._deviceId;
	for(i = 0; i < sensorValues.length; i++) {
		msg += sensorValues[i];
		if(i !== sensorValues.length)
			msg += " ";
	}
	//this.file.write(msg, true);
	Ti.API.info(msg);
}


module.exports = Collector;

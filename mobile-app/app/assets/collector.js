
// Collector constructor
// wifiTestInterval: Number -> The number of secons we wait to check if wifi is available 
function Collector ( wifiTestInterval, log ) {
	this._wifiInterval = wifiTestInterval || 30;
	this._file = Ti.Filesystem.createTempFile();
	this.seperator = ',';
	this._deviceId = Ti.Platform.id;
	this.running = false;
	this._samples = "";
	this._log = log;
	(this.writeLog())();
}


// Starts the sensor collection and registers all callbacks
Collector.prototype.start = function () {
	var self = this;
	
	this.running = true;
	
	this._log("Start collecting.");
	
	this.accelerometerCallback = this._createAccCallback();
	
	// Register the accelerometer callback
	Ti.Accelerometer.addEventListener('update', this.accelerometerCallback);
	if (Ti.Platform.name === 'android'){
		Ti.Android.currentActivity.addEventListener('pause', function(e) {
	   	Ti.API.info("removing accelerometer callback on pause");
	   	Ti.Accelerometer.removeEventListener('update', self.accelerometerCallback);
	   });
	   Ti.Android.currentActivity.addEventListener('resume', function(e) {
	   	Ti.API.info("adding accelerometer callback on resume");
	   	Ti.Accelerometer.addEventListener('update', self.accelerometerCallback);
	   });
	}
	
	// Register GPS
	Ti.Geolocation.accuracy = Ti.Geolocation.ACCURACY_BEST;
	this.gpsCallback = this._createGPSCallback();
	Ti.Geolocation.addEventListener('location', this.gpsCallback);
};

Collector.prototype.stop = function () {
	this.running = false;
	this._log("Stop collecting.");
	Ti.Accelerometer.removeEventListener('update', this.accelerometerCallback);
	Ti.Geolocation.removeEventListener('location', this.gpsCallback);
};

Collector.prototype._createAccCallback = function () {
	var self = this;
	var callback = function ( e ) {
		self.log('ACC', [e.x, e.y, e.z]);
	};
	return callback;
};

Collector.prototype._createGPSCallback = function () {
	var self = this;
	
	var callback = function ( e ) {
		if(e.success && e.coords) {
			self.log('GPS', [e.coords.longitude, e.coords.latitude, e.coords.altitude], e.coords.timestamp);
		} else {
			Ti.API.info('GPS Data not successfull');
		}
	};
	return callback;
};

Collector.prototype.log = function ( sensorId, sensorValues, ts ) {
	var s = this.seperator;
	if(!ts) {
		ts = new Date().getTime();
	}
	var msg = sensorId + s + ts + s + this._deviceId + s;
	for(i = 0; i < sensorValues.length; i++) {
		msg += sensorValues[i];
		if(i !== sensorValues.length)
			msg += " ";
	}
	this._samples += msg + '\n';
};

Collector.prototype.writeLog = function () {
	
	var self = this;
	
	var callback = function () {
		if("" === self._samples) {
			setTimeout(callback, 1000);
			return;
		}
		var file = Ti.Filesystem.getFile(Ti.Filesystem.tempDirectory, "sensor.log");
		file.write(self._samples, true);
		self._log("Writinig samples to file: " + self._samples.length);
		self._samples = "";
		setTimeout(callback, 1000);
	};
	
	return callback;
};

module.exports = Collector;

function Collector(log) {
    this.seperator = ",";
    this._deviceId = Ti.Platform.id;
    this.running = false;
    this._samples = "";
    this._log = log;
    this.writeLog()();
    this.maxFileSize = 104857600;
    this._currentFile = "sensor.log";
}

Collector.prototype.start = function() {
    this.running = true;
    this._log("Start collecting.");
    this.accelerometerCallback = this._createAccCallback();
    Ti.Accelerometer.addEventListener("update", this.accelerometerCallback);
    Ti.Geolocation.accuracy = Ti.Geolocation.ACCURACY_BEST;
    this.gpsCallback = this._createGPSCallback();
    Ti.Geolocation.addEventListener("location", this.gpsCallback);
};

Collector.prototype.stop = function() {
    this.running = false;
    this._log("Stop collecting.");
    Ti.Accelerometer.removeEventListener("update", this.accelerometerCallback);
    Ti.Geolocation.removeEventListener("location", this.gpsCallback);
};

Collector.prototype._createAccCallback = function() {
    var self = this;
    var callback = function(e) {
        self.log("ACC", [ e.x, e.y, e.z ]);
    };
    return callback;
};

Collector.prototype._createGPSCallback = function() {
    var self = this;
    var callback = function(e) {
        e.success && e.coords ? self.log("GPS", [ e.coords.latitude, e.coords.longitude, e.coords.altitude ], e.coords.timestamp) : Ti.API.info("GPS Data not successfull");
    };
    return callback;
};

Collector.prototype.log = function(sensorId, sensorValues, ts) {
    var s = this.seperator;
    ts || (ts = new Date().getTime());
    var msg = sensorId + s + ts + s + this._deviceId + s;
    for (i = 0; sensorValues.length > i; i++) {
        msg += sensorValues[i];
        i !== sensorValues.length && (msg += " ");
    }
    this._samples += msg + "\n";
};

Collector.prototype.writeLog = function() {
    var self = this;
    var callback = function() {
        if ("" === self._samples) {
            setTimeout(callback, 1e3);
            return;
        }
        var file = Ti.Filesystem.getFile(Ti.Filesystem.tempDirectory, self._currentFile);
        if (file.size >= self.maxFileSize) {
            self._currentFile = "sensor.log" === self._currentFile ? "sensor2.log" : "sensor.log";
            file = Ti.Filesystem.getFile(Ti.Filesystem.tempDirectory, self._currentFile);
            file.write("");
            self._log("File too big. Using file " + self._currentFile);
        }
        file.write(self._samples, true);
        self._log("Writinig samples to file: " + self._samples.length);
        self._samples = "";
        setTimeout(callback, 1e3);
    };
    return callback;
};

Collector.prototype.sendTag = function(tagName) {
    this._log("Adding Tag: " + tagName);
    this.log("TAG", [ '"' + tagName + '"' ]);
};

module.exports = Collector;
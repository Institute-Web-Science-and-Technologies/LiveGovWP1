function Collector(wifiTestInterval) {
    this._wifiInterval = wifiTestInterval || 30;
    this._file = Ti.Filesystem.createTempFile();
    this.seperator = ",";
    this._deviceId = Ti.Platform.id;
}

Collector.prototype.start = function() {
    var self = this;
    var accelerometerCallback = self._createAccCallback();
    Ti.Accelerometer.addEventListener("update", accelerometerCallback);
    Ti.Android.currentActivity.addEventListener("pause", function() {
        Ti.API.info("removing accelerometer callback on pause");
        Ti.Accelerometer.removeEventListener("update", accelerometerCallback);
    });
    Ti.Android.currentActivity.addEventListener("resume", function() {
        Ti.API.info("adding accelerometer callback on resume");
        Ti.Accelerometer.addEventListener("update", accelerometerCallback);
    });
};

Collector.prototype._createAccCallback = function() {
    var self = this;
    var callback = function(e) {
        self.log("ACC", [ e.x, e.y, e.z ]);
    };
    return callback;
};

Collector.prototype.log = function(sensorId, sensorValues) {
    var s = this.seperator;
    var ts = new Date().getTime();
    var msg = sensorId + s + ts + s + this._deviceId;
    for (i = 0; sensorValues.length > i; i++) {
        msg += sensorValues[i];
        i !== sensorValues.length && (msg += " ");
    }
    Ti.API.info(msg);
};

module.exports = Collector;
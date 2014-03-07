/* jshint -W097 */
/* global app:true, console:true, angular:true, d3:true */

'use strict';





app.factory('Data', function () {
	// return a at first empty object. will be populated.
	return {
		'gra': [],
		'acc': [],
		'lac': []
	};
});





app.factory('Trip', function($resource) {
	return $resource('/trips/:trip_id', { trip_id: '@trip_id' });
});





app.factory('Map', function($resource) {
	return $resource(
		'/trips/:trip_id/:what', {trip_id: '@trip_id' }, {
			gps: {method: 'GET', 'params': {what: 'gps'}, isArray: true},
			har: {method: 'GET', 'params': {what: 'har'}, isArray: true}
		}
	);
});





app.factory('Sensor', ['$http', '$q', 'Data', function ($http, $q, Data) {

// get closest starttime index of interpolated ts of sensor data
	var startIdx = function(ts, data) {
		for (var i=0; i<data.length; i++) {
			if (data[i].starttime >= +ts) {
				return i;
			}
		}
	};

	// get closest endtime index of interpolated ts of sensor data
	var endIdx = function(ts, data) {
		for (var i=data.length-1; i>0; i--) {
			if (data[i].endtime <= +ts) {
				return i;
			}
		}
	};

	var sortData = function(data) {
		// data.sort(function(a, b) { return d3.ascending(a.starttime, b.starttime); });
		data.sort(function(a, b) {
			return +a.ts < +b.ts ? -1 : +a.ts > +b.ts ? 1 : 0;
		});
	};

	var factory = {
		query: function(value) {

			if (!value.sensor)   { console.log("Error: sensor missing");  return; }
			if (!value.trip_id)  { console.log("Error: trip_id missing"); return; }

			var trip_id = value.trip_id,
					sensor = value.sensor,
					oldData = Data[sensor],
					startTime = '',
					endTime = '',
					windowSize = '';

			if (value.sel) {
				startTime = "?starttime=" + value.sel[0].getTime();
				endTime   = "?endtime="   + value.sel[1].getTime();
			}

			if (value.windowSize) {
				windowSize = "?window=" + value.windowSize;
			}

			var deferred = $q.defer();

			console.log('asking for ' + sensor + ' data of trip ' + trip_id);
			var queryStartTime = new Date();

			$http({ method: "GET", url: '/trips/' + trip_id + '/' + sensor + '/window' + startTime + endTime + windowSize })
				.success(function (data, status, headers, config) {
					console.log("Success: received " + sensor + " data for trip " + trip_id + " after " + ((new Date() - queryStartTime) / 1000) + " ms");

					data.forEach(function(d) {
						d.ts         = new Date((+d.starttime + +d.endtime) / 2);
						d.starttime  = +d.starttime;
						d.endtime    = +d.endtime;
					});

					if (oldData.length > 0) {

						var start = startIdx(startTime, oldData);
						var range = endIdx(endTime, oldData) - start;

						// Array.prototype.splice.apply(oldData, [start, range].concat(data));
						console.log("VVVoldData" + oldData.length);
						console.log("WWWdata   " + data.length);
						var args = [start, range].concat(data);
						console.log(Array.prototype.splice.apply(oldData, args));
						Array.prototype.splice.apply(oldData, args);
						data = sortData(oldData);

						// console.log(oldData.length);
						// console.log(oldData);
						// console.log(data.length);
						// console.log(data);

						console.log("Success: merged " + sensor + " data for trip " + trip_id + " after " + ((new Date() - queryStartTime) / 1000) + " ms");
					}

					// for (var i=1; i<a.length; i++) { a[i-1].starttime > a[i].starttime ? console.log(a[i-1].starttime + " " + a[i].starttime + " " + i) : ''; }

					console.log("Success: prepared " + sensor + " data for trip " + trip_id + " after " + ((new Date() - queryStartTime) / 1000) + " ms");
					console.log("YYY");
					console.log(data); // FIXME undefined
					deferred.resolve(data);
					console.log("Success: serving " + sensor + " data for trip " + trip_id + " after " + ((new Date() - queryStartTime) / 1000) + " ms");
				}).error(function (data, status, headers, config) {
					console.log("Failure: couldn't get any " + sensor + " data for trip " + trip_id);
					deferred.reject(data);
				});

			return deferred.promise;
		}
	};
	return factory;
}]);





///////////////////////////////////////////////////////////////////////////////
// DEBOUNCE SERVICE
///////////////////////////////////////////////////////////////////////////////

// https://gist.github.com/adamalbrecht/7226278
// https://github.com/angular/angular.js/issues/2690
// https://stackoverflow.com/questions/13320015/how-to-write-a-debounce-service-in-angularjs

// Returns a function, that, as long as it continues to be invoked, will not
// be triggered. The function will be called after it stops being called for
// N milliseconds. If `immediate` is passed, trigger the function on the
// leading edge, instead of the trailing.
app.factory('debounce', function($timeout, $q) {
	return function(func, wait, immediate) {
		var timeout;
		var deferred = $q.defer();
		return function() {
			var context = this, args = arguments;
			var later = function() {
				timeout = null;
				if(!immediate) {
					deferred.resolve(func.apply(context, args));
					deferred = $q.defer();
				}
			};
			var callNow = immediate && !timeout;
			if ( timeout ) {
				$timeout.cancel(timeout);
			}
			timeout = $timeout(later, wait);
			if (callNow) {
				deferred.resolve(func.apply(context,args));
				deferred = $q.defer();
			}
			return deferred.promise;
		};
	};
});
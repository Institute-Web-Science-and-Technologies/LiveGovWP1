/* jshint -W097 */
/* global app:true, console:true, angular:true, d3:true */

'use strict';





app.factory('Data', function () {
	// return a at first empty object. will be populated.
	return {
		// 'gra': [],
		// 'acc': [],
		// 'lac': []
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

	// Usage: si(oldData, data)
	function merge(oldData, data) {
		var len = oldData.length;
		function index() {
			for (var i = 0; i < len; i++) {
				if (oldData[i].starttime >= data[0].starttime) return i;
			}
		}
		
		function range() {
			for (var i = len - 1; i >= 0; --i) {
				if (oldData[i].starttime <= data[data.length - 1].starttime) {
					var idx = index();
					return [idx, i - idx + 1];
				}
			}
		}
		
		Array.prototype.splice.apply(oldData, range().concat(data));
		return oldData;
	}

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
				startTime = value.sel[0].getTime();
				endTime   = value.sel[1].getTime();
			}

			if (value.windowSize) {
				windowSize = value.windowSize;
			}

			var deferred = $q.defer();

			console.log('DB request for ' + sensor + ' data of trip ' + trip_id);
			var queryStartTime = new Date();

			$http({
				method: "GET",
				url: '/trips/' + trip_id + '/' + sensor + '/window',
				params: {
					windowSize: windowSize,
					startTime: startTime,
					endTime: endTime
				}}).success(function (data, status, headers, config) {
					console.log("Success: received " + sensor + " data for trip " + trip_id + " after " + ((new Date() - queryStartTime) / 1000) + " ms");

					data.forEach(function(d) {
						d.ts         = new Date((+d.starttime + (+d.endtime)) / 2);
						d.starttime  = +d.starttime;
						d.endtime    = +d.endtime;
					});

					if (oldData) {
						data = merge(oldData, data);
						console.log("Success: merged " + sensor + " data for trip " + trip_id + " after " + ((new Date() - queryStartTime) / 1000) + " ms");
					}

					console.log("Success: prepared " + sensor + " data for trip " + trip_id + " after " + ((new Date() - queryStartTime) / 1000) + " ms");
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
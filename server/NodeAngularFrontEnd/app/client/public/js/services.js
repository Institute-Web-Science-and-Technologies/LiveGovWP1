/* jshint -W097 */
/* global app:true, console:true, angular:true, d3:true, moment:true, gju:true */

'use strict';

app.factory('Trip', ['$http', '$q', function ($http, $q) {
	return {
		query: function() {
			var deferred = $q.defer();
			var queryStartTime = new Date();

			$http.get('/trips')
			.success(function(data, status, headers, config) {

				// var trips = {};
				// var length = data.length;
				// for (var i = length - 1; i > 0; --i) {
				// 	trips[data[i].trip_id.toString()] = {
				// 		'trip_id': data[i].trip_id,
				// 		'user_id': data[i].user_id,
				// 		'name': data[i].name,
				// 		'duration': +data[i].stop_ts - +data[i].start_ts - 3600000,
				// 		'ts': {
				// 			'start': +data[i].start_ts,
				// 			'stop': +data[i].stop_ts
				// 		}
				// 	};
				// }
				// deferred.resolve(trips);

				data.map(function(d) {
					d.start_ts = parseInt(d.start_ts);
					d.stop_ts = parseInt(d.stop_ts);
					d.duration = parseInt(d.stop_ts) - parseInt(d.start_ts) - 3600000;
				});
				deferred.resolve(data);

			}).error(function(data, status, headers, config) {
				deferred.reject(data);
			});

			return deferred.promise;
		},

		save: function (trip_id, data) {
			$http({
				method: 'POST',
				url: '/trips/' + trip_id,
				data: data
			}).success(function(data, status, headers, config) {
				// $scope.data = data; // XXX
			}).error(function(data, status, headers, config) {
				// $scope.status = status;
			});
		},
	};
}]);

app.factory('Data', ['Geo', 'Sensor', '$rootScope', 'debounce', function (Geo, Sensor, $rootScope) {
	return {
		geo: function () {
			var queryStartTime = new Date();
			Geo.query($rootScope.trip.id)
			.then(function (data) {
				console.log('Success: geo data for trip ' + $rootScope.trip.id + ' ready (' + ((new Date() - queryStartTime) / 1000) + " ms)");
				$rootScope.trips[$rootScope.trip.idx].geo = data;
			}, function (data) {
				console.log("Failure: No geo data for trip " + $rootScope.trip.id);
			});
		},

		sensor: function (sensor, sel, more) {
			var queryStartTime = new Date();
			Sensor.query({
				trip_id: $rootScope.trip.id,
				sensor: sensor,
				oldData: $rootScope.trips[$rootScope.trip.idx][sensor],
				sel: sel,
				more: true
			}).then(function (data) {
				console.log('Success: ' + sensor + ' data for trip ' + $rootScope.trip.id + ' ready (' + ((new Date() - queryStartTime) / 1000) + " ms)");
				$rootScope.trips[$rootScope.trip.idx][sensor] = data;
			}, function (data) {
				console.log("Failure: No " + sensor + " data for trip " + $rootScope.trip.id);
			});
		}
	};
}]);

app.factory('Geo', ['$http', '$q', function ($http, $q) {

	function getMaxOccurrence(array) {
		if (array.length === 0) return null;
		var modeMap = {};
		var maxEl = array[0];
		var maxCount = 1;
		for (var i = 0; i < array.length; i++) {
			var el = array[i];
			if (modeMap[el] === null)	modeMap[el] = 1;
			else modeMap[el]++;
			if (modeMap[el] > maxCount) {
					maxEl = el;
					maxCount = modeMap[el];
			}
		}
		return maxEl;
	}

	// calculate the most popular tag between t0 and t1
	function topActivity(har, t0, t1) {
		return getMaxOccurrence(har.map(function (d) {
			if (d.ts >= t0 && d.ts <= t1) { // get tags between t0 and t1
				return d.tag.replace(/\"/g, ""); // remove quotes
			}}).filter(function (d) { return d; }) // remove undefined
		);
	}

	function calculateDistance(a, b) {
		return gju.pointDistance({
			type: 'Point',
			coordinates: a
		}, {
			type: 'Point',
			coordinates: b
		});
	}

	function getSpeedMode(d) {
		if (d === 0) {
			return "standing";
		} else if (d > 0 && d < 5) {
			return "walking";
		} else if (d > 5 && d < 20) {
			return "running";
		} else if (d > 20) {
			return "driving";
		} else return null;
	}

	function createFeature(coordinates, activity) {
		return {
			'type': 'Feature',
			'geometry': {
				'type': 'LineString',
				'coordinates': coordinates // [g0, g1]
			},
			'properties': {
				'activity': activity
			}
		};
	}

	return {
		query: function(trip_id) {

			var gps = $http.get('/trips/' + trip_id + '/gps');
			var har = $http.get('/trips/' + trip_id + '/har');

			var deferred = $q.defer();
			var queryStartTime = new Date();

			var fc = {
				"type": "FeatureCollection",
				"features": []
			};

			$q.all([gps, har]).then(function(data) {

				var gps = data[0].data;
				var har = data[1].data;

				var gpsLength = gps.length;
				var harLength = har.length;

				var n = 0;

				for (var i = gpsLength - 1; i >= 0; --i) {
					if (gps[i - 1]) {
						var coordinates = [gps[i-1].lonlat.coordinates, gps[i].lonlat.coordinates];
						var activity = topActivity(har, gps[i-1].ts, gps[i].ts);

						if (previousFeature && previousFeature.properties.activity == activity) {
							previousFeature.geometry.coordinates.push(coordinates[1]);
							// previousFeature.properties.t1 = t1; // wrong timestamp?
							// previousFeature.properties.distance += calculateDistance(g0, g1);
							// previousFeature.properties.duration += moment.duration(t1 - t0);
						} else {
							fc.features.push(createFeature(coordinates, activity));
						}

						var previousFeature = fc.features[fc.features.length - 1];
					}
				}
				deferred.resolve(fc);
			});
			return deferred.promise;
		}};
}]);

app.factory('Sensor', ['$http', '$q', '$rootScope', function ($http, $q, $rootScope) {

	function merge(oldData, data) {
		console.log(oldData.length, data.length);
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
		console.log(range());
		Array.prototype.splice.apply(oldData, range().concat(data));
		console.log(oldData.length);
		return oldData;
	}

	var sortData = function(data) {
		// data.sort(function(a, b) { return d3.ascending(a.starttime, b.starttime); });
		data.sort(function(a, b) { return +a.ts < +b.ts ? -1 : +a.ts > +b.ts ? 1 : 0; });
	};

	return {
		query: function(value) {

			if (!value.sensor)   { console.log("Error: sensor missing");  return; }
			if (!value.trip_id)  { console.log("Error: trip_id missing"); return; }
	
			var trip_id = value.trip_id,
					sensor = value.sensor,
					startTime = '',
					endTime = '',
					windowSize;

			if (value.oldData) {
				var oldData = value.oldData;
			}

			if (value.sel) {
				startTime = value.sel[0].getTime();
				endTime   = value.sel[1].getTime();
			}

			if (value.windowSize) {
				windowSize = value.windowSize;
			}
			else if (value.more && $rootScope.trip.brushSize) {
				console.log($rootScope.trip.brushSize);
				windowSize = $rootScope.trip.brushSize + 200;
				console.log(windowSize);
			}

			var deferred = $q.defer();

			$http({
				method: "GET",
				url: '/trips/' + trip_id + '/' + sensor + '/window',
				params: {
					window: windowSize,
					startTime: startTime,
					endTime: endTime
				}}).success(function (data, status, headers, config) {

					data.forEach(function(d) {
						d.ts         = new Date((+d.starttime + (+d.endtime)) / 2);
						d.starttime  = +d.starttime;
						d.endtime    = +d.endtime;
					});

					if (oldData) { data = merge(oldData, data); console.log('merging');}

					deferred.resolve(data);
				}).error(function (data, status, headers, config) {
					console.log("Failure: couldn't get any " + sensor + " data for trip " + trip_id);
					deferred.reject(data);
				});

			return deferred.promise;
		}
	};
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

/* global app:true, console:true, confirm:true */
/* jshint -W097 */
'use strict';

// CHANGEME DRY!

/* CONTROLLERS */

// records tab
app.controller('recCtrl', function ($scope, $rootScope, $routeParams, $location, Data, Trip, Sensor, Map) { // sensorData

	// initialize our shared data scope
	$scope.data = Data;

	// populate the shared data scope with the list of trips
	// TODO refresh button
	if (!$scope.data.trips) {
		$scope.data.trips = Trip.query(); // gets all trips
	}

	// TODO retain table sorting

	// delete a trip by clicking on the button on the far right side
	this.destroy = function (trip) {
		// TODO use modal window instead
		var r = confirm("Are you sure? Permanently delete trip " + trip.trip_id + "?");
		if (r) {
			trip.$delete(); // $delete is provided by $resource service
			$scope.data.trips.splice($scope.data.trips.indexOf(trip), 1);
			console.log("DB QUERY: DELETED TRIP " + trip.trip_id);
		}
	};

	// update a trips name
	this.updateName = function (trip, value) {
		trip.name = value;
		trip.$save();
		console.log("DB QUERY: UPDATED TRIP " + trip.trip_id);
	};

	// click on a trip to globally select it
    this.select = function (trip) {
		$rootScope.trip = trip;


		// $rootScope.fuck = sensorData(trip.trip_id);
		// console.log($rootScope.fuck);


		$routeParams.trip_id = trip.trip_id; // FIXME rewrite and react on $locationParams (if possible)
	};

	// highlight the selected trip by changing css class to 'selected'
	this.is = function(trip) {
		return ($rootScope.trip && $rootScope.trip.trip_id === trip.trip_id) ? true : false;
	};

	// watch the shared scope for our selected trip and start getting all
	// required data to reduce waiting time when switching to another view
	$rootScope.$watch(
		// listener: wait for $rootScope.trip changes
		function () {
			return $rootScope.trip;
		},
		// change handler: do this when $rootScope.trip changed
		function (newTrip, oldTrip) {
			if (newTrip !== oldTrip) {
				if ($rootScope.trip) {
					console.log("TRIP HAS CHANGED. LOADING NEW DATA.");
					$scope.data.acc = Sensor.acc({trip_id: $rootScope.trip.trip_id });
					$scope.data.lac = Sensor.lac({trip_id: $rootScope.trip.trip_id });
					$scope.data.gra = Sensor.gra({trip_id: $rootScope.trip.trip_id });
					$scope.data.gps = Map.gps({trip_id: $rootScope.trip.trip_id });
					$scope.data.har = Map.har({trip_id: $rootScope.trip.trip_id });
				}
			}
		}
	);

});

// raw
/* jshint -W030 */
app.controller('rawCtrl', function ($scope, $rootScope, $location, Sensor, Data, debounce) {

	// redirect to /rec if there's no trip selected
	$rootScope.trip || $location.path('/rec');

	// put shared data object into scope
	$scope.data = Data;

	// helper to get closest starttime of interpolated ts of sensor data
	var starttime = function(ts, data) {
		for (var i=0; i<data.length; i++) {
			if (data[i].starttime >= +ts) {
				return [i, data[i].starttime];
			}
		}
	};

	// helper to get closest endtime of interpolated ts of sensor data
	var endtime = function(ts, data) {
		for (var i=data.length-1; i>0; i--) {
			if (data[i].endtime <= +ts) {
				return [i, data[i].endtime];
			}
		}
	};

	// sort data by timestamp (do we really need that?)
	var sortData = function(data) {
		data.sort(function(a, b) {
			return ((a.ts < b.ts) ? -1 : ((b.ts > a.ts) ? 1 : 0));
		});
	};

	// TODO
	// 1. after data is loaded, compute default (unbrushed) selection range
	// 2. watch $scope.selection for changes (via brush directive)
	// 3. if selection is changed, chart directive should react by zooming functions

	$scope.$watchCollection('data.selection', debounce(function (newVal, oldVal) {
		if (newVal !== oldVal) {
			console.log("RAW:   " + newVal);

			// TODO

			var start = starttime(newVal[0], $scope.data.acc);
			var end = endtime(newVal[1], $scope.data.acc);

			// load more data for a specific sensor
			Sensor.acc({
				trip_id: $rootScope.trip.trip_id,
				startTime: start[1],
				endTime: end[1]
			}).$promise.then(function(data) {
				// merge old and new data and remove obsolete values
				// $scope.data.acc.splice(start, end-start, data);
				var args = [start[0]+1, end[0]-start[0]].concat(data);
				console.log(args);
				Array.prototype.splice.apply($scope.data.acc, args));
				// sortData(Array.prototype.splice.apply($scope.data.acc, args));

				console.log('PUSHED NEW DATA ' + $scope.data.acc.length);
				console.log($scope.data.acc);
			});

			Sensor.lac({
				trip_id: $rootScope.trip.trip_id,
				startTime: start,
				endTime: end
			}).$promise.then(function(data) {
				var args = [start, end-start].concat(data);
				Array.prototype.splice.apply($scope.data.lac, args);
				console.log('PUSHED NEW DATA ' + $scope.data.lac.length);
				// console.log($scope.data.lac);
			});

			Sensor.gra({
				trip_id: $rootScope.trip.trip_id,
				startTime: start,w
				endTime: end
			}).$promise.then(function(data) {
				var args = [start, end-start].concat(data);
				Array.prototype.splice.apply($scope.data.gra, args);
				console.log('PUSHED NEW DATA ' + $scope.data.gra.length);
				// console.log($scope.data.gra);
			});
		}
	}, 1000));

});

// har
/* jshint -W030 */
app.controller('harCtrl', function ($scope, $rootScope, $location, Data) {
	$rootScope.trip || $location.path('/rec');
	$scope.data = Data; // CHANGEME only expose/collect data as needed
});

// navbar
app.controller('navCtrl', function ($scope, $route, Data) {
	$scope.data = Data; // CHANGEME only expose/collect data as needed

	this.is = function(loc) {
		return ($route.current && $route.current.name == loc) ? true : false;
	};

});

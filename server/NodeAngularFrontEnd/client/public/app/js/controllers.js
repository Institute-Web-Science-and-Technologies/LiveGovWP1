/* global app:true, console:true, confirm:true */
/* jshint -W097 */
'use strict';

/* CONTROLLERS */
var a;
// records tab
app.controller('recCtrl', function ($scope, $rootScope, $routeParams, $location, Data, Trip, Sensor, Map) {

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

		// reset brush selection if trip changed
		$scope.data.selection = '';

		$routeParams.trip_id = trip.trip_id; // FIXME rewrite and react on $locationParams (if possible)
	};

	// highlight the selected trip (see rec.jade)
	this.is = function(trip) {
		return ($rootScope.trip && $rootScope.trip.trip_id === trip.trip_id) ? true : false;
	};

	$rootScope.$watch(
		function () {
			return $rootScope.trip;
		},
		function (newTrip, oldTrip) {
			if (newTrip !== oldTrip) {
				if ($rootScope.trip) {
					console.log("TRIP HAS CHANGED. LOADING NEW DATA.");

					$scope.data.gps = Map.gps({trip_id: $rootScope.trip.trip_id });
					$scope.data.har = Map.har({trip_id: $rootScope.trip.trip_id });

					var loadData = function (trip_id, sensor) {
						Sensor.query({
							trip_id: trip_id,
							sensor: sensor
						}).then(function (data) {
							console.log('Success: ' + sensor + ' data for trip ' + $rootScope.trip.trip_id + ' returned');
							$scope.data[sensor] = data;
						}, function (data) {
							console.log("Failure: No " + sensor + " data for trip " + $rootScope.trip.trip_id + " returned");
						});
					};

					loadData($rootScope.trip.trip_id, 'gra');
					loadData($rootScope.trip.trip_id, 'acc');
					loadData($rootScope.trip.trip_id, 'lac');

				}
			}
		}
	);
});

// raw
/* jshint -W030 */
app.controller('rawCtrl', function ($scope, $rootScope, $location, Sensor, Data) {

	$rootScope.trip || $location.path('/rec');
	$scope.data = Data;

	$scope.$watchCollection('data.selection', function (sel, oldSel) {
		if (sel !== oldSel) {
			console.log("BRUSH DETECTED: " + sel);

			var loadMoreData = function(trip_id, sensor, sel) {
				console.log("Old data length: " + $scope.data[sensor].length);
				Sensor.query({
					trip_id: trip_id,
					sensor: sensor,
					sel: sel
				}).then(function (data) {
					console.log('Success: ' + sensor + ' data for trip ' + $rootScope.trip.trip_id + ' returned');
					console.log(data);
					console.log("New data length: " + data.length);
					$scope.data[sensor] = data;
				}, function (data) {
					console.log("Failure: No " + sensor + " data for trip " + $rootScope.trip.trip_id + " returned");
				});
			};

			if ($rootScope.trip) {
				loadMoreData($rootScope.trip.trip_id, 'gra', sel);
				loadMoreData($rootScope.trip.trip_id, 'acc', sel);
				loadMoreData($rootScope.trip.trip_id, 'lac', sel);
			}
		}
	});
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

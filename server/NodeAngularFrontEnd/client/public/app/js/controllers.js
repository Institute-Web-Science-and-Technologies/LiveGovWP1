/* jshint -W097 */
'use strict';

/* global app:true, console:true, confirm:true */

/* CONTROLLERS */

// records tab
app.controller('recCtrl', function ($scope, $rootScope, $location, $modal, Data, Trip, Sensor, Map) {

	// initialize our shared data scope
	$scope.data = Data;

	// populate the shared data scope with the list of trips
	if (!$scope.data.trips) {
		$scope.data.trips = Trip.query(); // get all trips
	}

	// this is for sorting the record table
	// console.log($scope.predicate);
	// console.log($scope.reverse);
	// !$scope.predicate && ($scope.predicate = 'trip_id');
	// !$scope.reverse && ($scope.reverse = true);
	// console.log($scope.predicate);
	// console.log($scope.reverse);

	// delete a trip by clicking on the button on the far right side
	$scope.destroy = function (trip) {
		var r = confirm("Are you sure? Permanently delete trip " + trip.trip_id + "?");
		if (r === true) {
			trip.$delete();
			$scope.data.trips.splice($scope.data.trips.indexOf(trip), 1);
			console.log("DB QUERY: DELETED TRIP " + trip.trip_id);
		}
	};

	// update a trips name
	$scope.updateName = function (trip, value) {
		trip.name = value;
		trip.$save();
		console.log("DB QUERY: UPDATED TRIP " + trip.trip_id);
	};

	// click on a trip to globally select it
	$scope.selectTrip = function (trip) {
		$rootScope.trip = trip;
	};

	// switch to the raw data view by clicking on a table row (UNUSED)
	$scope.changeView = function (view) {
		$rootScope.trip.trip_id = view;
		$location.path(view);
	};

	// if ($location.search('trip')) {
	// $rootScope.trip = $location.search('trip');
	// }

	// watch the shared scope for our selected trip and start getting all
	// required data to reduce waiting time when switching to another view
	$rootScope.$watch(
		// listener: wait for $rootScope.trip changes
		function () {
			return $rootScope.trip;
		},
		// change handler: and then do this
		function (newTrip, oldTrip) {
			if (newTrip !== oldTrip) {
				if ($rootScope.trip) {
					console.log("TRIP HAS CHANGED. LOADING NEW DATA.");
					$scope.data.acc = Sensor.acc({
						trip_id: $rootScope.trip.trip_id
					});
					$scope.data.lac = Sensor.lac({
						trip_id: $rootScope.trip.trip_id
					});
					$scope.data.gra = Sensor.gra({
						trip_id: $rootScope.trip.trip_id
					});
					$scope.data.gps = Map.gps({
						trip_id: $rootScope.trip.trip_id
					});
					$scope.data.har = Map.har({
						trip_id: $rootScope.trip.trip_id
					});
					console.log($rootScope.trip);
					console.log($scope.data);
				}
			}
		}
	);

	// highlight the selected trip by changing css class to 'selected'
	$scope.isSelected = function (trip) {
		if ($rootScope.trip) {
			if (trip.trip_id === $rootScope.trip.trip_id) {
				return 'selected';
			} else {
				return '';
			}
		}
	};

});

// raw
/* jshint -W030 */
app.controller('rawCtrl', function ($scope, $rootScope, $location, Data) {
	$rootScope.trip || $location.path('/rec');
	$scope.data = Data;

	// load more data for a specific sensor
	$scope.loadMoreData = function (sensor) {
		console.log($scope.data.sensor);
	};
});

// har
/* jshint -W030 */
app.controller('harCtrl', function ($scope, $rootScope, $location, Data) {
	$rootScope.trip || $location.path('/rec');
	$scope.data = Data;
});

// navbar
app.controller('navbarCtrl', function ($scope, $rootScope) {
	$rootScope.$watch('trip', function () {
		$scope.trip = $rootScope.trip;
	});
});
/* global app:true, console:true, confirm:true */
/* jshint -W097 */
'use strict';

/* CONTROLLERS */

// returns array index of a trip
var findById = function (source, trip_id) {
	var length = source.length;
  for (var i = length - 1; i >= 0; --i) {
    if (source[i].trip_id === trip_id) {
      return i;
    }
  }
  throw "Couldn't find trip with id: " + trip_id;
};

app.controller('recCtrl', function ($scope, $rootScope, $location, $routeParams, Trip, Data, debounce) {

	// get all trips
	if (!$rootScope.trips) {
		Trip.query().then(function (data) {
			$rootScope.trips = data;
		}, function (data) {
			console.error("Error getting trips");
		});
	}

	// TODO retain table sorting

	// delete a trip
	this.destroy = function (trip) {
		// TODO use modal window instead
		var r = confirm("Are you sure? Permanently delete trip " + trip.trip_id + "?");
		if (r) {
			Trip.delete(trip.trip_id);
			$rootScope.trips.splice($rootScope.trips.indexOf(trip), 1);
			console.log("DB QUERY: DELETED TRIP " + trip.trip_id);
		}
	};

	// update a trips name
	this.updateName = function (trip, data) {
		trip.name = data;
		Trip.save(trip.trip_id, {'name': data});
		console.log("DB QUERY: UPDATED TRIP " + trip.trip_id);
	};

	// globally select a trip
	this.select = function (trip) {
		$rootScope.tripSelected = trip.trip_id;

		// reset brush selection if trip changed
		// $scope.data.selection = '';
		// $routeParams.trip_id = trip.trip_id; // CHANGEME rewrite and react on $locationParams (if possible)
	};

	// change route to loc
	this.to = function(loc) {
		if ($rootScope.tripSelected) {
			$location.path(loc);
		}
	};

	// test if a trip is selected (highlight helper, see rec.jade)
	this.is = function(trip) {
		return ($rootScope.tripSelected === trip.trip_id) ? true : false;
	};

	$rootScope.$watch('tripSelected',
		// FIXME the next function fires twice after switching back vom raw to rec
		// and selecting a different trip. though the db requests are only made
		// once, i $rootScope will be updated twice.
		function (newTrip, oldTrip) {
			if (newTrip !== oldTrip) {
				console.log(newTrip, oldTrip);
				var trip_idx = findById($rootScope.trips, $rootScope.tripSelected);
				console.info("TRIP SELECTED\n", $rootScope.trips[trip_idx]);

				console.log('data req from recCtrl');
				Data.geo($rootScope.tripSelected, trip_idx);
				Data.sensor($rootScope.tripSelected, trip_idx, 'gra');
				Data.sensor($rootScope.tripSelected, trip_idx, 'acc');
				Data.sensor($rootScope.tripSelected, trip_idx, 'lac');
			}
		}
	);
});

app.controller('rawCtrl', function ($scope, $rootScope, $location, Data) {

	$rootScope.tripSelected || $location.path('/rec');
	var trip_idx = findById($rootScope.trips, $rootScope.tripSelected);
	$scope.data = $rootScope.trips[trip_idx];

	$scope.$watchCollection('data.selection', function (sel, oldSel) {
		if (sel !== oldSel) {



			// FIX BRUSH

			if (sel[0].getTime() == sel[1].getTime()) {
				console.warn('THATS NO BRUSH');
				console.log(sel[0].getTime());
				console.log(sel[1].getTime());
				$scope.data.selection = [];
				return;
			}

			console.log(sel);
			console.log("BRUSH DETECTED: " + sel);




			// SHOULD ONLY BE TRIGGERED TO RELOAD DATA, SO SKIP GEO
			if ($rootScope.tripSelected) {
				console.log('data req from rawCtrl');
				// Data.geo($rootScope.tripSelected, trip_idx);
				Data.sensor($rootScope.tripSelected, trip_idx, 'gra', sel);
				Data.sensor($rootScope.tripSelected, trip_idx, 'acc', sel);
				Data.sensor($rootScope.tripSelected, trip_idx, 'lac', sel);
			}
		}
	});
});

app.controller('harCtrl', function ($scope, $rootScope, $location) {
	$rootScope.tripSelected || $location.path('/rec');
	var trip_idx = findById($rootScope.trips, $rootScope.tripSelected);
	$scope.data = $scope.data[trip_idx];
});

// navbar
app.controller('navCtrl', function ($scope, $rootScope, $route) {
	if ($rootScope.tripSelected) {
		var trip_idx = findById($rootScope.trips, $rootScope.tripSelected);
		$scope.data = $scope.data[trip_idx];
	}

	this.is = function(loc) {
		return ($route.current && $route.current.name == loc) ? true : false;
	};
});

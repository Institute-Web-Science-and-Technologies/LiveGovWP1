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
			throw("Error getting trips!");
		});
	}

	// select a trip and calculate it's array index in $scope.trips
	this.select = function (trip) {
		if (!$scope.trips) return;
		if ($rootScope.trip && $rootScope.trip.id === trip.trip_id) return;

		$rootScope.trip = {
			'id': trip.trip_id || undefined,
			'idx': findById($rootScope.trips, trip.trip_id) || undefined,
			'brush': undefined,
			'domain': undefined
		}
	};

	// reset previously drawn brushes if we return to the recordings view
	if ($rootScope.trip && $rootScope.trip.id && $rootScope.trip.brush) {
		$rootScope.trip.brush = undefined;
	}

	// TODO retain and display table sorting

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

	// change route to loc
	this.to = function(loc) {
		if ($rootScope.trip) {
			$location.path(loc);
		}
	};

	// test if a trip is selected (highlight helper, see rec.jade)
	this.is = function(trip) {
		if ($rootScope.trip) {
			return ($rootScope.trip.id === trip.trip_id) ? true : false;
		}
	};

	$rootScope.$watch(
		function() {
			return $rootScope.trip === undefined;
		},
		// FIXME the next function fires twice after switching back vom raw to rec
		// and selecting a different trip. though the db requests are only made
		// once, i $rootScope will be updated twice.
		function (newTrip, oldTrip) {
			if (newTrip !== oldTrip) {
				console.info("TRIP SELECTED\n", $rootScope.trips[$rootScope.trip.idx]);
				Data.geo();
				Data.sensor('gra');
				Data.sensor('acc');
				Data.sensor('lac');
			}
		}
	);
});

app.controller('rawCtrl', function ($scope, $rootScope, $location, Data) {

	if (!$rootScope.trip) {
		$location.path('/rec')
		return;
	}

	$scope.data = $rootScope.trips[$rootScope.trip.idx];

	$rootScope.$watch('trip.brush', function (sel, oldSel) {
		if (sel && sel !== oldSel) {
			Data.sensor('gra', sel);
			Data.sensor('acc', sel);
			Data.sensor('lac', sel);
		}
	});
});

app.controller('harCtrl', function ($scope, $rootScope, $location) {
	$rootScope.trip || $location.path('/rec');
	$scope.data = $scope.data[$rootScope.trip.idx];
});

// navbar
app.controller('navCtrl', function ($scope, $rootScope, $route, Data) {
	if ($rootScope.trip) {
		$scope.data = $scope.data[$rootScope.trip.idx];
		console.log($scope.data);
	}

	this.is = function(loc) {
		return ($route.current && $route.current.name == loc) ? true : false;
	};

	this.deselectTrip = function () {
		delete $rootScope.trip;
		// $rootScope.trip.id = undefined;
		// $rootScope.trip.idx = undefined;
		// $rootScope.trip.brush = undefined;
		// $rootScope.trip.domain = undefined;
	}

	this.clearBrush = function () {
		$rootScope.trip.brush = undefined;
	}

	this.loadMoreData = function(sensor) {
		Data.sensor('gra', $rootScope.trip.brush, 'more');
		Data.sensor('acc', $rootScope.trip.brush, 'more');
		Data.sensor('lac', $rootScope.trip.brush, 'more');
		$scope.digest();
		// $scope.apply();
	}


});

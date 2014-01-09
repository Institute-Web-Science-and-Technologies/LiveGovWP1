'use strict';

/* CONTROLLERS */

app.controller('recCtrl', function($scope, Trip) {
  $scope.trips = Trip.index();
  $scope.sort = 'trip_id';

  // FIXME
  $scope.destroy = function(trip_id) {
  	Trip.destroy({ id: trip_id }, function() {
	  	$scope.trips.splice(trip_id, 1);
	  });
  }
});

app.controller('rawCtrl', function($scope, RawData) {
	$scope.message = 'Raw Data Controller (rawCtrl)';
});

app.controller('harCtrl', function($scope) {
	$scope.message = 'Human Activity Recognition Controller (harCtrl)';
});

app.controller('sldCtrl', function($scope) {
	$scope.message = 'Service Line Detection Controller (sldCtrl)';
});

app.controller('navbarCtrl', function($scope, $location) {
	$scope.isActive = function (viewLocation) {
    return viewLocation === $location.path().substring(1) ? 'active' : '';
	};
});

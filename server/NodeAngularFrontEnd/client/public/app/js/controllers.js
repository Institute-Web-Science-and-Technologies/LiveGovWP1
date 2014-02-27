'use strict';

/* CONTROLLERS */

// records tab
app.controller('recCtrl', function($scope, $rootScope, $modal, Data, Trip, Sensor, Map) {

    // initialize our shared data scope
    $scope.data = Data;

    // populate the shared data scope with the list of trips
    if (!$scope.data.trips) {
        $scope.data.trips = Trip.query(); // get all trips
    }

    // this is for sorting the record table
    $scope.orderByField = 'trip_id';
    $scope.reverseSort = true;


    // ugly
    // delete a trip by clicking on the button on the far right side
    $scope.destroy = function(trip) {
        var r = confirm("Are you sure that you want to permanently delete the selected record?");
        if (r == true) {
            trip.$delete();
            $scope.data.trips.splice($scope.data.trips.indexOf(trip), 1);
            console.log("DB QUERY: DELETED TRIP " + trip.trip_id);
        }
    }

    // update a trips name
    $scope.updateName = function(trip, value) {
        trip.name = value;
        trip.$save();
        console.log("DB QUERY: UPDATED TRIP " + trip.trip_id);
    }

    // click on trip to globally select it
    $scope.selectTrip = function(trip) {
        $rootScope.trip = trip;
    }

    // switch to the raw data view by clicking on a table row (OUT OF ORDER)
    $scope.changeView = function(view) {
        $rootScope.trip.trip_id = view;
        $location.path(view);
    }

    // watch the shared scope for our selected trip and start getting all
    // required data to reduce waiting time when switching to another view
    $rootScope.$watch(
        // listener: wait for $rootScope.trip changes
        function() {
            return $rootScope.trip;
        },
        // change handler: and then do this
        function(newTrip, oldTrip) {
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
    $scope.isSelected = function(trip) {
        if ($rootScope.trip) {
            if (trip.trip_id === $rootScope.trip.trip_id) {
                return 'selected';
            } else {
                return ''
            }
        }
    }

});

// raw data map and charts
app.controller('rawCtrl', function($scope, $rootScope, $location, Data) {

    // redirect to table view if no trip is selected and display message
    if (!$rootScope.trip) {
        $location.path('/rec');
    }

    $scope.data = {
        "acc": Data.acc,
        "gra": Data.gra,
        "lac": Data.lac,
        "gps": Data.gps,
        "har": Data.har
    }
});

// har map
app.controller('harCtrl', function($scope, $rootScope, Data) {

    // redirect to table view if no trip is selected and display message
    if (!$rootScope.trip) {
        $location.path('/rec');
    }

    $scope.data = {
        "gps": Data.gps,
        "har": Data.har
    }
});

// navigation bar
app.controller('navbarCtrl', function($scope, $rootScope, $location) {

    // hightlight the current tab
    $scope.isActive = function(viewLocation) {
        return viewLocation === $location.path().substring(0) ? 'active' : '';
    };

    $scope.tripSelected = function() {
        if (!$scope.trip) {
            return 'disabled'
        };
    };

    $scope.isSelected = function(trip) {
        if ($scope.trip) {
            return 'selected';
        } else {
            return 'unselected'
        }
    }

    $rootScope.$watch('trip', function() {
        $scope.trip = $rootScope.trip;
    });

});
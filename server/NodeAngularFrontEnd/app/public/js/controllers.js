/* jshint strict:true, devel:true, debug:true */
/* globals app, d3 */
'use strict'; // jshint -W097

app.controller('recCtrl',
  function($scope, $location, $route, $q, Config, Trip) {

    /*
      1. gather input
      2. perform work
      3. deliver results
      4. handle failure
    */

    /*
      - only controllers modify scope
      - only controllers communicate with services (though directives could)
      - rootScope is not used
      - passed trip variable is always trip object
      - tell, don't ask (avoid watching)
      - code does not repeat (dry)
      - all business logic happens in services
      - directives <-> controller <-> trip service <-> data factory
      - all parameters are optional
      - all functions return some useful value
     */

  // as the controller is initialized every time it's accessed by pointing the
  // brower to '/rec', it's $scope will be empty at first at this point. so
  // unconditionally call the trip service and receive our trip object, which
  // is either already prepared or freshly created.

  Trip.loadTrips().then(function(data) {
    $scope.trips = data;
  });

  // update a trip's name FIXME abstract for all fields
  this.update = function(trip, data) {
    Trip.save(trip, data);
  };

  // delete a trip
  this.delete = function(trip) {
    if (confirm("Permanently delete trip " + trip.trip_id + "?")) {
      Trip.delete(trip);
    }
  };

  // select a trip
  this.select = function(trip) {
    Trip.select(trip);
  };

  // test if a trip is selected
  this.selected = function(trip) {
    return Trip.selected(trip);
  };

  // test if trip data is loaded
  this.hasData = function(trip) {
    return Trip.hasData();
  };

  // load (more) data for a trip
  // obj is optional: { extent: Array[2], windowSize: number }
  this.loadData = function(trip, obj) {
    Trip.loadData(trip, obj);
  };

  // test if a trip has extent set (zoomed in)
  // this.extent = function(trip, extent) {
  //   return Trip.extent(trip);
  // };

  /* ... */

  // change location path
  this.to = function(loc) {
    $location.path(loc);
  };

  // test for current route (used by navbar)
  this.loc = function(loc) {
    return ($route.current && $route.current.name == loc);
  };

});

app.controller('rawCtrl',
  function($scope, $location, Trip) {

  if (!Trip.selected()) {
    $location.path('/rec');
    return;
  }

  $scope.trip = Trip.selected();

  // update scope (called by directive)
  $scope.updateExtent = function(extent) {
    $scope.$apply(function() {
      $scope.trip.extent = extent;
    });
  };

  // load more data
  $scope.loadMoreData = function(extent) {
    Trip.loadData($scope.trip, {extent: extent, windowSize: 200});
  };
});


// FIXME DRY
app.controller('navCtrl', function ($scope, $rootScope, $route, Data) {
  this.is = function(loc) {
    return ($route.current && $route.current.name == loc) ? true : false;
  };
});

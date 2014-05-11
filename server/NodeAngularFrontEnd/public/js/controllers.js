/* jshint strict:true, devel:true, debug:true */
/* globals app, d3 */
'use strict'; // jshint -W097

/*
  See 'Controllers' section in README.md for documentation.
 */

app.controller('tripCtrl',
  function($scope, $location, $route, $q, Config, Trip) {

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

  // export data. format can be 'csv' or 'json'
  this.download = function(trip, sensor, format) {
    Trip.download(trip, sensor, format);
  };

});

// FIXME DRY
app.controller('navCtrl', function ($route, Trip) {
  this.is = function(loc) {
    return ($route.current && $route.current.name == loc) ? true : false;
  };

  this.selected = function(trip) {
    return Trip.selected(trip);
  };
});

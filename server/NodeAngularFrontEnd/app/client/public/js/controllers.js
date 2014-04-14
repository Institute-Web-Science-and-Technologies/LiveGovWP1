/* global app:true, console:true, confirm:true */

'use strict'; // jshint -W097

/* CONTROLLERS */

app.controller('recCtrl', function ($scope, $rootScope, $location, $routeParams, $q, Trip, Data) {
/*
  $rootScope.trips
    Array containing all trips including sensor and geo data.

  $rootScope.trip
    Array containing the temporary state of currently selected trip.
      id: currently selected trip id
      idx: index of currently selected trip
      extent: brush extent

  $rootScope.trips[$rootScope.trip.idx]
    Array containing the currently selected trip.
      domain: min/max values of all sensor data
      ...
*/

  // get all trips
  if (!$rootScope.trips) {
    var t = new Date(); // query start time
    Trip.query().then(function (data) {
      $rootScope.trips = data;
      console.info("trip data ready (" + ((new Date() - t) / 1000) + " ms)");
    }, function (data) {
      throw("Error getting trips!");
    });
  }

  // test if a trip is selected
  this.is = function (trip) {
    if ($rootScope.trip) {
      return ($rootScope.trip.id === trip.trip_id) ? true : false;
    }
  };

  this.hasData = function (trip) {
    return $rootScope.trips[$rootScope.trips.indexOf(trip)].acc.length ? true : false;
  };

  // select a trip
  this.select = function (trip) {
    if (this.is(trip)) return; // trip already selected
    $rootScope.trip = {
      'id': trip ? trip.trip_id : undefined,
      'idx': trip ? $rootScope.trips.indexOf(trip) : undefined,
      'extent': []
    };
  };

  if (!$rootScope.trip) this.select(); // create empty trip selection object

  // delete a trip
  this.destroy = function (trip) {
    var r = confirm("Are you sure? Permanently delete trip " + trip.trip_id + "?");
    if (r) {
      Trip.delete(trip.trip_id);
      $rootScope.trips.splice($rootScope.trips.indexOf(trip), 1);
      console.log("trip deleted:", trip.trip_id);
    }
  };

  // update a trip's name
  this.updateName = function (trip, data) {
    trip.name = data;
    Trip.save(trip.trip_id, {'name': data});
    console.info("trip updated:", trip.trip_id);
  };

  // change location path
  this.to = function(loc) {
    if ($rootScope.trip) {
      $location.path(loc);
    }
  };

  // calculate a trip's domain
  function updateDomains(data) {
    // FIXME wrgon wongr wrong! causes all kind of trouble

    // AND REMEMBER: we need to update the y-domain on every extent change.
    // the x-domain must be updated on first data arrival only.

    var trip = $rootScope.trips[$rootScope.trip.idx];
    trip.domain.x = d3.extent(trip.domain.x.concat.apply([], data.map(function(d) {
      return [d.starttime, d.endtime];
    })));
    trip.domain.y = d3.extent(trip.domain.y.concat.apply([], data.map(function(d) {
      return [d.avgx, d.avgy, d.avgz]; // these are the values used drawing the chart lines
    })));
  };

  $rootScope.$watch('trip.id',
    function (newTrip, oldTrip) {
      var t = new Date(); // query start time
      if ($rootScope.trip.id) {
        if (!$rootScope.trips[$rootScope.trip.idx].updates) {
          $rootScope.trips[$rootScope.trip.idx].updates++;
          console.info("trip selected", $rootScope.trip, $rootScope.trips[$rootScope.trip.idx]);

          Data.geo();

          Data.sensor(['gra', 'acc', 'lac']).then(function(data) {
            console.log('all sensor data has arrived (' + ((new Date() - t) / 1000) + " ms)");
            var trip = $rootScope.trips[$rootScope.trip.idx];
            if (trip.updates == 1) { // if this is the first data query for this trip id
              data.map(function(d) {
                updateDomains(d); // update trip domains
              })
            }
          });
        }
      }
    }
  ); // end of watch
});

app.controller('rawCtrl', function ($scope, $rootScope, $location, Data) {
  if (!$rootScope.trip) { $location.path('/rec'); return; }
  $scope.data = $rootScope.trips[$rootScope.trip.idx];

  // update scope (called by directive)
  $scope.onBrushExtent = function(extent) {
    // EXTENT 3:
    // console.log('CTRL  EXTENT 3:', extent, $rootScope.trip.extent);
    $scope.$apply(function() { $rootScope.trip.extent = extent; });
  };
  
  // load more data (called by directive)
  $scope.loadMoreData = function(extent) {
    $rootScope.trips[$rootScope.trip.idx].updates++;
    Data.sensor(['gra', 'acc', 'lac'], extent, 'more');
  };
});

app.controller('navCtrl', function ($scope, $rootScope, $route, Data) {
  this.is = function(loc) {
    return ($route.current && $route.current.name == loc) ? true : false;
  };

  // this.deselectTrip = function () {
  //   delete $rootScope.trip;
  // };

  // this.clearBrush = function () {
  //   $rootScope.trip.extent = undefined;
  // };

  // this.loadMoreData = function(sensor) {
  //   Data.sensor('gra', $rootScope.trip.extent, 'more');
  //   Data.sensor('acc', $rootScope.trip.extent, 'more');
  //   Data.sensor('lac', $rootScope.trip.extent, 'more');
  //   $scope.digest();
  //   // $scope.apply();
  // };
});
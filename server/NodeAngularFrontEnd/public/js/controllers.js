/* jshint strict:true, devel:true, debug:true */
/* globals app, d3 */
'use strict'; // jshint -W097

/*
  See 'Controllers' section in README.md for documentation.
 */

app.controller('mainCtrl', ['$scope', '$rootScope', '$route', function($scope, $rootScope, $route) {
  console.log('loading mainCtrl');

  if (!$rootScope.httpRequests) $rootScope.httpRequests = 0;
  if (!$rootScope.httpRequestErrors) $rootScope.httpRequestErrors = false;

  $scope.$on('$routeChangeSuccess', function() {
    console.log('route changed');
    $scope.template = $route.current.templateUrl;
  });

  $scope.$on('httpRequest', function(e) {
    // console.log('http request started');
    $rootScope.httpRequests++;
  });

  $scope.$on('httpResponse', function(e) {
    // console.log('http request finished');
    $rootScope.httpRequests--;
  });

  $scope.$on('httpRequestError', function(e) {
    console.error('http request error');
    $rootScope.httpRequestErrors = true;
  });

  $scope.$on('httpResponseError', function(e) {
    console.error('http response error');
    $rootScope.httpRequests--;
  });
}]);

app.controller('tripCtrl',
  function($scope, $location, $route, $routeParams, $q, Config, Trip) {

  console.log('loading tripCtrl');

  Trip.loadTrips().then(function(data) {
    $scope.trips = data;

    if ($routeParams.trip_id) {
      console.log('trip id is set by route params to', $routeParams.trip_id);
      $scope.trip = $scope.trips.filter(function(d) {
        return d.id == $routeParams.trip_id;
      })[0];
      Trip.select($scope.trip);
    } else {
      $scope.trip = Trip.selected();
      // debugger
    }

  });

  // update a trip
  // TODO debounce
  this.update = function(trip, data) {
    // console.log(data);
    Trip.update(trip, data);
  };

  // delete a trip
  // TODO modal
  this.delete = function(trip) {
    if (confirm("Permanently delete trip " + trip.trip_id + "?")) {
      Trip.delete(trip);
    }
  };

  this.updateUrl = function(trip) {
    var c = $location.path().split('/');
    var i = c.indexOf($routeParams.trip_id);

    if (!arguments.length) {
      c.pop();
    } else if (i == -1) {
      c.push(trip.id);
    } else {
      c[i] = trip.id;
    }

    $location.url(c.join('/')).replace();
  };

  // select a trip
  this.select = function(trip) {
    if (!arguments.length) {
      this.updateUrl();
      Trip.select();
    } else {
      this.updateUrl(trip);
      Trip.select(trip);
    }
  };

  // test if a trip is selected
  this.selected = function(trip) {
    return Trip.selected(trip);
  };

  // test if trip data is loaded
  this.hasData = function(trip) {
    return Trip.hasData(trip);
  };

  this.hasDuration = function(trip) {
    return Trip.hasDuration(trip);
  };

  this.hasName = function(trip) {
    return Trip.hasName(trip);
  };

  this.hasLove = function(trip) {
    return Trip.hasLove(trip);
  };

  this.toggleLove = function(trip) {
    return Trip.toggleLove(trip);
  };

  // reset loaded trip data
  this.reset = function(trip) {
    // debugger
    console.log('reset -> trip.extent', trip.extent);
    return Trip.reset(trip);
  };

  // load (more) data for a trip
  // obj is optional: { extent: Array[2], windowSize: number }
  this.loadData = function(trip, obj) {
    Trip.loadData(trip, obj);
  };

  /* ... */

  // change location path
  this.to = function(loc, trip) {
    console.log(loc);
    $location.path(loc + "/" + trip.id);
  };

  // test for current route (used by navbar)
  this.loc = function(loc) {
    return ($route.current && $route.current.name == loc);
  };

  this.loadMoreData = function(trip) {
    console.log(Trip.hasData($scope.trip));
    var newWindowSize = Math.floor(Math.abs((Trip.hasData(trip) / 3) + 200)); // oO
    Trip.loadData($scope.trip, {windowSize: newWindowSize});
  }


  // update scope (called by directive)
  $scope.updateExtent = function(extent) {
    console.log('tripCtrl updateExtent', extent);
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

  this.is = function(loc) {
    return ($route.current && $route.current.name == loc) ? true : false;
  };
});

app.controller('navCtrl', function ($route, $scope, $routeParams, Trip) {
  console.log('loading navCtrl');

  $scope.$on('$routeChangeSuccess', function() {
    $scope.trip_id = $routeParams.trip_id;
  });

  this.selected = function() {
    return Trip.selected();
  };

  this.loc = function(loc) {
    return ($route.current && $route.current.name == loc);
  };
});

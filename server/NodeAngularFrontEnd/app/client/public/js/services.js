/* jshint strict:true, devel:true, debug:true */
/* global angular, app */
'use strict'; // jshint -W097

app.service('Config', function() {
  var sensors = ['acc', 'gra', 'lac']; // used sensors
  var xDomain = ['starttime', 'endtime']; // by which values x-domain is calculated
  var yDomain = ['avgx', 'avgy', 'avgz']; // ... y-domain ...
  var windowSize = 200; // default window size

  return {
    sensors: function() { return sensors; },
    xDomain: function() { return xDomain; },
    yDomain: function() { return yDomain; },
    windowSize: function() { return windowSize; },
  };
});

/*
    DIRECTIVES <-> CONTROLLER <-> TRIP SERVICE <-> DATA FACTORY
 */

// trip service is called by controller, calls data service
app.service('Trip', ['$http', '$q', 'Config', 'Data', function($http, $q, Config, Data) { // FIXME
  var trips = [];
  var selectedTrip;

  return {
    // select a trip and call data factory
    select: function(trip) {
      if (!arguments.length) {
        selectedTrip = undefined;
        return;
      }

      selectedTrip = trip;

      // FIXME!
      if ((trip == selectedTrip) || (this.hasData(trip))) { // ? === ==
        // if (this.hasData(trip)) {
          console.log("has data, not loading");
          return;
        // }
      }

      // console.log(trip == selectedTrip, this.hasData(trip));
      // console.info('LOAD "' + trip.id + '", 8, 1:');


      Data.geo(trip);
      var t = new Date();
      Data.sensor(trip, Config.sensors()).then(function(data) {
        console.info('all sensor data has arrived (' + ((new Date() - t) / 1000) + " ms)");
        trip.domain.x.extent(data.extent(Config.xDomain()));
        trip.domain.y.extent(data.extent(Config.xDomain()));
      });

      return trip; // FIXME split -> loadData
    },

    // test if a trip is selected
    selected: function(trip) {
      if (!arguments.length) return selectedTrip;
      return trip === selectedTrip;
    },

    // test if trip data is loaded
    hasData: function(trip) {
      if (!arguments.length) return;
      return Config.sensors()
      .map(function (d) { return trip.data.sensors[d].length; })
      .reduce(function (a, b) { return a + b; });
    },

    // load (more) data for a trip
    // obj is optional: { extent: Array[2], windowSize: number }
    loadData: function(trip, obj) {
      // Data.sensor($scope.trip, extent).then(function(data) {
      //   $scope.trip.domain.y.extent(data, Config.xDomain());
      // });
    },

    // test if a trip has extent set (zoomed in)
    extent: function(trip, extent) {
      if (!arguments.length || !selectedTrip) return;
      return (trip ? trip.extent : selectedTrip.extent);
      // console.log('CTRL  EXTENT 3:', extent, $rootScope.trip.extent);
      // $scope.$apply(function() {
      //   $scope.trip.extent = extent;
      // });
    },

    // get all trips (init)
    query: function() {
      var deferred = $q.defer();
      $http.get('/trips')
      .success(function(data, status, headers, config) {
        trips = data.map(function(d) {
          d = {
            name: d.name,
            id: d.trip_id,
            user: d.user_id,
            start: +d.start_ts,
            stop: +d.stop_ts,
            duration: +d.stop_ts - (+d.start_ts) - 3600000, // minus one hour due to wrong timestamps in db
            extent: [],
            domain: { x: [], y: [] },
            data: { sensors: {}, geo: [] } // feature collection
          };

          // create empty sensor objects
          Config.sensors().map(function (sensor) { d.data.sensors[sensor] = []; });

          return d;
        });

        deferred.resolve(trips);
      })
      .error(function(data, status, headers, config) { deferred.reject(); });

      return deferred.promise;
    },

    // update a trip's name FIXME abstract for all fields
    update: function (trip, data) {
      trip.name = data.name; // client side update

      $http({ method: 'POST', url: '/trips/' + trip.id, data: data })
      .success(function(data, status, headers, config) {
        console.info("trip updated:", trip.trip_id);
      })
      .error(function(data, status, headers, config) {});
    },

    // delete a trip
    delete: function (trip) {
      trips.splice(trips.indexOf(trip), 1); // client side removal

      $http({ method: 'DELETE', url: '/trips/' + trip.id })
      .success(function(data, status, headers, config) {
        console.info("trip deleted:", trip.id);
      })
      .error(function(data, status, headers, config) {});
    }
  };
}]);

// data factory is called by trip service only
app.factory('Data', ['$http', '$q', 'Config', function ($http, $q, Config) {
  return {
    // load sensor data
    sensor: function (trip, obj) {
      var t = new Date();

      var promises = Config.sensors().map(function(sensor) {
        var deferred = $q.defer();

        // FIXME
        // startTime/endTime -> extent[0,1]
        // windowSize -> arg.windowSize || Config.windowSize()

        var startTime = obj.extent[0];
        var endTime = obj.extent[1];
        var windowSize = obj.windowSize || Config.windowSize();

        $http({
          method: "GET",
          url: '/trips/' + trip.id + '/' + sensor + '/window',
          params: { 'window': windowSize, 'startTime': startTime, 'endTime': endTime }
        })
        .success(function (data, status, headers, config) {

          data.forEach(function(d) {
            d.ts         = (+d.starttime + (+d.endtime)) / 2;
            d.starttime  = +d.starttime;
            d.endtime    = +d.endtime;
          });

          trip.data.sensors[sensor] = trip.data.sensors[sensor].merge(data);

        // defer merged data
        deferred.resolve(trip.data.sensors[sensor]);

        console.info(sensor + ' data for trip ' + trip.id + ' ready (' + ((new Date() - t) / 1000) + " ms)");
        })
        .error(function (data, status, headers, config) {
          deferred.reject();
        });

        // return merged data as promise
        return deferred.promise;
      });

      // return all promised sensor data
      return $q.all(promises);
    },

    // load har and gps data, return feature collection
    geo: function(trip) {

      function calculateDistance(a, b) {
        return gju.pointDistance({
          type: 'Point',
          coordinates: a
        }, {
          type: 'Point',
          coordinates: b
        });
      }

      function createFeature(coordinates, activity) {
        return {
          'type': 'Feature',
          'geometry': {
            'type': 'LineString',
            'coordinates': coordinates // [g0, g1]
          },
          'properties': {
            'activity': activity
          }
        };
      }

      // FIXME MOVE TO HELPERS
      // get array element which occures the most
      function getMaxOccurrence(array) {
        if (!array.length) return null;
        var len = array.length;
        var modeMap = {};
        var maxEl = array[0];
        var maxCount = 1;
        for (var i = 0; i < len; i++) {
          var el = array[i];
          if (modeMap[el] === null) modeMap[el] = 1;
          else modeMap[el]++;
          if (modeMap[el] > maxCount) {
            maxEl = el;
            maxCount = modeMap[el];
          }
        }
        return maxEl;
      }

      // FIXME MOVE TO HELPERS
      function topActivity(har, t0, t1) {
        return getMaxOccurrence(har.map(function (d) {
          if (d.ts >= t0 && d.ts <= t1) { // get tags between t0 and t1
            return d.tag.replace(/\"/g, ""); // remove quotes
          }}).filter(function (d) { return d; }) // remove undefined
        );
      }

      var gps = $http.get('/trips/' + trip.id + '/gps');
      var har = $http.get('/trips/' + trip.id + '/har');

      var deferred = $q.defer();
      var t = new Date();

      var fc = {
        "type": "FeatureCollection",
        "features": []
      };

      $q.all([gps, har]).then(function(data) {
        var gps = data[0].data;
        var har = data[1].data;

        var gpsLength = gps.length;
        var harLength = har.length;

        var n = 0;

        for (var i = gpsLength - 1; i >= 0; --i) {
          if (gps[i - 1]) {
            var coordinates = [gps[i-1].lonlat.coordinates, gps[i].lonlat.coordinates];
            var activity = topActivity(har, gps[i-1].ts, gps[i].ts);

            if (previousFeature && previousFeature.properties.activity == activity) {
              previousFeature.geometry.coordinates.push(coordinates[1]);
              // previousFeature.properties.t1 = t1; // wrong timestamp?
              // previousFeature.properties.distance += calculateDistance(g0, g1);
              // previousFeature.properties.duration += moment.duration(t1 - t0);
            } else {
              fc.features.push(createFeature(coordinates, activity));
            }

            var previousFeature = fc.features[fc.features.length - 1];
          }
        }

        deferred.resolve(fc);
      });
      return deferred.promise;
    }
  }
}]);

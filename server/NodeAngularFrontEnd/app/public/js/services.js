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

// Trip.loadTrips():
// 1. load trip data and populate the trip table view ('/rec')

// Trip.select():
// 2. user selects a trip

// Trip.loadData():
// 3. load sensor data for that trip

app.service('Trip',
  ['$http', '$q', 'Config', 'Data', function($http, $q, Config, Data) {

  var trips = []; // all data is stored in here
  var selectedTrip; // a copy(?) of the currently selected trip object

  return {

    // called unconditionally by the record controller. returns a promise on
    // the trip object. if it's already populated, just resolve the promise
    // with it, else send an xhr request to our api, (hopefully) receive the
    // data and prepare it first.

    loadTrips: function() {
      var deferred = $q.defer();

      if (trips.length) {
        deferred.resolve(trips);
      } else {

        // do the xhr request on '/trips' to get the trip list and immediately
        // return a promise on the to be received and prepared data. see
        // this.query() in the record controller (recCtrl).

        $http.get('/trips')
        .success(function(data, status, headers, config) {

          // set up trip object architecture
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
        .error(function(data, status, headers, config) {
          console.error("Could not load trips!");
          deferred.reject();
        });
      }

      return deferred.promise;
    },

    // select a trip and call data factory
    select: function(trip) {
      // call w/o args to clear trip selection
      if (!arguments.length) {
        selectedTrip = undefined;
        return;
      }

      selectedTrip = trip;

      // load trip data if neccessary
      if (!this.hasData(trip)) this.loadData(trip);

      return;
    },

    // load (more) data for a trip
    // obj is optional: { extent: Array[2], windowSize: number }
    loadData: function(trip, obj) {
      var that = this;
      var t = new Date();
      console.info(trip.id + ": loading sensor and geo data");

      // if (obj) {
      //   console.log(obj);
      //   debugger
      // }

      Data.geo(trip); // TODO do only when neccessary

      // load sensor data. calls Data.sensor() for every sensor given in
      // Config.sensors(). when all sensor data has arrived, update the trips
      // x and y domain (which is the min and max timestamp for the x-axis and
      // min and max sensor data for the y-axis. see Config.xDomain() and
      // Config.yDomain())

      Data.sensor(trip, obj).then(function(data) {
        trip.domain.x = data.extent(Config.xDomain());
        trip.domain.y = data.extent(Config.yDomain());
        console.info(trip.id + ": done (" + ((new Date() - t) / 1000) + " ms, " + that.hasData(trip) + ")", trip);
      });
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

        $http({
          method: "GET",
          url: '/trips/' + trip.id + '/' + sensor + '/window',
          params: {
            'window':    (obj && obj.hasOwnProperty('windowSize') ? obj.windowSize : Config.windowSize()),
            'startTime': (obj && obj.hasOwnProperty('extent')     ? obj.extent[0] : undefined),
            'endTime':   (obj && obj.hasOwnProperty('extent')     ? obj.extent[1] : undefined)
          }
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

        })
        .error(function (data, status, headers, config) {
          console.warn('Problems getting sensor data! ' + sensor + ' data for trip ' + trip.id + ' ready (' + ((new Date() - t) / 1000) + " ms)");
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

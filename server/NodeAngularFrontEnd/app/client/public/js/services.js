/* global app:true, console:true, angular:true, d3:true, moment:true, gju:true */
'use strict'; // jshint -W097

app.factory('Trip', ['$http', '$q', function ($http, $q) {
  return {
    query: function() {
      var deferred = $q.defer();
      $http.get('/trips')
      .success(function(data, status, headers, config) {
        data.map(function(d) {
          d.start_ts = +d.start_ts;
          d.stop_ts = +d.stop_ts;
          d.duration = +d.stop_ts - +d.start_ts - 3600000;
          d.gra = []; // gravity sensor data
          d.lac = []; // linear acceleration sensor data
          d.acc = []; // acceleration sensor data
          d.domain = {x: [], y: []}; // min/max values of all sensor data
          d.updates = 0; // count data updates
        });
        deferred.resolve(data);
      }).error(function(data, status, headers, config) {
        deferred.reject(data);
      });
      return deferred.promise;
    },

    save: function (tripId, data) {
      $http({method: 'POST', url: '/trips/' + tripId, data: data
      }).success(function(data, status, headers, config) {
      }).error(function(data, status, headers, config) {
      });
    },

    delete: function(tripId) {
      $http({method: 'DELETE', url: '/trips/' + tripId
      }).success(function(data, status, headers, config) {
      }).error(function(data, status, headers, config) {
      });
    }
  };
}]);

app.factory('Data', ['Geo', 'Sensor', '$rootScope', function (Geo, Sensor, $rootScope) {
  var updateDomains = function(data) {
    var trip = $rootScope.trips[$rootScope.trip.idx];
    trip.domain.x = d3.extent(trip.domain.x.concat.apply([], data.map(function(d) {
      return [d.starttime, d.endtime];
    })));
    trip.domain.y = d3.extent(trip.domain.y.concat.apply([], data.map(function(d) {
      return [d.minx, d.miny, d.minz, d.maxx, d.maxy, d.maxz];
    })));
  };

  function merge(oldData, data) {
    // FIXME: CHECK FOR CORRECTNESS

    if (!oldData.length) return data; // on first run

    var len = oldData.length;
    function index() {
      for (var i = 0; i < len; i++) {
        if (oldData[i].starttime >= data[0].starttime) return i;
      }
    }

    function range() {
      for (var i = len - 1; i >= 0; --i) {
        if (oldData[i].starttime <= data[data.length - 1].starttime) {
          var idx = index();
          return [idx, i - idx + 1]; // [ from-here, n-fields ]
        }
      }
    }

    // dont merge if there's no new data
    if (((data.length - range()[1]) > 0)) {
      Array.prototype.splice.apply(oldData, range().concat(data));
    }

    return oldData;
  }

  function sortData(data) {
    // data.sort(function(a, b) { return d3.ascending(a.starttime, b.starttime); });
    data.sort(function(a, b) { return +a.ts < +b.ts ? -1 : +a.ts > +b.ts ? 1 : 0; });
  }


  return {
    sensor: function (sensor, extent) {
      var trip = $rootScope.trips[$rootScope.trip.idx];
      var t = new Date();
      Sensor.query({
        tripId: trip.trip_id,
        oldData: trip[sensor], // data array
        sensor: sensor,
        extent: extent
      }).then(function (data) {
        data.forEach(function(d) {
          d.ts         = new Date((+d.starttime + (+d.endtime)) / 2);
          d.starttime  = +d.starttime;
          d.endtime    = +d.endtime;
        });

        trip[sensor] = data; // data is already merged
        if (trip.updates == 1) { // if this is the first data query for this trip id
          updateDomains(data); // update trip domain
        }
        console.log('Success: ' + sensor + ' data for trip ' + trip.trip_id + ' ready (' + ((new Date() - t) / 1000) + " ms)");
      }, function (data) {
        console.log("Failure: No " + sensor + " data for trip " + trip.trip_id);
      });
    },

    geo: function () {
      var trip = $rootScope.trips[$rootScope.trip.idx];
      var t = new Date();
      Geo.query(trip.trip_id)
      .then(function (data) {
        trip.geo = data;
        console.log('Success: geo data for trip ' + trip.trip_id + ' ready (' + ((new Date() - t) / 1000) + " ms)");
      }, function (data) {
        console.log("Failure: No geo data for trip " + trip.trip_id);
      });
    }
  };
}]);

app.factory('Geo', ['$http', '$q', function ($http, $q) {
  function getMaxOccurrence(array) {
    if (array.length === 0) return null;
    var modeMap = {};
    var maxEl = array[0];
    var maxCount = 1;
    for (var i = 0; i < array.length; i++) {
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

  // calculate the most popular tag between t0 and t1
  function topActivity(har, t0, t1) {
    return getMaxOccurrence(har.map(function (d) {
      if (d.ts >= t0 && d.ts <= t1) { // get tags between t0 and t1
        return d.tag.replace(/\"/g, ""); // remove quotes
      }}).filter(function (d) { return d; }) // remove undefined
    );
  }

  function calculateDistance(a, b) {
    return gju.pointDistance({
      type: 'Point',
      coordinates: a
    }, {
      type: 'Point',
      coordinates: b
    });
  }

  function getSpeedMode(d) {
    if (d === 0) {
      return "standing";
    } else if (d > 0 && d < 5) {
      return "walking";
    } else if (d > 5 && d < 20) {
      return "running";
    } else if (d > 20) {
      return "driving";
    } else return null;
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

  return {
    query: function(tripId) {

      var gps = $http.get('/trips/' + tripId + '/gps');
      var har = $http.get('/trips/' + tripId + '/har');

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
    }};
}]);

app.factory('Sensor', ['$http', '$q', function ($http, $q) {
  return {
    query: function(args) {
      if (!args.sensor)  throw("Error: sensor missing");
      if (!args.tripId) throw("Error: tripId missing");
  
      var deferred = $q.defer();

      var tripId = args.tripId,
          oldData = args.oldData,
          sensor = args.sensor,
          windowSize,
          startTime,
          endTime;

      if (args.extent) {
        if (!args.extent.length) deferred.reject(); // return: no selection -> nothing to fetch (zooming out)
        startTime = +args.extent[0];
        endTime   = +args.extent[1];
      }

      $http({
        method: "GET",
        url: '/trips/' + tripId + '/' + sensor + '/window',
        params: { 'window': windowSize, 'startTime': startTime, 'endTime': endTime }
      })
      .success(function (data, status, headers, config) { deferred.resolve(data); })
      .error(function (data, status, headers, config) { deferred.reject(data); });

      return deferred.promise;
    }
  };
}]);

///////////////////////////////////////////////////////////////////////////////
// DEBOUNCE SERVICE
///////////////////////////////////////////////////////////////////////////////

// https://gist.github.com/adamalbrecht/7226278
// https://github.com/angular/angular.js/issues/2690
// https://stackoverflow.com/questions/13320015/how-to-write-a-debounce-service-in-angularjs

// Returns a function, that, as long as it continues to be invoked, will not
// be triggered. The function will be called after it stops being called for
// N milliseconds. If `immediate` is passed, trigger the function on the
// leading edge, instead of the trailing.
app.factory('debounce', function($timeout, $q) {
  return function(func, wait, immediate) {
    var timeout;
    var deferred = $q.defer();
    return function() {
      var context = this, args = arguments;
      var later = function() {
        timeout = null;
        if(!immediate) {
          deferred.resolve(func.apply(context, args));
          deferred = $q.defer();
        }
      };
      var callNow = immediate && !timeout;
      if ( timeout ) {
        $timeout.cancel(timeout);
      }
      timeout = $timeout(later, wait);
      if (callNow) {
        deferred.resolve(func.apply(context,args));
        deferred = $q.defer();
      }
      return deferred.promise;
    };
  };
});

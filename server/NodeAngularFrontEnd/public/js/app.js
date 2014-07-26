/* jshint strict:true, devel:true, debug:true, camelcase:false, curly:false */
/* global angular, React, _, d3 */

(function() {
  'use strict';

    // merge two sensor data arrays, sorted w/o duplicates
    Array.prototype.merge = function(array) {
      if (!this.length) return array;
      if (!array.length) return this;

      // sort by timestamp first, then remove the ones where a[i+1].endtime is
      // bigger than a[i].endtime

      return this.concat(array)
        .sort(function(a,b) {
          return d3.ascending(a.ts, b.ts);
        })
        .filter(function(d,i,a) { // true returns d
          return (a[i+1] ? (a[i].stop <= a[i+1].stop) : true);
        })
        .filter(function(d,i,a) { // FIXME there are still remaining entries after the first filter run
          return (a[i+1] ? (a[i].stop <= a[i+1].stop) : true);
        });
    };

    var config = {
      api: {
        trips: '/trips',
        check: function(id) { return '/trips/' + id + '/check'; },
        count: function(id) { return '/trips/' + id + '/count'; },
        sensors: function(id, sensor) { return '/trips/' + id + '/' + sensor; }
      },
      windowSize: 200
    };

    // angular module
		angular.module('app', ['ngResource', 'ngRoute'])

    // provide location.path() in template
    .run(function($rootScope, $routeParams) {
      $rootScope.routeParams = $routeParams;
    })

    // http interceptor (so we can check on ongoing xhr calls)
    .config(function ($httpProvider, $provide, $routeProvider) {

      // (fake) routes
      $routeProvider
      .when('/:view/:id', {})
      .otherwise({redirectTo: '/'});

      $provide.factory('httpInterceptor', function ($q, $rootScope) {
        return {
          'request': function (config) {
            // intercept and change config: e.g. change the URL
            // config.url += '?nocache=' + (new Date()).getTime();
            $rootScope.$broadcast('httpRequest', config);
            return config || $q.when(config);
          },
          'response': function (response) {
            // we can intercept and change response here
            $rootScope.$broadcast('httpResponse', response);
            return response || $q.when(response);
          },
          'requestError': function (rejection) {
            $rootScope.$broadcast('httpRequestError', rejection);
            return $q.reject(rejection);
          },
          'responseError': function (rejection) {
            $rootScope.$broadcast('httpResponseError', rejection);
            return $q.reject(rejection);
          }
        };
      });
      $httpProvider.interceptors.push('httpInterceptor');
    })

    // ANGULAR CONTROLLER
		.controller('appCtrl', ['$rootScope', '$scope', '$location', '$route', '$routeParams', 'Data',
			function($rootScope, $scope, $location, $route, $routeParams, Data) {

      // handle intercepted http requests
      if (!$rootScope.httpRequests) { $rootScope.httpRequests = 0; }
      if (!$rootScope.httpRequestErrors) { $rootScope.httpRequestErrors = false; }

      $scope.$on('$routeChangeSuccess', function() {
        console.log('route changed');
        $scope.template = $route.current.templateUrl;
      });

      $scope.$on('httpRequest', function(e) {
        console.log('http request started');
        $rootScope.httpRequests++;
      });

      $scope.$on('httpResponse', function(e) {
        console.log('http request finished');
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

      Data.trips()
      .then(function(data) {
        $scope.trips = data;
        if ($routeParams.id) { $scope.selectTrip(+$routeParams.id); }
      });

      $scope.selectTrip = function(id) {
        $scope.trip = $scope.trips.filter(function(trip) {
          return trip.id === id;
        })[0];

        Data.countData(id)
        .then(function(dataCount) {
          $scope.trip.count = {};

          _.each(dataCount, function(d) {
            var key = Object.keys(d);
            $scope.trip.count[key] = +d[key];
          });

          Data.loadData($scope.trip)
          .then(function(data) {
            $scope.trip.data = data;
            console.log('ready', $scope.trip);
            $location.path('raw/' + id);
          });
        });
      };

      $scope.loadMoreData = function(id, props) {
        Data.loadData($scope.trip, props, true)
        .then(function(data) {
          _.keys(data).forEach(function(sensor) {
            $scope.trip.data[sensor] = $scope.trip.data[sensor].merge(data[sensor]);
          });
        });
      };

      $scope.deleteTrip = function(id) {
        $scope.trips = $scope.trips.filter(function(trip) { return trip.id !== id; });
        Data.deleteTrip(id);
      };

      $scope.updateTrip = function(id, value) {
        Data.updateTrip(id, value);
      };
		}])

    // DATA FACTORY (MAKES API CALLS)
    .factory('Data', ['$http', '$q', function($http, $q) {
      return {
        // get all trips
        trips: function() {
          var deferred = $q.defer();
          $http.get(config.api.trips)
          .success(function(data) {
            data.forEach(function(trip) {
              trip.start = +trip.start;
              trip.stop = +trip.stop;
              trip.duration = trip.stop - trip.start;
              trip.user = trip.user.replace(/"/g, '');
            });

            deferred.resolve(data);
          });

          return deferred.promise;
        },

        // get all sensor, gps and har data for a trip
        loadData: function(trip, params, more) {
          var deferred = $q.defer();

          var sensors = _.keys(trip.count).filter(function(d) { return trip.count[d]; });

          if (more) {
            sensors.splice(sensors.indexOf('sensor_gps'), 1);
            sensors.splice(sensors.indexOf('sensor_har'), 1);
            sensors.splice(sensors.indexOf('sensor_tags'), 1);
          }

          var queries = sensors.map(function(sensor) {
            return $http.get(config.api.sensors(trip.id, sensor), {
              params: {
                'w': (params && params.windowSize || config.windowSize),
                'e': (params && params.extent.join(","))
              }
            });
          });

          $q.all(queries)
          .then(function(arr) {
            // sanitize data
            arr.forEach(function(sensor) {
              sensor.data.forEach(function(d) {

                // remove toxic characters from strings
                if (d.hasOwnProperty('tag')) {
                  d.tag = d.tag.replace(/"/g, '').replace(/ /g, '_');
                }

                // convert timestamps from strings to numbers
                if (d.hasOwnProperty('ts')) { d.ts = +d.ts; }

                // convert start and stop times to numbers and calculate ts
                if (d.hasOwnProperty('start') && d.hasOwnProperty('stop')) {
                  d.start = +d.start;
                  d.stop  = +d.stop;
                  d.ts = Math.floor((d.stop + d.start) / 2);
                }
              });
            });

            var data = {};
            sensors.forEach(function(sensor, i) {
              data[sensor] = arr[i].data;
            });

            deferred.resolve(data);
          });

          return deferred.promise;
        },

        // minimal db query to count available data
        countData: function(id) {
          var deferred = $q.defer();
          $http.get(config.api.count(id))
          .success(function(data) {
            deferred.resolve(data);
          });
          return deferred.promise;
        },

        deleteTrip: function(id) {
          $http({ method: 'DELETE', url: 'trips/' + id })
          .success(function() {
            console.info('DELETED TRIP', id);
          });
        },

        updateTrip: function (id, value) {
          $http({ method: 'POST', url: 'trips/' + id, data:{name:value} })
          .success(function() {
            console.info('UPDATED TRIP', id);
          });
        },
      };
    }])

    .directive('trips', [function(){
      return {
        scope: {
          trips: '=',
          selectTrip: '&',
          deleteTrip: '&',
          updateTrip: '&'
        },
        restrict: 'E',
        link: function($scope, $element) {
          $scope.$watchCollection('trips', function(trips) {
            if (!trips || !trips.length) { return; }

            React.renderComponent(Trips({
              trips:trips,
              selectTrip: function(id) {
                $scope.selectTrip({id:id});
              },
              deleteTrip: function(id) {
                $scope.deleteTrip({id:id});
              },
              updateTrip: function(id, value) {
                $scope.updateTrip({id:id, value:value});
              },
            }), $element[0]);

          });
        }
      };
    }])

    .directive('raw', [function(){
      return {
        scope: { trip: '=', loadMoreData: '&' },
        restrict: 'E',
        link: function($scope, $element) {
          $scope.$watchCollection('trip', function(trip) {
            if (!trip || !trip.data) { return; }

            React.renderComponent(Raw({
              trip:trip,
              width: $element[0].parentNode.offsetWidth,
              loadMoreData: function(props) {
                $scope.loadMoreData({props:props});
              },
            }), $element[0]);
          });
        }
      };
    }])

    .directive('har', [function(){
      return {
        scope: { trip: '=' },
        restrict: 'E',
        link: function($scope, $element) {
          $scope.$watchCollection('trip', function(trip) {
            if (!trip || !trip.data) { return; }

            React.renderComponent(Har({
              trip:trip,
              width: $element[0].parentNode.offsetWidth
            }), $element[0]);
          });
        }
      };
    }])

    .directive('menu', [function(){
      return {
        scope: { trip: '=' },
        restrict: 'E',
        link: function($scope, $element) {
          $scope.$watch('trip.id', function(tripId) {
            React.renderComponent(Menu({
              tripId: tripId || false
            }), $element[0]);
          });
        }
      };
    }]);

}());

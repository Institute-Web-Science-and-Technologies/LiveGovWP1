/* jshint -W097 */
/* global app:true, console:true, angular:true */

'use strict';

app.factory('Data', function () {
	// return a at first empty object. will be populated.
	return {};
});

app.factory('Trip', function($resource) {
	return $resource('/trips/:trip_id', { trip_id: '@trip_id' });
});

app.factory('Sensor', function($resource) {
  return $resource(
    '/trips/:trip_id/:sensor/window', {trip_id: '@trip_id' }, {
      acc: {method: 'GET', 'params': {sensor: 'acc'}, isArray: true},
      lac: {method: 'GET', 'params': {sensor: 'lac'}, isArray: true},
      gra: {method: 'GET', 'params': {sensor: 'gra'}, isArray: true}
    }
  );
});

// TODO rewrite using $http, so we can get data at once and prepare it

app.factory('Map', function($resource) {
	return $resource(
		'/trips/:trip_id/:what', {trip_id: '@trip_id' }, {
			gps: {method: 'GET', 'params': {what: 'gps'}, isArray: true},
			har: {method: 'GET', 'params': {what: 'har'}, isArray: true}
		}
	);
});

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
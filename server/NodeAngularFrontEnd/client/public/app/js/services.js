'use strict';

app.factory('Data', function () {
	// return a at first empty object. will be populated.
	return {};
});

app.factory('Trip', function($resource) {
	console.log("DB QUERY: GETTING TRIP DATA");
	return $resource('/trips/:trip_id', { trip_id: '@trip_id' });
});

app.factory('Sensor', function($resource) {
	console.log("DB QUERY: GETTING SENSOR DATA");
	return $resource(
		'/trips/:trip_id/:sensor/window', {trip_id: '@trip_id' }, {
			acc: {method: 'GET', 'params': {sensor: 'acc'}, isArray: true},
			lac: {method: 'GET', 'params': {sensor: 'lac'}, isArray: true},
			gra: {method: 'GET', 'params': {sensor: 'gra'}, isArray: true}
		}
	);
});

app.factory('Map', function($resource) {
	console.log("DB QUERY: GETTING MAP DATA");
	return $resource(
		'/trips/:trip_id/:what', {trip_id: '@trip_id' }, {
			gps: {method: 'GET', 'params': {what: 'gps'}, isArray: true},
			har: {method: 'GET', 'params': {what: 'har'}, isArray: true}
		}
	);
});
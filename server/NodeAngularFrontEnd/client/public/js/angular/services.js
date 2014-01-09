'use strict';

/* SERVICES */

app.factory('Trip', function ($resource) {
	return $resource(
		'/trips/:id', {}, // {Id: '@Id' },
		{
			index: {method: 'GET', isArray: true},
			show: {method: 'GET', 'params': {Id: '@Id'}, isArray: true},
			update: {method: 'PUT', 'params': {Id: '@Id'}},
			destroy: {method: 'DELETE', 'params': {Id: '@Id'}}
		}
		);
});

app.factory('RawData', function($resource) {
	return $resource();
});

app.factory('ActivityRecognition', function($resource) {
	return $resource();
});

app.factory('ServiceLineDetection', function($resource) {
	return $resource();
});

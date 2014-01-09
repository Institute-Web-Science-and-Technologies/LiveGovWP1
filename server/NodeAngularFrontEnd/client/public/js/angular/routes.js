'use strict';

/* ROUTES */

app.config(function($routeProvider) {
  // $locationProvider.html5Mode(true);
  // $locationProvider.hashPrefix('!');

	$routeProvider

		// recordings (trip list)
		.when('/rec', {
			templateUrl: '/partials/recordings',
			controller: 'recCtrl'
		})

		// raw data
		.when('/raw', {
			templateUrl: 'partials/raw_data',
			controller: 'rawCtrl'
		})

		// human activity recognition (har)
		.when('/har', {
			templateUrl: '/partials/human_activity_recognition',
			controller: 'harCtrl'
		})

		// service line detection (sld)
		.when('/sld', {
			templateUrl: '/partials/service_line_detection',
			controller: 'sldCtrl'
		})
});

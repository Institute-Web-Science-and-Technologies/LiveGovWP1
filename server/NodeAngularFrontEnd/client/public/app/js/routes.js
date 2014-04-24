/* jshint -W097 */
'use strict';

/* global app:true, console:true, confirm:true */

/* ROUTES */

app.config(function ($routeProvider, $locationProvider) {
	// $locationProvider.html5Mode(true);
	// $locationProvider.hashPrefix('!');

	$routeProvider
		.when('/rec', {
			templateUrl: '/partials/rec',
			controller: 'recCtrl'
			// reloadOnSearch: false
		})

	.when('/rec/:trip_id', {
		templateUrl: '/partials/rec',
		controller: 'recCtrl'
	})

	.when('/raw', {
		templateUrl: '/partials/raw',
		controller: 'rawCtrl'
	})

	.when('/raw/:trip_id', {
		templateUrl: '/partials/raw',
		controller: 'rawCtrl'
	})

	.when('/har', {
		templateUrl: '/partials/har',
		controller: 'harCtrl'
	})

	.when('/har/:trip_id', {
		templateUrl: '/partials/har',
		controller: 'harCtrl'
	})

	.when('/sld', {
		templateUrl: '/partials/sld',
		controller: 'sldCtrl'
	})

	.otherwise({
		redirectTo: '/rec'
	});
});
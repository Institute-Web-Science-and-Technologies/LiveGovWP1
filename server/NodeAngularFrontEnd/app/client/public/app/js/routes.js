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
			controller: 'recCtrl',
			controllerAs: 'rec',
			name: 'rec'
		})

		.when('/rec/:trip_id', {
			templateUrl: '/partials/rec',
			controller: 'recCtrl',
			controllerAs: 'rec',
			name: 'rec'
		})

		.when('/raw', {
			templateUrl: '/partials/raw',
			controller: 'rawCtrl',
			controllerAs: 'raw',
			name: 'raw'
		})

		.when('/raw/:trip_id', {
			templateUrl: '/partials/raw',
			controller: 'rawCtrl',
			controllerAs: 'raw',
			name: 'raw'
		})

		.when('/har', {
			templateUrl: '/partials/har',
			controller: 'harCtrl',
			controllerAs: 'har',
			name: 'har'
		})

		.when('/har/:trip_id', {
			templateUrl: '/partials/har',
			controller: 'harCtrl',
			controllerAs: 'har',
			name: 'har'
		})

		.when('/sld', {
			templateUrl: '/partials/sld',
			controller: 'sldCtrl',
			controllerAs: 'sld',
			name: 'sld'
		})

		.when('/sld/:trip_id', {
			templateUrl: '/partials/sld',
			controller: 'sldCtrl',
			controllerAs: 'sld',
			name: 'sld'
		})

		.otherwise({
			redirectTo: '/rec'
	});
});
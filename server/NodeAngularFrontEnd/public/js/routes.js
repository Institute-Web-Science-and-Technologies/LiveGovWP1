/* jshint strict:true, devel:true, debug:true */
/* globals app */
'use strict'; // jshint -W097

//- NOTE be careful to specify paths relative to the current directory

app.config(function ($routeProvider, $locationProvider) {
	// $locationProvider.html5Mode(true);
	// $locationProvider.hashPrefix('!');

	$routeProvider
		.when('/rec', { // relative to hash (e.g. localhost:3001/#/rec)
			templateUrl: './partials/rec', // relative to host (e.g. localhost:3001/partials/rec)
			controller: 'recCtrl',
		})

		.when('/raw', {
			templateUrl: './partials/raw',
			controller: 'rawCtrl',
		})

		.otherwise({
			redirectTo: '/rec'
	});
});
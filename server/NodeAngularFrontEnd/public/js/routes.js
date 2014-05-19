/* jshint strict:true, devel:true, debug:true */
/* globals app */
'use strict'; // jshint -W097

//- NOTE be careful to specify paths relative to the current directory

app.config(function ($routeProvider, $locationProvider) {
  // $locationProvider.html5Mode(true);
  // $locationProvider.hashPrefix('!');

  // NOTE
  // paths must be relative and without leading ./ (e.g. localhost:3001/#/rec)

  $routeProvider
    .when('/rec/:trip_id?', {
      templateUrl: 'partials/rec',
    })

    .when('/raw/:trip_id?', {
      templateUrl: 'partials/raw',
    })

    .when('/har/:trip_id?', {
      templateUrl: 'partials/har',
    })

    .otherwise({
     redirectTo: '/rec/:trip_id?'
    });
});
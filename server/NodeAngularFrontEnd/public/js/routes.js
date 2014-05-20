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

// to know if there are any $http queries running
// https://docs.angularjs.org/api/ng/service/$http
app.config(function ($httpProvider, $provide) {
    $provide.factory('httpInterceptor', function ($q, $rootScope) {
        return {
            'request': function (config) {
                // intercept and change config: e.g. change the URL
                // config.url += '?nocache=' + (new Date()).getTime();
                // broadcasting 'httpRequest' event
                $rootScope.$broadcast('httpRequest', config);
                return config || $q.when(config);
            },
            'response': function (response) {
                // we can intercept and change response here...
                // broadcasting 'httpResponse' event
                $rootScope.$broadcast('httpResponse', response);
                return response || $q.when(response);
            },
            'requestError': function (rejection) {
                // broadcasting 'httpRequestError' event
                $rootScope.$broadcast('httpRequestError', rejection);
                return $q.reject(rejection);
            },
            'responseError': function (rejection) {
                // broadcasting 'httpResponseError' event
                $rootScope.$broadcast('httpResponseError', rejection);
                return $q.reject(rejection);
            }
        };
    });
    $httpProvider.interceptors.push('httpInterceptor');

});

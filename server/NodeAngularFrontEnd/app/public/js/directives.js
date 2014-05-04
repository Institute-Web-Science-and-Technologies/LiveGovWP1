/* jshint strict:true, devel:true, debug:true */
/* globals app, d3 */
'use strict'; // jshint -W097

app.directive('brush', [
  function($window) {
    return {
      restrict: 'E',
      scope: {data: '=', domain: '=', extent: '=', updateExtent: '&', loadMoreData: '&'},
      link: function($scope, $element, $attributes) {
        var brush = d3.custom.chartBrush();
        var brushElement = d3.select($element[0]); // brushes 'this'

        brush.on('brushed', function(d, i) {
          // $log('BRUSH EXTENT 2:', d, $scope.extent);
          $scope.updateExtent({args: d});
        });

        brush.on('brushended', function(d, i) {
          $scope.loadMoreData({args: d});
        });

        $scope.$watchCollection('domain', function(domain, oldDomain) {
          if (domain && domain.x.length && domain.y.length) {
            brushElement.datum({domain: domain}).call(brush);
          }
        });

        $scope.$watchCollection('extent', function(extent, oldExtent) {
          if (extent && extent.length) {
            brushElement.call(brush.extent($scope.extent));
          }
        });

      }
    };
  }
]);

app.directive('chart', [
  function($window) {
    return {
      restrict: 'E',
      scope: {data: '=', domain: '=', extent: '=', updateExtent: '&', loadMoreData: '&'},
      link: function($scope, $element, $attributes) {

        var chart = d3.custom.lineChart();
        var chartElement = d3.select($element[0]); // chart's this

        chart.on('brushended', function(d, i) {
          $scope.updateExtent({args: d});
          $scope.loadMoreData({args: d});
        });

        // draw the chart as soon as data is ready
        $scope.$watchCollection('data', function(data, oldData) {
          if (data && data.length && $scope.domain.x.length && $scope.domain.y.length) {
            chartElement
            .datum({data: data})
            .call(chart
              .xScale($scope.extent.length ? $scope.extent : $scope.domain.x)
              .yScale($scope.domain.y)
            );
          }
        });

        // FIXME get rid of this watch and communicate via event listener
        // watch for extent changes and change the chart's domains accordingly
        $scope.$watchCollection('extent', function(extent, oldExtent) {
          if (extent && extent.length && $scope.data.length && $scope.domain.x.length && $scope.domain.y.length) {
          chartElement
            .call(chart
              .extent($scope.extent.length ? $scope.extent : $scope.domain.x)
            );
          }
        });
      }
    };
  }
]);

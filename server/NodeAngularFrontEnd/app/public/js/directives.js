/* jshint strict:true, devel:true, debug:true */
/* globals app, d3 */
'use strict'; // jshint -W097

app.directive('brush', [
  function($window, $log) {
    return {
      restrict: 'E',
      scope: {data: '=', domain: '=', extent: '=', onBrushExtent: '&', loadMoreData: '&'},
      link: function($scope, $element, $attributes) {
        var brush = d3.custom.chartBrush();
        var brushElement = d3.select($element[0]); // brush's this

        brush.on('brushed', function(d, i) {
          // $log('BRUSH EXTENT 2:', d, $scope.extent);
          $scope.onBrushExtent({args: d});
        });

        brush.on('brushended', function(d, i) {
          $scope.loadMoreData({args: d});
        });

        $scope.$watchCollection('domain', function(domain, oldDomain) {
          if (domain && domain.x.length && domain.y.length) {
            brushElement.datum({domain: domain}).call(brush);
          }
        });
      }
    }
  }
]);

app.directive('chart', [
  function($window, $log) {
    return {
      restrict: 'E',
      scope: {data: '=', domain: '=', extent: '=', onBrushExtent: '&', loadMoreData: '&'},
      link: function($scope, $element, $attributes) {

        // if (!$scope.data || !$scope.data.length) return;
        // console.log('chart directive');

        var chart = d3.custom.lineChart();
        var chartElement = d3.select($element[0]); // chart's this

        // if not extent is set, pass the x-domain, else pass extent

        // if extent is set
        //   pass it to xScale, else pass x-domain
        //   pass recalculated y-domain to yScale, else pass y-domain (optional)

        // don't draw chart before x- and y-domain are ready

        // use dates for the extent, not timestamps

        // don't draw chart before data is ready

        // if ($scope.data && $scope.data.length) {
        //   chartElement
        //     .datum({data: $scope.data})
        //     .call(chart
        //       .xScale(($scope.extent ? $scope.extent : $scope.domain.x))
        //       .yScale($scope.domain.y)
        //     );
        // }

        chart.on('brushended', function(d, i) {
          // $log('CHART EXTENT 2:', d, $scope.extent);
          $scope.onBrushExtent({args: d});
          $scope.loadMoreData({args: d});
        });

        // draw the chart as soon as data is ready
        $scope.$watchCollection('data', function(data, oldData) {
          if (data && data.length && $scope.domain.x.length && $scope.domain.y.length) {
            chartElement
              .datum({data: data})
              .call(chart
                .xScale(($scope.extent ? $scope.extent : $scope.domain.x))
                .yScale($scope.domain.y)
              );
          }
        });

        // FIXME get rid of this watch and communicate by event listener
        // watch for extent changes and change the chart's domains accordingly
        $scope.$watchCollection('extent', function(extent, oldExtent) {
          if (extent && extent.length && $scope.data.length && $scope.domain.x.length && $scope.domain.y.length) {
          chartElement
            .call(chart
              .xScale(($scope.extent ? $scope.extent : $scope.domain.x))
              .yScale($scope.domain.y)
            );
          }
          // if (extent && $scope.data.length) {
          //   if (extent.length) {
          //     chartElement
          //       .call(chart.extent(extent)) // set chart domain to extent
          //       .call(chart.yScale($scope.domain.y));
          //   } else {
          //     chartElement
          //       .call(chart.extent($scope.domain.x))
          //       .call(chart.yScale($scope.domain.y));
          //   }
          // }
        });
      }
    }
  }
]);

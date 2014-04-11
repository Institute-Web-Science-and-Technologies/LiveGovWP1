(function() {
  'use strict';

  app.directive('brush', [function () {
    var brush = d3.custom.chartBrush();
    return {
      restrict: 'E',
      scope: {
        domain: '=', extent: '=', onBrushExtent: '&', loadMoreData: '&'
      },
      link: function ($scope, $element, $attributes) {
        var brushElement = d3.select($element[0]);

        brush.on('brushed', function (d, i) {
          $scope.onBrushExtent({args:d});
        });

        brush.on('brushended', function (d, i) {
          $scope.loadMoreData({args: d});
        });

        brush.on('ready', function (d, i) {
          unwatchDomain();
        });

        var unwatchDomain =
        $scope.$watchCollection('domain', function (val, oldVal) {
          brushElement.datum({domain:val}).call(brush);
        });

        $scope.$watchCollection('extent', function (val, oldVal) {
          brushElement.call(brush.extent(val));
        });
      }
    };
  }]);

  app.directive('chart', [function () {
    var chart = d3.custom.lineChart();
    return {
      restrict: 'E',
      scope: {
        data: '=', domain: '=', extent: '=', onBrushExtent: '&', loadMoreData: '&'
      },
      link: function ($scope, $element, $attributes) {
        var chartElement = d3.select($element[0]);

        chart.on('brushended', function(d, i) {
          $scope.loadMoreData({args:d});
          $scope.onBrushExtent({args:d}); // update scope
        });

        // chart.on('ready', function (d, i) {
        //   console.info('unwatching domain!');
        //   unwatchDomain();
        // });

        // var unwatchDomain =
        // $scope.$watchCollection('domain', function (val, oldVal) {
        //   if (val && val.x.length && val.y.length) {
        //     chartElement.call(chart.xScale(val.x));
        //     chartElement.call(chart.yScale(val.y));
        //     console.info('domain action!');
        //   }
        // });

        $scope.$watchCollection('data', function (val, oldVal) {
          if ($scope.data.length && $scope.domain.x.length && $scope.domain.y.length) {
            chartElement.datum({data:$scope.data, domain:$scope.domain}).call(chart);
          }
        });

        $scope.$watchCollection('extent', function (val, oldVal) {
          chartElement.call(chart.x($scope.extent));
        });


      }
    };
  }]);

}());

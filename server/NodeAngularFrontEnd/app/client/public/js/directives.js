(function() {
  'use strict';

  // FIXME this is a dirty workaround to update the brush extent directly from
  // the chart directive. better require the brushes directive controller in
  // the chart directive. if then the brush/chart element would be called
  // directly from the other directive, we could avoid passing the extent to
  // the root scope completely. huge simplification and probably performance
  // gain.
  var brush = d3.custom.chartBrush();
  var brushElement;

  app.directive('brush', [function () {
    return {
      restrict: 'E',
      scope: {
        data: '=', domain: '=', extent: '=', onBrushExtent: '&', loadMoreData: '&'
      },
      // priority: 2,
      // controller: function () {
      //   this.setExtent = function (extent) {
      //     brushElement.call(brush.extent(extent));
      //   };
      // },
      link: function ($scope, $element, $attributes) {
        brushElement = d3.select($element[0]);

        brush.on('brushed', function (d, i) {
          // console.log('BRUSH EXTENT 2:', d, $scope.extent);
          $scope.onBrushExtent({args:d});
        });

        brush.on('brushended', function (d, i) {
          $scope.loadMoreData({args: d});
        });

        brush.on('ready', function (d, i) {
          unwatchDomain();
        });

        var unwatchDomain =
        $scope.$watchCollection('domain', function (domain, oldDomain) {
          if (domain && domain.x.length && domain.y.length) {
            // console.info('BRUSH 1: drawing brush with domain', domain);
            brushElement.datum({domain:domain}).call(brush);
          }
        });
      }
    };
  }]);

  app.directive('chart', [function ($window) {
    var chart = d3.custom.lineChart();
    return {
      restrict: 'E',
      scope: {
        data: '=', domain: '=', extent: '=', onBrushExtent: '&', loadMoreData: '&'
      },
      // priority: 1,
      // require: '^brush',
      link: function ($scope, $element, $attributes) { // brushCtrl
        var chartElement = d3.select($element[0]);

        chart.on('brushended', function(d, i) {
          // console.log('CHART EXTENT 2:', d, $scope.extent);
          $scope.onBrushExtent({args:d});
          $scope.loadMoreData({args:d});
          brushElement.call(brush.extent(d)); // directly call the brush to change it's extent
          // brushCtrl.setExtent(d);
        });

        chart.on('ready', function (d, i) {
            // unwatchDomain();
          });

        // draw the chart as soon as the data has arrived
        $scope.$watchCollection('data', function (data, oldData) {
          if (data && data.length) {
            chartElement.datum({data:data}).call(chart);
          }
        });

        // watch for domain changes
        var unwatchDomain =
        $scope.$watchCollection('domain', function (domain, oldDomain) {
          if (domain && domain.x.length && domain.y.length) {
            chartElement.call(chart.yScale(domain.y));
            chartElement.call(chart.xScale(domain.x));
          }
        });

        // watch for extent changes and change the charts x-domain accordingly
        $scope.$watchCollection('extent', function (extent, oldExtent) {
          if (extent && extent.length) {
            // console.log('CHART EXTENT 4:', extent, oldExtent);
            chartElement.call(chart.extent(extent));
          }
        });
      }
    };
  }]);
}());

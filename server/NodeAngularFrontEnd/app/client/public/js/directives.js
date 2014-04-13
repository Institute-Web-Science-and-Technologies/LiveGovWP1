(function() {
  'use strict';

  var brush = d3.custom.chartBrush();
  var brushElement;

  app.directive('brush', [function () {
    return {
      // priority: 2,
      restrict: 'E',
      scope: {
        domain: '=', extent: '=', onBrushExtent: '&', loadMoreData: '&'
      },
      // controller: function () {
      //   this.setExtent = function (extent) {
      //     console.log("brush ctrl says:", extent);
      //     brushElement.call(brush.extent(extent));
      //   };
      // },
      link: function ($scope, $element, $attributes) {
        brushElement = d3.select($element[0]);

        brush.on('brushed', function (d, i) {
          // EXTENT 2:
          console.log('BRUSH EXTENT 2:', d, $scope.extent);
          $scope.onBrushExtent({args:d});
        });

        brush.on('brushended', function (d, i) {
          // $scope.loadMoreData({args: d});
        });

        brush.on('ready', function (d, i) {
          unwatchDomain();
        });

        // STEP 1: create the brush
        // domain = { 'x': Array[2], 'y': Array[2] }
        var unwatchDomain =
        $scope.$watchCollection('domain', function (domain, oldDomain) {
          if (domain && domain.x.length && domain.y.length) {
            console.info('BRUSH 1: drawing brush with domain', domain);
            brushElement.datum({domain:domain}).call(brush);
          }
        });

        // how to check if brush is already drawn?

        // extent = Array[2]
        // $scope.$watchCollection('extent', function (extent, oldExtent) {
        //   if (extent && extent.length) {
        //     console.log('BRUSH EXTENT 4:', extent, oldExtent);
        //     brushElement.call(brush.extent(extent));
        //   }
        // });
}
};
}]);

app.directive('chart', [function () {
  var chart = d3.custom.lineChart();
  return {
    // priority: 1,
    restrict: 'E',
    // require: '^brush',
    scope: {
      data: '=', domain: '=', extent: '=', onBrushExtent: '&', loadMoreData: '&'
    },
    link: function ($scope, $element, $attributes, brushCtrl) {
      var chartElement = d3.select($element[0]);

      chart.on('brushended', function(d, i) {
          // EXTENT 2:
          console.log('CHART EXTENT 2:', d, $scope.extent);
          $scope.onBrushExtent({args:d});
          // $scope.loadMoreData({args:d});
          // CALL BRUSH SCOPE FROM HERE AND CHANGE EXTENT!
          // brushCtrl.setExtent(d);
          // brushElement.call(brush.extent(extent))
          brushElement.call(brush.extent(d));
        });

      chart.on('ready', function (d, i) {
          // console.info('unwatching y domain!');
          // unwatchDomain();
        });

      var unwatchDomain =
      $scope.$watchCollection('domain', function (domain, oldDomain) {
        if (unwatchData && domain && domain.x.length && domain.y.length) {
          chartElement.call(chart.yScale(domain.y));
          chartElement.call(chart.xScale(domain.x));
            // console.info('updating y domain!');
          }
        });

        // STEP 1: as soon as chart data arrives, draw the chart
        var unwatchData =
        $scope.$watchCollection('data', function (data, oldData) {
          // console.log($scope.data.length);
          // if (data && data.length && $scope.domain.x.length && $scope.domain.y.length) {
            if (data && data.length) {
              chartElement.datum({data:data}).call(chart);
            // chartElement.datum({data:$scope.data, domain:$scope.domain}).call(chart);
          }
        });

        // if the brush extent changes, change the charts x-domain accordingly
        $scope.$watchCollection('extent', function (extent, oldExtent) {
          if (extent && extent.length) {
            console.log('CHART EXTENT 4:', extent, oldExtent);
            chartElement.call(chart.extent(extent));
          }
        });

        // $scope.$watchCollection('extent', function (val, oldVal) {
        //   if (val !== oldVal) {
        //     // console.log('extent updated!', val);
        //     chartElement.call(chart.x($scope.extent));
        //   }
        // });
}
};
}]);
}());

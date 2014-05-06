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

app.directive('map', [function () {
  return {
    restrict: 'E',
    scope: {
      data: '='
    },
    link: function ($scope, $element, $attributes) {





      // Leaflet Chloropleth Legend Example
      var legend = L.control({
        position: 'topright'
      });

      legend.onAdd = function (map) {
        var div = L.DomUtil.create('div', 'info legend leaflet-bar', this.legend);

        // sort activities before generating legend
        var a = sortActivities(activities).filter(function (n) { return n; });

        for (var i = 0; i < a.length; i++) {
          div.innerHTML += '<i style="background:' + getColor(a[i]) + '"></i>' + a[i] + '<br>';
        }
        return div;
      };

      function resetHighlight(e) {
        geoJson.resetStyle(e.target);
      }

      function onEachFeature(feature, layer) {
        // Create a popup for each feature
        if (feature.properties) {
          var popupString = '<div class="popup">';
          for (var k in feature.properties) {
            var v = feature.properties[k];
            if (k == 't0' || k == 't1') v = moment.unix(parseInt(v)).utc().format("HH:mm:ss");
            if (k == 'distance')        v = Math.round(v * 100, 12) / 100 + " m";
            if (k == 'duration')        v = moment.duration(v).humanize();
            if (k == 'speed')           v = v + ' km/h';
            popupString += k + ': ' + v + '<br />';
          }
          popupString += '</div>';
          layer.bindPopup(popupString, {
            maxHeight: 200
          });
        }
        // Highlight feature on mouseover
        layer.on({
          mouseover: highlightFeature,
          mouseout: resetHighlight,
        });
      }

      // returns activities in a manually sorted order
      function sortActivities(activities) {
        var a = ['driving', 'running', 'walking', 'standing', 'sitting', 'on table', 'unknown'];
        return a.map(function (d) {
          return activities[activities.indexOf(d)];
        });
      }

      function getColor(d) {
        switch (d) {
          case 'driving'  : return '#377eb8';
          case 'running'  : return '#e41a1c';
          case 'walking'  : return '#ff7f00';
          case 'standing' : return '#4daf4a';
          case 'sitting'  : return '#984ea3';
          case 'on table' : return '#a65628';
          case 'unknown'  : return '#777777';
          case null       : return '#777777';
        }
      }

      function style(feature) {
        return {
          weight: 8,
          opacity: 0.7,
          color: getColor(feature.properties.activity),
        };
      }

      function highlightFeature(e) {
        var layer = e.target;

        layer.setStyle({
          weight: 10,
          opacity: 1
        });

        if (!L.Browser.ie && !L.Browser.opera) {
          layer.bringToFront();
        }
      }




      // SETUP MAP

      var map = new L.Map('map', {
        zoom: 15,
        minZoom: 8,
        maxZoom: 24,
        scrollWheelZoom: false
      });

      // CloudMade
      L.tileLayer('http://{s}.tile.cloudmade.com/{key}/22677/256/{z}/{x}/{y}.png?token={token}', {
        attribution: 'Map data &copy; 2011 OpenStreetMap contributors, Imagery &copy; 2011 CloudMade',
        key: 'a04e905a7d8448d8b412b7371dfa21be', // rene
        token: '88fd62dfa481446aa8ac3c22bcb64d21'
      }).addTo(map);

      var data = $scope.data; // data is feature collection

      if (!data || data.length === 0) { console.log('no data :/'); return; }

      console.log(data);

      var activities = data.features.map(function (d) {
        return d.properties.activity;
      });

      activities[activities.indexOf(null)] = "unknown"; // change null to "unknown"

      activities = activities.sort().filter(function (el, i, a) {
        if (i == a.indexOf(el)) return 1;
        return 0;
      }); // sort unique

      activities.filter(function (n) {
        return n;
      }); // remove undefined

      legend.addTo(map);

      // Draw geoJSON object to map
      var geoJson = L.geoJson(data, {
        style: style,
        onEachFeature: onEachFeature
      }).addTo(map);

      // Zoom map to fit our route
      map.fitBounds(geoJson.getBounds());

    } // end link
  }; // end return
}]);
/*** @jsx React.DOM */

var Map = React.createClass({
  getDefaultProps: function() {
    return {
      colorized: true
    };
  },

  componentDidMount: function() {
    var map = L.mapbox.map('map', 'rene.i6mdi15p'); // mapbox id [username].[project]

    var harLayer = L.mapbox.featureLayer().addTo(map);
    harLayer.setGeoJSON(this.props.featureCollection);

    harLayer.on('mouseover', function(e) {
      console.log(e.layer.feature.properties.tag);
      e.layer.openPopup();
    });

    harLayer.on('mouseout', function(e) {
      e.layer.openPopup();
    });

    map.fitBounds(harLayer.getBounds());

      // var highlightFeature = function(e) {
      //   var layer = e.target;

      //   layer.setStyle({
      //     weight: 3,
      //     opacity: 1,
      //     color: this.props.colorized ? getColor(feature.properties.tag) : getColor('running')
      //   });

      // }.bind(this);

      // function onEachFeature(feature, layer) {
      //   layer.on({
      //     mouseover: function(e) {
      //       e.layer.openPopup();
      //       highlightFeature
      //     },
      //     mouseout: function(e) {
      //       e.layer.closePopup();
      //       resetHighlight
      //     }
      //   });
      // }

      // function resetHighlight(e) {
      //   geojson.resetStyle(e.target);
      // }

      function getColor(d) {
        switch (d) {
          case 'driving'  : return '#377eb8';
          case 'running'  : return '#e41a1c';
          case 'walking'  : return '#ff7f00';
          case 'standing' : return '#4daf4a';
          case 'sitting'  : return '#984ea3';
          case 'on table' : return '#a65628';
          case 'unknown'  : return '#777777';
          case void 0     : return '#333333';
        }
      }

      var style = function(feature) {
        return {
          weight: 3,
          opacity: 1,
          color: this.props.colorized ? getColor(feature.properties.tag) : getColor()
        };
      }.bind(this);

      var geojson = L.geoJson(this.props.featureCollection, {
        style: style,
        onEachFeature: onEachFeature
      }).addTo(map);

      // map.fitBounds(geojson.getBounds());

      // map.scrollWheelZoom.disable();

      // new L.Control.Zoom({ position: 'topright' }).addTo(map);
      function onEachFeature(feature, layer) {

        // create a popup for each feature
        if (feature.properties) {
          var popupString = '<div class="popup">';
          for (var k in feature.properties) {
            var v = feature.properties[k];
            if (k == 't0' || k == 't1') v = moment.unix(parseInt(v)).utc().format('HH:mm:ss');
            if (k == 'distance')        v = Math.round(v * 100, 12) / 100 + ' m';
            if (k == 'duration')        v = moment.duration(v).humanize();
            if (k == 'speed')           v = v + ' km/h';
            popupString += k + ': ' + v + '<br />';
          }
          popupString += '</div>';
          layer.bindPopup(popupString, {
            maxHeight: 200
          });
        }

        // highlight feature on mouseover
        // layer.on({
        //   mouseover: highlightFeature,
        //   mouseout: resetHighlight,
        // });
      }
  },

  render: function() {
    return <div id='map' />;
  }
});

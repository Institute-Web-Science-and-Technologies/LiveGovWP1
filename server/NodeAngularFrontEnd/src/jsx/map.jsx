/*** @jsx React.DOM */

// http://geojsonlint.com <3
// https://github.com/mapbox/simplestyle-spec/tree/master/1.1.0
// https://www.mapbox.com/maki/
// https://www.ietf.org/rfc/rfc2119.txt
// https://www.mapbox.com/mapbox.js/example/v1.0.0/custom-marker/
// https://www.mapbox.com/mapbox.js/example/v1.0.0/scaled-markers/

var Map = React.createClass({

  getDefaultProps: function() {
    return {
      har: false,
      harColors: {
          'driving'  : '#53db5d',
          'running'  : '#6079df',
          'walking'  : '#4f91d7',
          'standing' : '#52c3d7',
          'sitting'  : '#53db89',
          'on table' : '#d88b3d',
          'unknown'  : '#777777'
      },
      layerStyles: {
        extentLayer : {weight : 4, opacity : 1, color : '#fc4807'},
        gpsLayer    : {weight : 3, opacity : 1, color : '#333'},
        harLayer    : {weight : 3, opacity : 1 },
        none        : {weight : 0, }
      },
      markerStyle: {
        radius: 12,
        fillColor: "#ff7800",
        color: "#000",
        weight: 1,
        opacity: 1,
        fillOpacity: 0.8
      }
    };
  },

  getInitialState: function() {
    return {}; // initialize empty state to prevent undefined error
  },

  componentDidUpdate: function(prevProps, prevState) {
    console.log('componentDidUpdate', this.props.extent);

    /////////////////////////
    // HANDLE EXTENT LAYER //
    /////////////////////////

    if (this.props.extent.length) {
      this.state.extentLayer.setGeoJSON(this.generateExtentGeoJSON());
      this.state.extentLayer.setStyle(this.props.layerStyles.extentLayer);
      this.state.map.fitBounds(this.state.extentLayer.getBounds(), {padding: [10,10]});
    } else {
      this.state.extentLayer.setStyle(this.props.layerStyles.none);
      this.state.map.fitBounds(this.state.gpsLayer.getBounds());
    }
  },

  componentDidMount: function() {
    console.log('componentDidMount');
    // when the component did mount and the dom node '#map' is available,
    // create the map and all layers and keep them in the components state

    var map         = L.mapbox.map('map', 'rene.i6mdi15p'),
        gpsLayer    = L.mapbox.featureLayer().addTo(map),
        harLayer    = L.mapbox.featureLayer().addTo(map),
        extentLayer = L.mapbox.featureLayer().addTo(map);

    ////////////////////////////////////
    // RENDER LAYERS AND APPLY STYLES //
    ////////////////////////////////////

    // gps layer: one single geojson linestring
    gpsLayer.setGeoJSON(this.generateGPSGeoJSON());
    gpsLayer.setStyle(this.props.layerStyles.gpsLayer);

    // har layer: feature collection containing line strings
    if (this.props.har) {
      harLayer.setGeoJSON(this.generateHARGeoJSON());
      harLayer.setStyle(this.props.layerStyles.harLayer);
    }

    // render tags as circles with a popup
    this.renderTags(map);

    // zoom to fit gps layer
    map.fitBounds(gpsLayer.getBounds());

    // make map and layers available through state
    this.setState({
      map: map,
      gpsLayer    : gpsLayer,
      harLayer    : harLayer,
      extentLayer : extentLayer,
    })
  },

  render: function() {
    return (
      <div className='pure-g'>
        <div className='pure-u-24-24'>
          <ChartHeader sensorName='GPS' />
          <pre>{this.props.extent.toString()}</pre>
          <div className='cell map'>
            <div id='map' />
          </div>
        </div>
      </div>
    );
  },

  ////////////////////////
  // RENDER TAGS TO MAP //
  ////////////////////////

  renderTags: function(map) {
    function onEachFeature(feature, layer) {
      layer.bindPopup(feature.properties.popupContent);
    }

    L.geoJson(this.generateTagsGeoJSON(), {
      style: function (feature) {
        return feature.properties && feature.properties.style;
      },

      onEachFeature: onEachFeature,

      pointToLayer: function (feature, latlng) {
        return L.circleMarker(latlng, this.props.markerStyle);
      }.bind(this)
    }).addTo(map);
  },

  /////////////////////////////////////////
  // METHODS TO GENERATE GEOJSON OBJECTS //
  /////////////////////////////////////////

  // (1) unify gps coordinates
  // (2) split coordinates by activity
  // (3) join activities to prevent gaps in rendering

  generateGPSGeoJSON: function() {
    var gps = this.props.data.sensor_gps;

    return {
      type: 'LineString',
      coordinates: _(gps)
        .map(function(coord) { return coord.lonlat.coordinates; })
        .unique(function(coord) { return coord.toString(); })
        .value()
    }
  },

  generateHARGeoJSON: function() {
    var har = this.sum(this.props.data.sensor_har);
    var gps = this.props.data.sensor_gps;

    var features = har.map(function(activity) {
      return {
        type: 'Feature',
        geometry: {
          type: 'LineString',
          coordinates: this.props.data.sensor_gps
            .filter(function(d) { return d.ts >= activity.start && d.ts <= activity.stop; })
            .map(function(d) { return d.lonlat.coordinates; })
        },
        properties: {
          start: activity.start,
          stop: activity.stop,
          duration: activity.stop - activity.start,
          activity: activity.tag,
          stroke: this.props.harColors[activity.tag],
        }
      }
    }.bind(this));

    // connect each line string with the next one: push the first coordinate
    // of the next linestring to it's predecessors coordinate array
    _.forEach(features, function(c,i,a) {
      if (a[i + 1]) a[i].geometry.coordinates.push(a[i + 1].geometry.coordinates[0]);
    })

    // remove any falsy elements in each linestrings coordinates array
    _.forEach(features, function(feature) {
      return feature.geometry.coordinates = _.compact(feature.geometry.coordinates);
    })

    // return the features wrapped in a feature collection
    return {
      type: 'FeatureCollection',
      features: features
    };
  },

  generateTagsGeoJSON: function() {
    var tags = this.props.data.sensor_tags;
    var gps = this.props.data.sensor_gps;

    return {
      type: 'FeatureCollection',
      features: tags.map(function(tag) {
        return {
          type: 'Feature',
          geometry: gps.
            filter(function(coord) { return coord.ts >= tag.ts; })[0].lonlat,
          properties: {
            tag: tag.tag,
            ts: tag.ts,
            style: {
                weight: 2,
                color: "#999",
                opacity: 1,
                fillColor: "#B0DE5C",
                fillOpacity: 0.8,
            },
            popupContent: "<strong>Tag</strong>: " + tag.tag
          }
        };
      })
    };
  },

  generateExtentGeoJSON: function() {
    var extent = this.props.extent;
    var gps = this.props.data.sensor_gps;

    return {
      type: 'LineString',
      coordinates: gps
        .filter(function(d) { return d.ts >= extent[0] && d.ts <= extent[1]; })
        .map(function(d) { return d.lonlat.coordinates; })
    }
  },

  ///////////////////////////////
  // XXX CHECK FOR CORRECTNESS //
  ///////////////////////////////

  // summarize har tags
  sum: function(har) {
    return har.slice().filter(function(c, i, a) {
      return a[i - 1] ? a[i].tag !== a[i - 1].tag : true;
    }).map(function(c, i, a) {
      return a[i + 1] ? {
        start: a[i].ts,
        stop: a[i + 1].ts,
        tag: a[i].tag
      } : {
        start: a[i].ts,
        stop: har[har.length - 1].ts,
        tag: a[i].tag
      };
    }).filter(function(d) {
      return d.start !== d.stop;
    });
  },

});

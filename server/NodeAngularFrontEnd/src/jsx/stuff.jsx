/** @jsx React.DOM */

/////////////////////////////////////////////////////////////////////////////
// MIXINS                                                                  //
/////////////////////////////////////////////////////////////////////////////

var CalculateDomain = {
  calculateDomain: function(data, props) {
    return d3.extent(Object.keys(data).filter(function(d) {
      return data[d].length && data[d][0].hasOwnProperty(props[0]); // check all props (TODO)
    }).map(function(sensor) {
      return data[sensor].select(props);
    }).flatten());
  }
};

var SummarizeHarTags = {
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
  }
}

var GenerateFeatureCollection = {
  featureCollection: function(gps, har) {
    var fc = {
      type: 'FeatureCollection',
      features: []
    };

    function createFeature(properties, coordinates) {
      return {
        type: 'Feature',
        geometry: {
          type: 'LineString',
          coordinates: coordinates // [g0, g1]
        },
        properties: properties
      };
    }

    fc.features = har.map(function(properties) {
      return createFeature(properties, gps.filter(function(g) {
        return g.ts >= properties.start && g.ts <= properties.stop;
      }).map(function(position) {
        return position.lonlat.coordinates;
      }));
    }).filter(function(d) {
      return d.geometry.coordinates.length;
    });

    fc.features.forEach(function(c, i, a) {
      if (a[i + 1]) { a[i].geometry.coordinates.push(a[i + 1].geometry.coordinates[0]); }
    });

    return fc;
  }
};

/////////////////////////////////////////////////////////////////////////////
// PROTOTYPES                                                              //
/////////////////////////////////////////////////////////////////////////////

// flatten an array
Array.prototype.flatten = function() {
  return [].concat.apply([], this);
};

// return array of property values, e.g. [[objs],...] -> [props]
Array.prototype.select = function(props) {
  return this.flatten().map(function(d) {
    return props.map(function(p) {
      return d[p];
    });
  }).flatten();
};

Array.prototype.dataSeries = function(props) {
  return this.map(function(d) {
    return props.map(function(p) {
      return d[p];
    });
  });
};

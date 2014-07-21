/*** @jsx React.DOM */

/////////////////////////////////////////////////////////////////////////////
// CHART INFO STUFF                                                        //
/////////////////////////////////////////////////////////////////////////////

// chart title is clickable to show/hide chart
var ChartHeader = React.createClass({displayName: 'ChartHeader',
  showHideSensorChart: function(e) {
    var nodes = e.currentTarget.parentNode.getElementsByTagName('div');
    _.each(nodes, function(node) {
      node.classList.contains('hidden') ? node.classList.remove('hidden') : node.classList.add('hidden');
    })
    var dots = e.currentTarget.lastChild;
    dots.classList.contains('hidden') ? dots.classList.remove('hidden') : dots.classList.add('hidden');
  },

  render: function() {
    return (
      React.DOM.h1({className: "chartHeader", onClick: this.showHideSensorChart}, 
        this.props.sensorName, 
        React.DOM.small({className: "hidden"}, " ...")
      )
    );
  }
});

var ChartLegend = React.createClass({displayName: 'ChartLegend',
  render: function() {
    return (
      React.DOM.div(null, 
        React.DOM.ul({className: "legend"}, 
          React.DOM.li(null, React.DOM.a({className: "pure-button"}, "Legend:")), 
          React.DOM.li(null, React.DOM.a({className: "pure-button x"}, "x")), 
          React.DOM.li(null, React.DOM.a({className: "pure-button y"}, "y")), 
          React.DOM.li(null, React.DOM.a({className: "pure-button z"}, "z"))
        )
      )
    );
  }
});

var ChartExport = React.createClass({displayName: 'ChartExport',
  render: function() {
    return (
      React.DOM.div(null, 
        React.DOM.ul({className: "export"}, 
          React.DOM.li(null, React.DOM.a({className: "pure-button", href: '/trips/' + this.props.tripId + '/' + this.props.sensor, download: 'livegov-' + this.props.tripId + '-' + this.props.sensor + '.json', title: 'approx. ' + (Math.round(this.props.availableData / 10.25 / 1024)) + ' MB'}, "Export"))
        )
      )
    );
  }
});

var ChartDataCount = React.createClass({displayName: 'ChartDataCount',
  render: function() {
    return (
      React.DOM.div(null, 
        React.DOM.ul({className: "count"}, 
          React.DOM.li(null, React.DOM.a({className: "pure-button"}, "Data Count:")), 
          React.DOM.li(null, React.DOM.a({className: "pure-button"}, this.props.loadedData, "/", this.props.availableData))
        )
      )
    );
  }
});

var Brush = React.createClass({displayName: 'Brush',
  render: function() {
    return (
      React.DOM.g({className: "brush"})
    );
  },

  componentDidUpdate: function() {
    this.updateBrush();
  },

  updateBrush: function() {
    var xScale = d3.time.scale()
      .range([0, this.props.width]);

    var hasExtent = !!this.props.extent.length;
    xScale.domain(hasExtent ? this.props.extent : this.props.xDomain);

    var brush = d3.svg.brush();
    brush.x(xScale)
      .on('brushend', this.onBrushend.bind(this, brush));

   d3.select(this.getDOMNode())
     .call(brush)
     .selectAll('rect')
     .attr('height', this.props.height);
  },

  onBrushend: function(brush) {
    this.props.onBrush(brush.empty() ? [] : brush.extent().map(function(d) { return +d; }));
    d3.selectAll('.brush').call(brush.clear());
  },
});

var Circles = React.createClass({displayName: 'Circles',
  getDefaultProps: function() {
    return {radius: 1.5};
  },

  render: function() {
    var circles = Object.keys(this.props.data).map(function(key) {
      return React.DOM.g({key: key, className: key}, 
        this.props.data[key].map(function(d,i) {
          return React.DOM.circle({key: i, cx: d[0], cy: d[1], r: this.props.radius, className: 'circle circle' + key});
        }.bind(this))
      )
    }.bind(this));
    return React.DOM.g(null, circles);
  },
});

var Paths = React.createClass({displayName: 'Paths',
  getDefaultProps: function() {
    return {width: 2};
  },
  render: function() {
    var paths = Object.keys(this.props.data).map(function(key) {
      return React.DOM.path({key: key, width: this.props.width, d: this.props.data[key], className: 'path path' + key});
    }.bind(this))
    return React.DOM.g(null, paths);
  }
});

var Chart = React.createClass({displayName: 'Chart',
  getDefaultProps: function() {
    return {
      margin: {top: 20, right: 10, bottom: 30, left: 30}
    }
  },

  getInitialState: function() {
    return {
      width: window.innerWidth - this.props.margin.left - this.props.margin.right,
      height: window.innerHeight - this.props.margin.top - this.props.margin.bottom,
    };
  },

  componentDidMount: function() {
    var resizeChart = function() {
      this.setState({
        width: this.getDOMNode().offsetWidth - this.props.margin.left - this.props.margin.right,
        height: this.getDOMNode().offsetHeight - this.props.margin.top - this.props.margin.bottom
      });
      this.clipPath();
      this.axis();
    }.bind(this);

    window.addEventListener('resize', function() { resizeChart(); }.bind(this));
    resizeChart();
  },

  componentDidUpdate: function(prevProps, prevState) {
    this.axis();
  },

  // append clip path to svg > devs or update it
  clipPath: function() {
    var e = d3.select(this.getDOMNode()).select('defs');

    if (e.select('#clip').empty()) {
      e.append('svg:clipPath').attr('id', 'clip');
    }

    if (e.select('#clip').select('rect').empty()) {
      e.select('#clip').append('rect');
    }

    e.select('#clip').select('rect')
      .attr('width', this.state.width)
      .attr('height', this.state.height);

    d3.select(this.getDOMNode()).select('g.chart')
      .attr('clip-path', 'url(#clip)');
  },

  // create x/y-axis on the appropiate dom node
  axis: function() {
    var xScale = d3.time.scale().range([0, this.state.width]);
    xScale.domain(this.props.extent.length ? this.props.extent : this.props.xDomain);
    var xAxis = d3.svg.axis().scale(xScale).orient('bottom').ticks(Math.max(this.state.height / 20, 2));

    var yScale = d3.scale.linear().range([this.state.height, 0]);
    yScale.domain(this.props.yDomain);
    var yAxis = d3.svg.axis().scale(yScale).orient('left').ticks(Math.max(this.state.height / 20, 2));

    d3.select(this.getDOMNode()).select('.x.axis').call(xAxis);
    d3.select(this.getDOMNode()).select('.y.axis').call(yAxis);
  },

  render: function() {
    var xScale = d3.time.scale().range([0, this.state.width]);
    var yScale = d3.scale.linear().range([this.state.height, 0]);

    xScale.domain(this.props.extent.length ? this.props.extent : this.props.xDomain);
    yScale.domain(this.props.yDomain);

    var line = d3.svg.line().interpolate('linear')
      .x(function(d) { return xScale(d[0]); })  // d[0]: Number timestamp
      .y(function(d) { return yScale(d[1]); }); // d[1]: Number sensor value

    var axisSpread = function(x) { return { x:x, y:-x }; }(0)
    var translate = function(x, y) { return 'translate(' + x + ',' + y + ')' }

    function dataSeries(data, props) {
      return data.map(function(d) {
        return props.map(function(p) {
          return d[p];
        });
      });
    };

    var circleData = function(values) {
      var r = {};
      values.forEach(function(val) {
        r[val] = dataSeries(this.props.data, ['ts', val]).map(function(d) {
          return [xScale(d[0]), yScale(d[1])];
        }.bind(this));
      }.bind(this));
      return r;
    }.bind(this);

    var pathData = function(values) {
      var r = {};
      values.forEach(function(val) {
        r[val] = line(dataSeries(this.props.data, ['ts', val]))
      }.bind(this));
      return r;
    }.bind(this);

    return (
      React.DOM.div({className: "chart"}, 
        React.DOM.svg(null, 
          React.DOM.defs(null), 
          React.DOM.g({transform: translate(this.props.margin.left, this.props.margin.top)}, 
            React.DOM.text({className: "x label", textAnchor: "end", x: this.state.width, y: this.state.height - 2}, "time"), 
            React.DOM.text({className: "y label", textAnchor: "end", y: "2", transform: "rotate(-90)"}, "value"), 
            React.DOM.g({className: "x axis", transform: translate(0, this.state.height)}), 
            React.DOM.g({className: "y axis"})
          ), 
          React.DOM.g({className: "chart", width: this.state.width, height: this.state.height, transform: translate(this.props.margin.left, this.props.margin.top)}, 
            Brush({width: this.state.width, height: this.state.height, extent: this.props.extent, xDomain: this.props.xDomain, yDomain: this.props.yDomain, onBrush: this.props.onBrush}), 
            Paths({data: pathData(['x', 'y', 'z'])}), 
            Circles({data: circleData(['x', 'y', 'z'])})
          )
        )
      )
    )
  },
});

/*** @jsx React.DOM */

var HarTable = React.createClass({displayName: 'HarTable',
  render: function() {
    var rows = this.props.data.map(function(d, i) {
      return(
        React.DOM.tr({key: i, className: d.tag}, 
          React.DOM.td(null, moment(d.start).format('HH:mm:ss')), 
          React.DOM.td(null, moment(d.stop).format('HH:mm:ss')), 
          React.DOM.td(null, d.tag)
        )
      );
    });

    return (
      React.DOM.table(null, 
        React.DOM.thead(null, React.DOM.th(null, "start"), React.DOM.th(null, "stop"), React.DOM.th(null, "tag")), 
        React.DOM.tbody(null, rows)
      )
    );
  }
});

var HarTags = React.createClass({displayName: 'HarTags',
  render: function() {
    var tags = this.props.tags.map(function(tag, i) {
      return(
        React.DOM.li({key: i, className: 'harTag ' + tag}, 
          tag
        )
      );
    });

    return (
      React.DOM.ul({className: "harTags"}, tags)
    );
  }
});

var HarBar = React.createClass({displayName: 'HarBar',
  render: function() {
    var xScale = d3.time.scale().range([0, this.props.width]);
    xScale.domain(this.props.xDomain);

    var x = 0;
    var rects = this.props.data.map(function(c, i, a) {
      var width = xScale(c.stop) - xScale(c.start);
      var tag = c.tag;
      var rect = React.DOM.g({key: i}, React.DOM.rect({className: tag, x: x, width: width, y: "0", height: "100%"}), React.DOM.title(null, tag));
      x = x + width;
      return rect;
    });

    return React.DOM.g(null, rects);
  }
});

var HarBrush = React.createClass({displayName: 'HarBrush',
  onBrush: function(brush) {
    this.props.onBrush(brush.empty() ? [] : brush.extent().map(function(d) { return +d; }));
  },

  componentDidMount: function() {
    var xScale = d3.time.scale().range([0, this.props.width]);
    xScale.domain(this.props.xDomain);

    var brush = d3.svg.brush();
    brush.x(xScale)
      .on('brushend', this.onBrush.bind(this, brush));

    if (this.props.extent.length) {
      brush.extent(this.props.extent);
    } else {
      brush.clear();
    }

    d3.select(this.getDOMNode())
      .call(brush)
      .selectAll('rect')
      .attr('height', '100%');

  },

  render: function() {
    return React.DOM.g({className: "harBrush"});
  }
});

var HarLegend = React.createClass({displayName: 'HarLegend',
  render: function() {

    return (
      React.DOM.ul({className: "legend"}, 
        React.DOM.li({className: "walking"}, "walking"), 
        React.DOM.li({className: "running"}, "running"), 
        React.DOM.li({className: "sitting"}, "sitting"), 
        React.DOM.li({className: "standing"}, "standing"), 
        React.DOM.li({className: "driving"}, "driving"), 
        React.DOM.li({className: "on_table"}, "on table"), 
        React.DOM.li({className: "unknown"}, "unknown")
      )
    );
  }
});

var Har = React.createClass({displayName: 'Har',
  // calculates domain with given values
  calculateDomain: function(data, props) {
    return d3.extent(Object.keys(data).filter(function(d) {
      return data[d].length && data[d][0].hasOwnProperty(props[0]); // check all props (TODO)
    }).map(function(sensor) {
      return data[sensor].select(props);
    }).flatten());
  },

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
  },

  onBrush: function(extent) {
    this.setState({extent: extent});
  },

  getInitialState: function() {
    return {
      xDomain: this.calculateDomain(this.props.trip.data, ['ts']),
      yDomain: this.calculateDomain(this.props.trip.data, ['x', 'y', 'z']),
      extent: [],
      tripDomain: this.calculateDomain(this.props.trip.data, ['ts']),
    };
  },

  render: function() {
    var data = this.props.trip.data;
    var gps = data.sensor_gps;
    var har = data.sensor_har ? this.sum(data.sensor_har) : [];
    var featureCollection = this.featureCollection(gps, har);
    var harDomain = this.calculateDomain([har], ['ts']); // NOTE: harDomain != xDomain

    var width = window.innerWidth;
    var height = window.innerHeight;

    var harTable = HarTable({className: "harTable", data: har});
    var harMap   = Map({gps: gps, har: har})
    var harBar   = HarBar({className: "harBar", xDomain: harDomain, extent: this.state.extent, width: width, data: har});
    var harBrush = HarBrush({className: "harBrush", xDomain: harDomain, extent: this.state.extent, width: width, onBrush: this.onBrush});

    var harTags = _.uniq(_.map(this.props.trip.data.sensor_har, function(d) { return d.tag; }));

    var charts =
      Object.keys(data)
      .filter(function(sensor) {
        return sensor.match(/accelerometer/);
      })
      .map(function(sensor) {
        return {
          name: sensor,
          data: data[sensor]
        };
      })
      .map(function(sensor) {
        return (
          React.DOM.div({key: sensor.name, className: "pure-g"}, 
            React.DOM.div({className: "pure-u-24-24"}, 
              ChartHeader({sensorName: sensor.name.replace(/sensor/, '').replace(/_/g, ' ')}), 
              ChartLegend(null), 
              ChartDataCount({loadedData: sensor.data.length, availableData: this.props.trip.count[sensor.name]}), 
              ChartExport({tripId: this.props.trip.id, sensor: sensor.name, availableData: this.props.trip.count[sensor.name]}), 
              Chart({
                data: sensor.data, 
                extent: this.state.extent, 
                xDomain: this.state.xDomain, 
                yDomain: this.state.yDomain, 
                onBrush: this.onBrush}
              )
            )
          )
        );
      }.bind(this))

    var map = (
      React.DOM.div({className: "pure-g"}, 
        React.DOM.div({className: "pure-u-24-24"}, 
          ChartHeader({sensorName: "GPS"}), 
          React.DOM.div({className: "cell map"}, 
            Map({featureCollection: featureCollection})
          )
        )
      )
    );

    if (!featureCollection.features.length) map = '';

    return (
      React.DOM.div({id: "har"}, 
        React.DOM.div({id: "main"}, 
          React.DOM.svg({height: "50px"}, 
            HarBar({className: "harBar", xDomain: this.state.xDomain, extent: this.state.extent, width: width, data: har}), 
            HarBrush({className: "harBrush", xDomain: this.state.xDomain, extent: this.state.extent, width: width, onBrush: this.onBrush})
          ), 
          HarTags({tags: harTags}), 
          map, 
          charts
        ), 
        React.DOM.div({id: "minimap"}
        )
      )
    );
  }
});

/*** @jsx React.DOM */

var ExtentInfo = React.createClass({displayName: 'ExtentInfo',
  render: function() {
    var extent = this.props.extent.map(function(d) { return moment(d).format('HH:mm:ss'); }).join(' - ');
    return React.DOM.ul({className: "extentInfo"}, React.DOM.li(null, extent));
  }
});

var DataInfo = React.createClass({displayName: 'DataInfo',
  render: function() {
    var a = this.props.trip.count;
    var b = _.mapValues(this.props.trip.data, function(d) { return d.length; }.bind(this));
    var c = _.zip(_.keys(a),_.zip(_.values(b), _.values(a)));
    return React.DOM.div(null);
  }
});

/*** @jsx React.DOM */

var Map = React.createClass({displayName: 'Map',
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
    return React.DOM.div({id: "map"});
  }
});

/*** @jsx React.DOM */

var Menu = React.createClass({displayName: 'Menu',
  getInitialState: function() {
    return {
      activity: false
    };
  },

  render: function() {
    var href = function(loc) {
      return this.props.tripId ? '/#/' + loc + '/' + this.props.tripId : '';
    }.bind(this);

    var className = function(loc) {
      var classes = '';
      classes += !this.props.tripId ? ' pure-menu-disabled' : '';
      classes += this.props.loc === loc ? ' pure-menu-selected' : '';
      return classes;
    }.bind(this);

    return (
      React.DOM.ul({className: "pure-menu pure-menu-open pure-menu-horizontal"}, 
        React.DOM.li(null, 
          React.DOM.a({href: "/"}, "Records")
        ), 
        React.DOM.li({className: className('raw')}, 
          React.DOM.a({href: href('raw')}, "Raw Data")
        ), 
        React.DOM.li({className: className('har')}, 
          React.DOM.a({href: href('har')}, "Activity Recognition")
        ), 
        React.DOM.li({className: className('sld')}, 
          React.DOM.a({href: href('sld')}, "Service Line Detection")
        )
      )
    );
  }
});

/*** @jsx React.DOM */

var Raw = React.createClass({displayName: 'Raw',
  // calculates domain with given values
  calculateDomain: function(data, props) {
    return d3.extent(Object.keys(data).filter(function(d) {
      return data[d].length && data[d][0].hasOwnProperty(props[0]); // check all props (TODO)
    }).map(function(sensor) {
      return data[sensor].select(props);
    }).flatten());
  },

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
  },

  onBrush: function(extent) {
    this.setState({extent: extent});
    this.props.loadMoreData({extent: extent}, function() {
      console.warn('updating yDomain');
      this.setState({
        yDomain: this.calculateDomain(nextProps.trip.data, ['x', 'y', 'z'])
      })
    });
  },

  getInitialState: function() {
    return {
      xDomain: this.calculateDomain(this.props.trip.data, ['ts']),
      yDomain: this.calculateDomain(this.props.trip.data, ['x', 'y', 'z']),
      extent: [],
    };
  },

  render: function() {
    var data = this.props.trip.data;
    var gps = data.sensor_gps;
    var har = data.sensor_har ? this.sum(data.sensor_har) : [];
    var featureCollection = this.featureCollection(gps, har);
    var harDomain = this.calculateDomain([har], ['ts']); // NOTE: harDomain != xDomain

    var width = window.innerWidth;
    var height = window.innerHeight;

    var harTable = HarTable({className: "harTable", data: har});
    var harMap   = Map({gps: gps, har: har})

    var harTags = _.uniq(_.map(this.props.trip.data.sensor_har, function(d) { return d.tag; }));

    var charts =
      Object.keys(data)
      .filter(function(sensor) {
        return !sensor.match(/_har|_gps/);
      })
      .map(function(sensor) {
        return {
          name: sensor,
          data: data[sensor]
        };
      })
      .map(function(sensor) {
        return (
          React.DOM.div({key: sensor.name, className: "pure-g"}, 
            React.DOM.div({className: "pure-u-24-24"}, 
              ChartHeader({sensorName: sensor.name.replace(/sensor/, '').replace(/_/g, ' ')}), 
              ChartLegend(null), 
              ChartDataCount({loadedData: sensor.data.length, availableData: this.props.trip.count[sensor.name]}), 
              ChartExport({tripId: this.props.trip.id, sensor: sensor.name, availableData: this.props.trip.count[sensor.name]}), 
              Chart({
                data: sensor.data, 
                extent: this.state.extent, 
                xDomain: this.state.xDomain, 
                yDomain: this.state.yDomain, 
                onBrush: this.onBrush}
              )
            )
          )
        );
      }.bind(this))

    var map = (
      React.DOM.div({className: "pure-g"}, 
        React.DOM.div({className: "pure-u-24-24"}, 
          ChartHeader({sensorName: "GPS"}), 
          React.DOM.div({className: "cell map"}, 
            Map({featureCollection: featureCollection, colorized: false})
          )
        )
      )
    );

    if (!featureCollection.features.length) map = '';


		return (
      React.DOM.div({id: "raw"}, 
        React.DOM.div({id: "main"}, 
          map, 
          charts
        )
      )
		);
	}
});

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

/*** @jsx React.DOM */

var TripsFilter = React.createClass({displayName: 'TripsFilter',
  render: function() {
    return (
      React.DOM.span(null, 
        React.DOM.input({type: "slider", min: "0", max: "100"})
      )
    );
  }
});

var Trips = React.createClass({displayName: 'Trips',

  updateTrip: function(id, event) {
    this.props.updateTrip(id, event.target.value);
  },

  selectTrip: function(id, event) {
    this.props.selectTrip(id);
  },

  deleteTrip: function(id, event) {
    if (confirm('Permanently delete trip ' + id + '?')) {
      this.props.deleteTrip(id);
    }
  },

  render: function() {

    var trips = this.props.trips;

    // table rows
    // var trips = this.props.trips.filter(function(trip) { return (trip.start < trip.stop); });
    var trips = this.props.trips.map(function(trip) {

      var warnings = [];

      if (trip.start > trip.stop) warnings.push('wrong timestamp');
      if (trip.start === trip.stop) warnings.push('zero duration');
      if (trip.user.match(/"/)) warnings.push('bad quoting');

      // var info = Object.keys(trip.info).map(function(key, i) {
      //   return <div key={i} className={key}>{trip.info[key]}</div>;
      // });

      return(
        React.DOM.tr({key: trip.id}, 
          React.DOM.td({onClick: this.selectTrip.bind(this, trip.id)}, trip.id), 
          React.DOM.td({onClick: this.selectTrip.bind(this, trip.id)}, trip.user), 
          React.DOM.td({onClick: this.selectTrip.bind(this, trip.id)}, moment(trip.start).format('HH:mm:ss')), 
          React.DOM.td({onClick: this.selectTrip.bind(this, trip.id)}, moment(trip.stop).format('HH:mm:ss')), 
          React.DOM.td({onClick: this.selectTrip.bind(this, trip.id)}, moment(trip.duration).utc().format('HH:mm:ss')), 
          React.DOM.td(null, 
            React.DOM.input({type: "text", defaultValue: trip.comment, onChange: this.updateTrip.bind(this, trip.id)})
          ), 
          React.DOM.td({onClick: this.deleteTrip.bind(this, trip.id)}, "✗")
        )
      )
    }.bind(this));

    return (
      React.DOM.table(null, 
        React.DOM.thead(null, 
          React.DOM.tr(null, 
            React.DOM.th(null, "id"), 
            React.DOM.th(null, "user"), 
            React.DOM.th(null, "start"), 
            React.DOM.th(null, "stop"), 
            React.DOM.th(null, "duration"), 
            React.DOM.th(null, "comment"), 
            React.DOM.th(null)
          )
        ), 
        React.DOM.tbody(null, 
          trips
        )
      )
    );
  }

});

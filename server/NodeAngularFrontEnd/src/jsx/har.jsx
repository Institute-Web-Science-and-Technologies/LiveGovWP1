/*** @jsx React.DOM */

var HarTable = React.createClass({
  render: function() {
    var rows = this.props.data.map(function(d, i) {
      return(
        <tr key={i} className={d.tag}>
          <td>{moment(d.start).format('HH:mm:ss')}</td>
          <td>{moment(d.stop).format('HH:mm:ss')}</td>
          <td>{d.tag}</td>
        </tr>
      );
    });

    return (
      <table>
        <thead><th>start</th><th>stop</th><th>tag</th></thead>
        <tbody>{rows}</tbody>
      </table>
    );
  }
});

var HarTags = React.createClass({
  render: function() {
    var tags = this.props.tags.map(function(tag, i) {
      return(
        <li key={i} className={'harTag ' + tag}>
          {tag}
        </li>
      );
    });

    return (
      <ul className='harTags'>{tags}</ul>
    );
  }
});

var HarBar = React.createClass({
  render: function() {
    var xScale = d3.time.scale().range([0, this.props.width]);
    xScale.domain(this.props.xDomain);

    var x = 0;
    var rects = this.props.data.map(function(c, i, a) {
      var width = xScale(c.stop) - xScale(c.start);
      var tag = c.tag;
      var rect = <g key={i}><rect className={tag} x={x} width={width} y='0' height='100%' /><title>{tag}</title></g>;
      x = x + width;
      return rect;
    });

    return <g>{rects}</g>;
  }
});

var HarBrush = React.createClass({
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
    return <g className='harBrush' />;
  }
});

var HarLegend = React.createClass({
  render: function() {

    return (
      <ul className='legend'>
        <li className='walking'>walking</li>
        <li className='running'>running</li>
        <li className='sitting'>sitting</li>
        <li className='standing'>standing</li>
        <li className='driving'>driving</li>
        <li className='on_table'>on table</li>
        <li className='unknown'>unknown</li>
      </ul>
    );
  }
});

var Har = React.createClass({
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
    var tags = data.sensor_tags ? data.sensor_tags : [];

    var geojson = {
      activities: this.featureCollection(gps, har),
      tags: this.featureCollection(gps, tags),
      extent: this.featureCollection(gps, this.state.extent)
    }

    var harDomain = this.calculateDomain([har], ['ts']); // NOTE: harDomain != xDomain

    var width = window.innerWidth;
    var height = window.innerHeight;

    var harTable = <HarTable className='harTable' data={har} />;
    var harMap   = <Map gps={gps} har={har}/>
    var harBar   = <HarBar   className='harBar'   xDomain={harDomain} extent={this.state.extent} width={width} data={har} />;
    var harBrush = <HarBrush className='harBrush' xDomain={harDomain} extent={this.state.extent} width={width} onBrush={this.onBrush}/>;

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
          <div key={sensor.name} className='pure-g'>
            <div className='pure-u-24-24'>
              <ChartHeader sensorName={sensor.name.replace(/sensor/, '').replace(/_/g, ' ')} />
              <ChartLegend />
              <ChartDataCount loadedData={sensor.data.length} availableData={this.props.trip.count[sensor.name]} />
              <ChartExport tripId={this.props.trip.id} sensor={sensor.name} availableData={this.props.trip.count[sensor.name]}/>
              <Chart
                data={sensor.data}
                extent={this.state.extent}
                xDomain={this.state.xDomain}
                yDomain={this.state.yDomain}
                onBrush={this.onBrush}
              />
            </div>
          </div>
        );
      }.bind(this))

    return (
      <div id='har'>
        <div id='main'>
          <svg height='50px' className='minimap'>
            <HarBar className='harBar' xDomain={this.state.xDomain} extent={this.state.extent} width={width} data={har} />
            <HarBrush className='harBrush' xDomain={this.state.xDomain} extent={this.state.extent} width={width} onBrush={this.onBrush} />
          </svg>
          <HarTags tags={harTags} />
          <Map data={this.props.trip.data} extent={this.state.extent} har='true' />
          {charts}
        </div>
        <div id='minimap'>
        </div>
      </div>
    );
  }
});

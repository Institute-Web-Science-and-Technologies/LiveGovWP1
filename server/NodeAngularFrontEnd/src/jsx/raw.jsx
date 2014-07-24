/*** @jsx React.DOM */

var Raw = React.createClass({
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

    var harTable = <HarTable className='harTable' data={har} />;
    var harMap   = <Map gps={gps} har={har}/>

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

    var map = (
      <div className='pure-g'>
        <div className='pure-u-24-24'>
          <ChartHeader sensorName='GPS' />
          <div className='cell map'>
            <Map featureCollection={featureCollection} colorized={false} />
          </div>
        </div>
      </div>
    );

    if (!featureCollection.features.length) map = '';


		return (
      <div id='raw'>
        <div id='main'>
          {map}
          {charts}
        </div>
      </div>
		);
	}
});

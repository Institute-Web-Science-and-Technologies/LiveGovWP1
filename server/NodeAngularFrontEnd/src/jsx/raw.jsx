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

  onBrush: function(extent) {
    this.setState({extent: extent}); // propagate extent to child components
    this.props.loadMoreData({extent: extent}); // tell angular to load more data
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

    /////////////////////////////////////////////////////////////////////////////
    // ASSEMBLE CHART COMPONENT                                                //
    /////////////////////////////////////////////////////////////////////////////

    var charts =
      Object.keys(data)
      .filter(function(sensor) {
        return !sensor.match(/_har|_gps|_tags/); // exclude data w/o x/y/z
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

    /////////////////////////////////////////////////////////////////////////////
    // RENDER RAW VIEW                                                         //
    /////////////////////////////////////////////////////////////////////////////

		return (
      <div id='raw'>
        <div id='main'>
          <Debug extent={this.state.extent} />
          <Map data={this.props.trip.data} extent={this.state.extent} />
          {charts}
        </div>
      </div>
		);
	}
});

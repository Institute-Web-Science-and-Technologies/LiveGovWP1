/*** @jsx React.DOM */

var TripsFilter = React.createClass({
  render: function() {
    return (
      <span>
        <input type='slider' min='0' max='100' />
      </span>
    );
  }
});

var Trips = React.createClass({

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
        <tr key={trip.id}>
          <td onClick={this.selectTrip.bind(this, trip.id)}>{trip.id}</td>
          <td onClick={this.selectTrip.bind(this, trip.id)}>{trip.user}</td>
          <td onClick={this.selectTrip.bind(this, trip.id)}>{moment(trip.start).format('YY-MM-DD HH:mm:ss')}</td>
          <td onClick={this.selectTrip.bind(this, trip.id)}>{moment(trip.stop).format('HH:mm:ss')}</td>
          <td onClick={this.selectTrip.bind(this, trip.id)}>{moment(trip.duration).utc().format('HH:mm:ss')}</td>
          <td>
            <input type='text' defaultValue={trip.comment} onChange={this.updateTrip.bind(this, trip.id)} />
          </td>
          <td onClick={this.deleteTrip.bind(this, trip.id)}>✗</td>
        </tr>
      )
    }.bind(this));

    return (
      <table>
        <thead>
          <tr>
            <th>id</th>
            <th>user</th>
            <th>start</th>
            <th>stop</th>
            <th>duration</th>
            <th>comment</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {trips}
        </tbody>
      </table>
    );
  }

});

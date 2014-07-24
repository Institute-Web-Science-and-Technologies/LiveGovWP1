/*** @jsx React.DOM */

var ExtentInfo = React.createClass({
  render: function() {
    var extent = this.props.extent.map(function(d) { return moment(d).format('HH:mm:ss'); }).join(' - ');
    return <ul className='extentInfo'><li>{extent}</li></ul>;
  }
});

var DataInfo = React.createClass({
  render: function() {
    var a = this.props.trip.count;
    var b = _.mapValues(this.props.trip.data, function(d) { return d.length; }.bind(this));
    var c = _.zip(_.keys(a),_.zip(_.values(b), _.values(a)));
    return <div />;
  }
});

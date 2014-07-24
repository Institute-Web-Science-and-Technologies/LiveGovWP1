/*** @jsx React.DOM */

var Menu = React.createClass({
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
      <ul className='pure-menu pure-menu-open pure-menu-horizontal'>
        <li>
          <a href='/'>Records</a>
        </li>
        <li className={className('raw')}>
          <a href={href('raw')}>Raw Data</a>
        </li>
        <li className={className('har')}>
          <a href={href('har')}>Activity Recognition</a>
        </li>
        <li  className={className('sld')}>
          <a href={href('sld')}>Service Line Detection</a>
        </li>
      </ul>
    );
  }
});

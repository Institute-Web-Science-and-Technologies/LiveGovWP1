/*** @jsx React.DOM */

//////////////////////////////////
// DISPLAY SOME DEBUGGING STUFF //
//////////////////////////////////

var Debug = React.createClass({

  getInitialState: function() {
    return {
      lastExtent: [],
      nextExtent: [],
    };
  },

  componentWillReceiveProps: function(nextProps) {
    console.log('componentWillReceiveProps', nextProps);
    this.setState({
      lastExtent: this.props.extent,
      nextExtent: nextProps.extent
    })

  },

  componentWillUpdate: function(nextProps, nextState) {
    console.log('componentWillUpdate', nextProps, nextState);
  },

  render: function() {
    return (
      <pre>
        last extent: {this.state.lastExtent.toString()}<br />
        cur. extent: {this.props.extent.toString()}<br />
        next extent: {this.state.nextExtent.toString()}<br />
      </pre>
    );
  }
})

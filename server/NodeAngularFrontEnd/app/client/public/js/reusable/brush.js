'use strict';

if (!d3.custom) d3.custom = {};

d3.custom.chartBrush = function module() {

  // default values. may be overwritten by exported functions.

  var margin = {top: 8, right: 8, bottom: 8, left: 8 }, // FIXME
    width = d3.select(this)[0].parentNode.offsetWidth - ((margin.left + margin.right) * 3) - 1,
    height = 64,
    xScale = d3.time.scale().range([0, width]),
    yScale = d3.scale.linear().range([height, 0]),
    xAxis = d3.svg.axis().scale(xScale).orient("bottom").ticks(Math.max(width / 75, 2)),
    yAxis = d3.svg.axis().scale(yScale).orient("left").ticks(Math.max(height / 25, 2)),
    brush = d3.svg.brush(),
    extent = [];

  var dispatch = d3.dispatch('brushed', 'brushended'); // exposed functions

  function exports(selection) {
    selection.each(function(d, i) {
      if (!d) return;

      var domain = d.domain; // d.domain = { 'x': Array[2], 'y': Array[2] }

      xScale.domain(domain.x);
      yScale.domain(domain.y);

      // create svg element if it doesn't exist
      if (d3.select(this).select('svg').empty()) {
        d3.select(this)
          .append('svg')
            .classed('brush', true)
            .attr("width", width)
            .attr("height", height);
      }

      // select svg element
      var svg = d3.select(this).select('svg');

      // remove old children of svg element
      svg.selectAll("*").remove();

      // create brush
      brush.x(xScale)
        .on("brush", brushed)
        .on("brushend", brushended);

      // programatically set brush extent if it's set
      if (extent.length) brush.extent(extent);

      // draw the actual brush rectangle
      svg.append("g")
        .attr("class", "brush")
        .call(brush)
        .selectAll("rect")
        .attr("height", height - 1); // ?

      function brushed() {
        // console.log('BRUSH EXTENT 1:', brush.extent().map(function (d) { return +d; }), extent);
        dispatch.brushed(brush.empty() ? [] : brush.extent().map(function(d) { return +d; }));
      }

      function brushended() {
        if (!d3.event.sourceEvent) return; // only transition after input
        dispatch.brushended(brush.extent().map(function(d) { return +d; }));
      }
    });
  }

  // expose extent setter/getter
  exports.extent = function(_) {
    if (!arguments.length) return extent;
    // console.log('BRUSH EXTENT 5:', _, extent);
    extent = _;
    return this; // calls chartBrush
  };

  d3.rebind(exports, dispatch, 'on');
  return exports;
};

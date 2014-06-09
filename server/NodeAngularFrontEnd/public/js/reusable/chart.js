/* jshint strict:true, devel:true, debug:true */
/* globals d3 */
'use strict'; // jshint -W097

if (!d3.custom) d3.custom = {};

d3.custom.lineChart = function () {

  // default values may be overwritten by exported functions

  var margin = {top: 2, right: 1, bottom: 21, left: 24 }, // FIXED!
    width = d3.select('chart')[0][0].offsetWidth - margin.left - margin.right,
    height = 120,
    xScale = d3.time.scale().range([0, width]),
    yScale = d3.scale.linear().range([height, 0]),
    xAxis = d3.svg.axis().scale(xScale).orient("bottom").ticks(Math.max(width / 75, 2)),
    yAxis = d3.svg.axis().scale(yScale).orient("left").ticks(Math.max(height / 25, 2)),
    brush = d3.svg.brush(),
    extent;

  var dispatch = d3.dispatch('brushended'); // exposed functions

  function exports(selection) {
    selection.each(function(d, i) {
      if (!d) return;
      if (extent) xScale.domain(extent);
      var chartName = this.classList[0];
      var data = d.data;

      var line = d3.svg.line().interpolate("linear")
        .x(function(d) {
          return xScale(d[0]); // timestamp
        })
        .y(function(d) {
          return yScale(d[1]); // sensor value
        });

      if (d3.select(this).select('svg').empty()) {
        d3.select(this)
          .append('svg')
            .classed('chart', true)
            .classed(chartName, true)
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
          .append("defs")
          .append("clipPath")
            .attr("id", "clip")
          .append("rect")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom);
      }

      // remove old charts from svg element
      d3.select(this).select('svg').selectAll("g.chart").remove();
      d3.select(this).select('svg').selectAll("text").remove();
      d3.select(this).select('svg').selectAll("circle").remove();

      // select svg element
      var svg = d3.select(this).select('svg').data(data);

      var chart = svg.append("g")
        .attr("transform", "translate(" + (margin.left + 0) + "," + (margin.top + 0) + ")")
        .attr("class", "chart");

      chart.append("text")
          .attr("class", "x label")
          .attr("text-anchor", "end")
          .attr("x", width)
          .attr("y", height)
          .text("time");

      chart.append("text")
          .attr("class", "y label")
          .attr("text-anchor", "start")
          .attr("x", 0)
          .attr("y", (10 - 3)) // font size - axis shift
          .text("sensor value");

      // x-graph
      chart.append("path")
        .attr("class", "line line0")
        .attr("clip-path", "url(#clip)")
        .attr("d", line(data.map(function(d) { return [d.ts, d.x]; })));

      svg.selectAll("line0")
        .data(data)
        .enter().append("svg:circle")
        .attr("transform", "translate(" + (margin.left + 0) + "," + (margin.top + 0) + ")")
        .attr("cx", function(d) { return xScale(d.ts); })
        .attr("cy", function(d) { return yScale(d.x); })
        .attr("r", 1.5)
        .attr("class", "circle circle0");

      // y-graph
      chart.append("path")
        .attr("class", "line line1")
        .attr("clip-path", "url(#clip)")
        .attr("d", line(data.map(function(d) { return [d.ts, d.y]; })));

      svg.selectAll("line1")
        .data(data)
        .enter().append("svg:circle")
        .attr("transform", "translate(" + (margin.left + 0) + "," + (margin.top + 0) + ")")
        .attr("cx", function(d) { return xScale(d.ts); })
        .attr("cy", function(d) { return yScale(d.y); })
        .attr("r", 1.5)
        .attr("class", "circle circle1");

      // z-graph
      chart.append("path")
        .attr("class", "line line2")
        .attr("clip-path", "url(#clip)")
        .attr("d", line(data.map(function(d) { return [d.ts, d.z]; })));

      svg.selectAll("line2")
        .data(data)
        .enter().append("svg:circle")
        .attr("transform", "translate(" + (margin.left + 0) + "," + (margin.top + 0) + ")")
        .attr("cx", function(d) { return xScale(d.ts); })
        .attr("cy", function(d) { return yScale(d.z); })
        .attr("r", 1.5)
        .attr("class", "circle circle2");

      // x-axis
      chart.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + (height + 3) + ")") // ?
        .call(xAxis);

      // y-axis
      chart.append("g")
        .attr("class", "y axis")
        .attr("transform", "translate(-3," + margin.top + ")") // ?
        .call(yAxis);

      // create brush
      brush.x(xScale)
        .on("brushend", brushended);

      var gBrush = chart.append("g")
        .attr("class", "brush")
        .call(brush)
        .selectAll("rect")
        .attr("height", height);

      function brushended() {
        dispatch.brushended(brush.empty() ? [] : brush.extent().map(function(d) { return +d; }));
        d3.selectAll('chart').select('.brush').call(brush.clear());
      }
    });
  }

  // to update the x-domain with the brush'es extent
  exports.xScale = function(_) {
    if (!arguments.length) return xScale;
    xScale = xScale.domain(_);
    return this;
  };

  // to update the y-domain when it changes
  exports.yScale = function(_) {
    if (!arguments.length) return yScale;
    yScale = yScale.domain(_);
    return this;
  };

  exports.width = function(_) {
    if (!arguments.length) return width;
    width = parseInt(_);
    return this;
  };

  d3.rebind(exports, dispatch, 'on');
  return exports;
};
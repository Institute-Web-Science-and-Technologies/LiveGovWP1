/* jshint strict:true, devel:true, debug:true */
/* globals d3 */
'use strict'; // jshint -W097

if (!d3.custom) d3.custom = {};

d3.custom.lineChart = function module() {

  // default values. may be overwritten by exported functions.

  var margin = {top: 8, right: 8, bottom: 40, left: 40 }, // FIXME
    width = d3.select(this)[0].parentNode.offsetWidth - ((margin.left + margin.right) * 3) - 1,
    height = 120,
    xScale = d3.time.scale().range([0, width]),
    yScale = d3.scale.linear().range([height, 0]),
    xAxis = d3.svg.axis().scale(xScale).orient("bottom").ticks(Math.max(width / 75, 2)),
    yAxis = d3.svg.axis().scale(yScale).orient("left").ticks(Math.max(height / 25, 2)),
    brush = d3.svg.brush(),
    extent = [];

  var dispatch = d3.dispatch('brushended'); // exposed functions

  function exports(selection) {
    selection.each(function(d, i) {

      debugger

      if (!d) return;

      var data = d.data;

      var chartName = this.classList[0];

      // FIXME don't recalculate extent
      // if (extent.length) {
      //   xScale.domain(extent);
      // } else {
      //   dispatch('domain')
      //   xScale.domain(data.extent([d.starttime, d.endtime]));
      //   }))));
      // }

      // FIXME expose
      var line = d3.svg.line().interpolate("linear")
        .x(function(d) {
          return xScale(d[0]);
        })
        .y(function(d) {
          return yScale(d[1]);
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
            .attr("width", width)
            .attr("height", height);
      }

      // remove old children of svg element
      d3.select(this).select('svg').selectAll("*").remove();

      // select svg element
      var svg = d3.select(this).select('svg').data(data);

      // create brush
      brush.x(xScale)
        .on("brushend", brushended);

      var chart = svg.append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
        .attr("class", "chart");

      // x-graph
      chart.append("path")
        .attr("class", "line line0")
        .attr("clip-path", "url(#clip)")
        .attr("d", line(data.map(function(d) { return [d.ts, d.avgx]; })));

      // y-graph
      chart.append("path")
        .attr("class", "line line1")
        .attr("clip-path", "url(#clip)")
        .attr("d", line(data.map(function(d) { return [d.ts, d.avgy]; })));

      // z-graph
      chart.append("path")
        .attr("class", "line line2")
        .attr("clip-path", "url(#clip)")
        .attr("d", line(data.map(function(d) { return [d.ts, d.avgz]; })));

      // x-axis
      chart.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + (height + 3) + ")") // ?
        .call(xAxis);

      // y-axis
      chart.append("g")
        .attr("class", "y axis")
        .attr("transform", "translate(-3,0)") // ?
        .call(yAxis);

      var gBrush = chart.append("g")
        .attr("class", "brush")
        .call(brush)
        .selectAll("rect")
        .attr("height", height);

      // if (extent.length) gBrush.call(brush.extent(extent));

      function brushended() {
        // console.log('CHART EXTENT 1:', brush.extent().map(function (d) { return +d; }), extent);
        dispatch.brushended(brush.empty() ? [] : brush.extent().map(function(d) { return +d; }));

        // this first clears the brush, and then tells it to redraw
        // https://groups.google.com/d/msg/d3-js/SN4-kJD6_2Q/SmQNwLm-5bwJ
        chart.select(".brush").call(brush.clear());

        xScale.domain(brush.extent()); // scale the x-domain to the extent (zoom in)

        svg.transition().duration(2000).select(".x.axis").call(xAxis);

        svg
          .transition()
          .duration(2000)
          .selectAll(".line0")
          .attr("d", line(data.map(function(d) {return [d.ts, d.avgx]; })));

        svg
          .transition()
          .duration(2000)
          .selectAll(".line1")
          .attr("d", line(data.map(function(d) {return [d.ts, d.avgy]; })));

        svg
          .transition()
          .duration(2000)
          .selectAll(".line2")
          .attr("d", line(data.map(function(d) {return [d.ts, d.avgz]; })));
      }

      // function transition_data() {
      //   this.svg.selectAll(".line0").attr("d", line(data.map(function (d) { return [d.ts, d.avgx]; })));
      //   this.svg.selectAll(".line1").attr("d", line(data.map(function (d) { return [d.ts, d.avgy]; })));
      //   this.svg.selectAll(".line2").attr("d", line(data.map(function (d) { return [d.ts, d.avgz]; })));
      // }

      // function reset_axis() {
      //   svg.transition().duration(500)
      //     .select(".x.axis")
      //     .call(xAxis);
      // }

      // function clear_brush() {
      //   if (!data) { console.error('clear_brush has no data'); return; }
      //   xScale.domain(d3.extent([].concat.apply([], data.map(function (d) { return [d.starttime, d.endtime]; }))));
      //   transition_data();
      //   reset_axis();
      // }
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


  exports.extent = function(_) {
    if (!arguments.length) return extent;
    // console.log('CHART EXTENT 5:', _, extent);
    extent = _;
    return this;
  };

  d3.rebind(exports, dispatch, 'on');
  return exports;
};
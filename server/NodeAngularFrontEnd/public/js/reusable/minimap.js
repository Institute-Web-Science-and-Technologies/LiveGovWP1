/* jshint strict:true, devel:true, debug:true */
/* globals d3 */
'use strict'; // jshint -W097

if (!d3.custom) d3.custom = {};

d3.custom.minimap = function () {

  console.info('initializing minimap!');

  // default values. may be overwritten by exported functions.
  var tip = d3.tip().attr('class', 'd3-tip').html(function(d) { return d; });

  var margin = {top: 0, right: 0, bottom: 0, left: 0 },
    width = d3.select('minimap')[0][0].offsetWidth - margin.left - margin.right,
    height = 50,
    xScale = d3.time.scale().range([0, width]),
    yScale = d3.scale.linear().range([height, 0]),
    brush = d3.svg.brush(),
    extent;

  var dispatch = d3.dispatch('brushed', 'brushended'); // exposed functions

  function exports(selection) {
    selection.each(function(d, i) {
      if (!d) return;

      console.info('drawing minimap!');

      var domain = d.domain; // d.domain = { 'x': Array[2], 'y': Array[2] }

      xScale.domain(domain.x);
      yScale.domain(domain.y);

      // create svg element if it doesn't exist
      if (d3.select(this).select('svg').empty()) {
        d3.select(this)
          .append('svg')
            .classed('minimap', true)
            .attr("width", width)
            .attr("height", height)
          .append("rect")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom);
      }

      // select svg element
      var svg = d3.select(this).select('svg');

      // initialize tooltip
      svg.call(tip);

      // remove old children of svg element
      svg.selectAll("*").remove();

      // create brush
      brush.x(xScale)
        .on("brush", brushed)
        .on("brushend", brushended);

      // programatically set brush extent if it's set
      if (extent) {
        brush.extent(extent);
      } else {
        brush.clear();
      }

      // iterate through each har tag and draw colored rectangles into our svg
      // element.
      d.har.forEach(function(d, i, a) {
        svg.append("rect")
          .attr("width", function(d) {
            return xScale(a[i][1]) - xScale(a[i][0]);
          })
          .attr("height", height)
          .attr("x", function() {
            return xScale(a[i][0]);
          })
          .attr("y", 0)
          .attr("class", a[i][2].replace(/ /g, '-')) // har tag
          .on('mouseover', tip.show)
          .on('mouseout', tip.hide);
      });

      // draw the actually clickable brush rectangle
      svg.append("g")
        .attr("class", "minimap")
        .call(brush)
        .selectAll("rect")
        .attr("height", height);

      function brushed() {
        dispatch.brushed(brush.empty() ? [] : brush.extent().map(function(d) { return +d; }));
      }

      function brushended() {
        // if (!d3.event.sourceEvent) return; // only transition after input
        dispatch.brushended(brush.extent().map(function(d) { return +d; }));
      }
    });
  }

  // expose extent setter/getter
  exports.extent = function(_) {
    if (!arguments.length) return extent;
    extent = _;
    return this; // calls minimap
  };

  d3.rebind(exports, dispatch, 'on');
  return exports;
};
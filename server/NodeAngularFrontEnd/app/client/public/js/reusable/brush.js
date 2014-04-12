(function () {
  'use strict';

  var t = new Date();

  if (!d3.custom) d3.custom = {};

  d3.custom.chartBrush = function module() {

    // default values. may be overwritten by exported functions.

    var margin = {top: 8, right: 8, bottom: 8, left: 8},
        width = d3.select(this)[0].parentNode.offsetWidth - ((margin.left + margin.right)*3) - 1,
        height = 64,
        xScale = d3.time.scale().range([0, width]),
        yScale = d3.scale.linear().range([height, 0]),
        xAxis = d3.svg.axis().scale(xScale).orient("bottom").ticks(Math.max(width/75, 2)),
        yAxis = d3.svg.axis().scale(yScale).orient("left").ticks(Math.max(height/25, 2)),
        brush = d3.svg.brush(),
        extent = [];

    var svg;

    var dispatch = d3.dispatch('brushed', 'brushended', 'ready');

    function exports(_selection) {
      _selection.each(function (d, i) {

        // REQUIREMENTS
        // domain = { 'x': Array[2], 'y': Array[2] }

        // THIS FUNCTION SHOULD NOT BE CALLED WITHOUT PROPER DOMAIN DATA!

        var domain = d.domain;

        // FIXME: domain must be updated after final domain is calculated!
        dispatch.ready(); // stop watching for domain

        xScale.domain(domain.x);
        yScale.domain(domain.y);

        if (!d3.select(this).select('svg')[0][0]) {
          svg = d3.select(this)
            .append('svg')
              .classed('brush', true)
              .attr("width", width)
              .attr("height", height);
        }

        // so important...
        svg.selectAll("*").remove();

        brush.x(xScale)
          .on("brush", brushed)
          .on("brushend", brushended);

        var gBrush = svg.append("g")
          .attr("class", "brush")
          .call(brush)
        .selectAll("rect")
          .attr("height", height - 1); // ?

        // if the extent is not at it's default value ([], empty array), it
        // was changed by the exported function, then programatically set the
        // brushes extent.
        if (extent.length) brush.extent(extent);

        function brushed() {

          // FIXME: MAJOR BUG
          //
          // on mousedown on the brush brush.extent() is correct, on
          // mouserelease is expanded to the right.
          //
          // [1387563184444, 1387563184444] on mousedown
          // [1387563184444, 1387563214772] on mouserelease
          //
          // why, how and where is the extent changed?

          // EXTENT 1:
          console.log('EXTENT 1:', brush.extent().map(function (d) { return +d }), extent);
          dispatch.brushed(brush.extent().map(function (d) { return +d }));
        }

        function brushended() {
          if (!d3.event.sourceEvent) return; // only transition after input
          dispatch.brushended(brush.extent().map(function(d) { return +d; }));
        }
      });
    }

    exports.width = function(_) {
      if (!arguments.length) return width;
      width = parseInt(_);
      return this;
    };

    exports.height = function(_) {
      if (!arguments.length) return height;
      height = parseInt(_);
      duration = 0;
      return this;
    };

    exports.extent = function(_) {
      if (!arguments.length) return extent;

      // _ is the incoming extent value
      // extent is the old extent value

      console.log('EXTENT 5:', _, extent);
      console.log('\n--- END OF BRUSH CYCLE -----------------------------------------------\n');
      extent = _;
      return this;
    };

    d3.rebind(exports, dispatch, 'on');
    return exports;
  };

  // document.addEventListener("DOMContentLoaded", function (event) {
  //   console.log("DOM fully loaded and parsed after " + (event.timeStamp - t) + " ms");
  // });

}());
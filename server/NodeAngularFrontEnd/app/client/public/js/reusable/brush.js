(function () {
  'use strict';

  var t = new Date();

  if (!d3.custom) d3.custom = {};

  d3.custom.chartBrush = function module() {

    var margin = {top: 8, right: 8, bottom: 8, left: 8},
        width = d3.select(this)[0].parentNode.offsetWidth - ((margin.left + margin.right)*3) - 1,
        height = 64,
        xScale = d3.time.scale(),
        yScale = d3.scale.linear(),
        xAxis = d3.svg.axis().scale(xScale).orient("bottom").ticks(Math.max(width/75, 2)),
        yAxis = d3.svg.axis().scale(yScale).orient("left").ticks(Math.max(height/25, 2)),
        brush = d3.svg.brush(),
        extent = [];

    var svg,
        gBrush;

    var dispatch = d3.dispatch('brushed', 'brushended', 'ready');

    function exports(_selection) {
      _selection.each(function (d, i) {
        if (!d.domain || d.domain.length === 0) return;
        dispatch.ready(); // stop watching for domain
        var domain = d.domain;

        xScale.range([0, width]).domain(domain.x);
        yScale.range([height, 0]).domain(domain.y);

        if (!d3.select(this).select('svg')[0][0]) {
          svg = d3.select(this)
            .append('svg')
              .classed('brush', true)
              .attr("width", width)
              .attr("height", height);
        }

        // so important...
        svg.selectAll("*").remove();

        // var brush = d3.svg.brush().x(xScale)
        //   .on("brush", brushed)
        //   .on("brushend", brushended);

        brush.x(xScale)
          .on("brush", brushed)
          .on("brushend", brushended);

        gBrush = svg.append("g")
          .attr("class", "brush")
          .call(brush)
        .selectAll("rect")
          .attr("height", height - 1 ); // ?

        if (extent && extent.length !== 0) brush.extent(extent);

        function brushed() {
          // console.log(d3.event.mode);
          if (!d3.event.sourceEvent) return; // only transition after input
          dispatch.brushed(brush.extent().map(function(d) { return +d; }));
        }

        function brushended() {
          if (!d3.event.sourceEvent) return; // only transition after input
          dispatch.brushended(brush.extent().map(function(d) { return +d; }));
        }
      });
    }

    exports.width = function(_x) {
      if (!arguments.length) return width;
      width = parseInt(_x);
      return this;
    };

    exports.height = function(_x) {
      if (!arguments.length) return height;
      height = parseInt(_x);
      duration = 0;
      return this;
    };

    exports.extent = function(_x) {
      if (!arguments.length) return extent;
      extent = _x;
      return this;
    };

    d3.rebind(exports, dispatch, 'on');
    return exports;
  };

  // document.addEventListener("DOMContentLoaded", function (event) {
  //   console.log("DOM fully loaded and parsed after " + (event.timeStamp - t) + " ms");
  // });

}());

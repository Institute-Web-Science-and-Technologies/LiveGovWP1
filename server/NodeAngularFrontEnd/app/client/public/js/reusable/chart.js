(function () {
  'use strict';

  var t = new Date();

  if (!d3.custom) d3.custom = {};

  d3.custom.lineChart = function module() {

    // default values. may be overwritten by exported functions.

    var margin = {top: 30, right: 30, bottom: 30, left: 30},
        width = d3.select(this)[0].parentNode.offsetWidth - ((margin.left + margin.right)*3) - 1,
        height = 120,
        xScale = d3.time.scale().range([0, width]),
        yScale = d3.scale.linear().range([height, 0]),
        xAxis = d3.svg.axis().scale(xScale).orient("bottom").ticks(Math.max(width/75, 2)),
        yAxis = d3.svg.axis().scale(yScale).orient("left").ticks(Math.max(height/25, 2)),
        brush = d3.svg.brush(),
        extent = [];

    var svg;

    var dispatch = d3.dispatch('brushended', 'ready');

    function exports(selection) {
      selection.each(function (d, i) {
        if (!d) return;

        // REQUIREMENTS
        // d.data = Array[n]

        var data = d.data;

        // var xAxis = d3.svg.axis().scale(xScale).orient("bottom").ticks(Math.max(width/75, 2)),
        //     yAxis = d3.svg.axis().scale(yScale).orient("left").ticks(Math.max(height/25, 2));

        var line = d3.svg.line().interpolate("linear")
          .x(function(d) { return xScale(d[0]); })
          .y(function(d) { return yScale(d[1]); });

        if (!d3.select(this).select('svg')[0][0]) {
          svg = d3.select(this)
                .append('svg')
                  .classed('chart', true)
                  .attr("width", width + margin.left + margin.right)
                  .attr("height", height + margin.top + margin.bottom)
                .append("defs")
                .append("clipPath")
                  .attr("id", "clip")
                .append("rect")
                  .attr("width", width)
                  .attr("height", height);
                  // .attr('width', '100%');
        } else {
          d3.select(this).selectAll('svg').selectAll("*").remove();
        }

        svg = d3.select(this).selectAll('svg').data(data);

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
          .attr("transform", "translate(0," + (height + 3) + ")")
          .call(xAxis);

        // y-axis
        chart.append("g")
          .attr("class", "y axis")
          .attr("transform", "translate(-3,0)")
          .call(yAxis);

        if (extent.length) xScale.domain(extent);
        
        brush.x(xScale)
          .on("brushend", brushended);


        var gBrush = chart.append("g")
          .attr("class", "brush")
          .call(brush)
        .selectAll("rect")
          .attr("height", height);


        function brushended() {
          if (brush.empty()) return;

          // scale x domain to brush extent (zoom in)
          xScale.domain(brush.extent());

          console.log('CHART EXTENT 1:', brush.extent().map(function (d) { return +d; }), extent);
          // dispatch.brushed(brush.empty() ? [] : brush.extent().map(function (d) { return +d; }));

          // redraw the graph lines
          svg.selectAll(".line0").attr("d", line(data.map(function (d) { return [d.ts, d.avgx]; })));
          svg.selectAll(".line1").attr("d", line(data.map(function (d) { return [d.ts, d.avgy]; })));
          svg.selectAll(".line2").attr("d", line(data.map(function (d) { return [d.ts, d.avgz]; })));

          // rescale x axis
          // svg.select(".x.axis").call(xAxis);

          brush.clear();
          reset_axis();
          // clear the brush and call a new one
          chart.select("brush").call(brush.clear());
          // chart.select("brush").call(brush);
        }

        function reset_axis() {
          svg.transition().duration(500)
            .select(".x.axis")
            .call(xAxis);
        }

        function clear_brush() {
          if (data) {
            x.domain(d3.extent([].concat.apply([], data.map(function (d) { return [d.starttime, d.endtime]; }))));
            transition_data(data);
            reset_axis();
          }
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

    // to update the x-domain with the brush'es extent
    exports.xScale = function(_) {
      if (!arguments.length || !_) return xScale;
      xScale = xScale.domain(_);
      return this; // redraws the chart!
    };

    // to update the y-domain when it changes
    // FIXME: release watch after final y-domain has been calculated
    exports.yScale = function(_) {
      if (!arguments.length) return yScale;
      yScale = yScale.range([height, 0]).domain(_);
      // dispatch.ready(); // stop watching for y domain
      return this;
    };

    exports.extent = function(_) {
      if (!arguments.length) return extent;

      // _ is the incoming extent value
      // extent is the old extent value

      console.log('CHART EXTENT 5:', _, extent);
      // console.log('\n--- END OF BRUSH CYCLE -----------------------------------------------\n');
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

(function () {
  'use strict';

  var t = new Date();

  if (!d3.custom) d3.custom = {};

  d3.custom.lineChart = function module() {

    var margin = {top: 20, right: 20, bottom: 40, left: 40},
        width = d3.select(this)[0].parentNode.offsetWidth - ((margin.left + margin.right)*3) - 1,
        height = 120,
        x = d3.time.scale(),
        y = d3.scale.linear(),
        brush = d3.svg.brush();

    var xDomain,
        yDomain;

    var svg;

    var dispatch = d3.dispatch('brushended', 'ready');

    console.log(x);

    function exports(_selection) {
      _selection.each(function (d, i) { // what is i?
        if (!d) return;
        // if (!d.data || d.data.length === 0) return;
        // if (!d.domain || !d.domain.y || d.domain.y.length === 0) return;

        // dispatch.ready(); // stop watching for domain

        console.log(d);

        var data = d.data;
        var domain = d.domain;

        console.log(domain.x, domain.y);
        console.log(x);
        console.log(x.range([0,width]));



        x.range([0, width]).domain(domain.x);
        y.range([height, 0]).domain(domain.y);

        var xAxis = d3.svg.axis().scale(x).orient("bottom").ticks(Math.max(width/75, 2)),
            yAxis = d3.svg.axis().scale(y).orient("left").ticks(Math.max(height/25, 2));

        var line = d3.svg.line().interpolate("linear")
          .x(function(d) { return x(d[0]); })
          .y(function(d) { return y(d[1]); });

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
                  .attr("height", height)
                  .style('width', '100%');
        }

        d3.select(this).selectAll('svg').selectAll("*").remove();

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

          brush.x(x)
          .on("brushend", brushended);

        // brush
        chart.append("g")
          .attr("class", "brush")
          .call(brush)
          .selectAll("rect")
          .attr("height", height);

        function brushended() {
          if (brush.extent()[0] == brush.extent()[1]) return;
          // if (brush.empty()) return;

          dispatch.brushended(brush.extent());
          // dispatch.brushended(brush.extent().map(function(d) { return +d; })); // update brush extent in scope

          x.domain(brush.extent());

          svg.selectAll(".line0").attr("d", line(data.map(function (d) { return [d.ts, d.avgx]; })));
          svg.selectAll(".line1").attr("d", line(data.map(function (d) { return [d.ts, d.avgy]; })));
          svg.selectAll(".line2").attr("d", line(data.map(function (d) { return [d.ts, d.avgz]; })));

          svg.select(".x.axis") .call(xAxis);

          // brush.clear();

          chart.select("brush").call(brush.clear());
          chart.select("brush").call(brush);
        }

        function reset_axis() {
          svg //.transition().duration(500)
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

    // function X(d) {
    //   return xScale(d)
    // }

    // exports.x = function(_x) {
    //   if (!arguments.length) return x;
    //   x = _x;
    //   return this;
    // };

    // exports.y = function(_x) {
    //   if (!arguments.length) return y;
    //   y = _x;
    //   return this;
    // };

    exports.xDomain = function(_x) {
      if (!arguments.length) return xDomain;
      xDomain = _x;
      return this;
    };

    exports.yDomain = function(_x) {
      if (!arguments.length) return yDomain;
      yDomain = _x;
      return this;
    };

    d3.rebind(exports, dispatch, 'on');
    return exports;
  };

  // document.addEventListener("DOMContentLoaded", function (event) {
  //   console.log("DOM fully loaded and parsed after " + (event.timeStamp - t) + " ms");
  // });

}());

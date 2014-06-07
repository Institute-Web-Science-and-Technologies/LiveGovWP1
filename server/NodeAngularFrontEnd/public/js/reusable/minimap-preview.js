/* jshint strict:true, devel:true, debug:true */
/* globals d3 */
'use strict'; // jshint -W097

if (!d3.custom) d3.custom = {};

d3.custom.minimapPreview = function () {

  // default values. may be overwritten by exported functions.

  // debugger

  var margin = {top: 0, right: 0, bottom: 0, left: 0 },
    width = d3.select('td.minimap-preview')[0][0].offsetWidth - margin.left - margin.right,
    height = d3.select('td.minimap-preview')[0][0].offsetHeight - margin.top - margin.bottom,
    xScale = d3.time.scale().range([0, width]),
    yScale = d3.scale.linear().range([height, 0]);

  function exports(selection) {
    selection.each(function(d, i) {
      if (!d) return;
      if (!d.har.length) return;

      console.log('not returned!');

      var domain = d.domain; // d.domain = { 'x': Array[2], 'y': Array[2] }

      xScale.domain(domain.x);
      yScale.domain(domain.y);

      // create svg element if it doesn't exist
      if (d3.select(this).select('svg').empty()) {
        d3.select(this)
          .append('svg')
            .classed('minimapPreview', true)
            .attr("width", width)
            .attr("height", height)
          .append("rect")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom);
      }

      // select svg element
      var svg = d3.select(this).select('svg');

      // remove old children of svg element
      svg.selectAll("*").remove();

      // debugger

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
          .attr("class", a[i][2].replace(/ /g, '-')); // har tag
      });
    });
  }

  d3.rebind(exports, 'on');
  return exports;
};

/* jshint -W097 */
'use strict';

/* global app:true, console:true, confirm:true, d3:true */

app.directive("chart", function(debounce) { // $timeout here

	return {
		restrict: "EA",
		scope: {
			data: "=",
			selection: "=",
			domain: "=",
			sensor: "="
		},
		link: function(scope, element) {

			var chart;

			// NOTE
			// 1. Selection via brush directive to $scope.selection.
			// 2. Raw controller watches $scope.selection and loads more data to $scope.data.
			// 3. Chart directive watches $scope.data and redraws graph.

			// TODO
			// A. Focus brush must be retained during these actions.
			// B. Data must be sorted correctly by ts to get a proper graph.
			// C. The brush domain must be calculated by min_ts and max_ts of all
			// used sensors, as they are not necessarily the same for one trip.

			// XXX
			// X. We need a redraw function.
			// Y. I need more sleep.
			//
			// http://mbostock.github.io/d3/tutorial/bar-2.html
			// http://pothibo.com/2013/09/d3-js-how-to-handle-dynamic-json-data/
			// http://www.d3noob.org/2013/02/update-d3js-data-dynamically-button.html

			// https://stackoverflow.com/questions/15731409/how-to-call-a-brushs-on-function
			// https://stackoverflow.com/questions/18204352/multiple-force-layout-graphs-with-d3-in-seperate-svg-divs

			// .on("brushstart") -> repaint brush
			// .on("brush") -> set the extent
			// .on("brushend") ->

			// VALUES FROM SCOPE (SEE CONTROLLER)
			var data = scope.data;
			var e = element[0].children[1]; // here we draw

			// STATIC VALUES (WON'T CHANGE)
			var containerWidth = parseInt(d3.select(e).style("width"), 10);
			
			var margin = {top: 10, right: 30, bottom: 30, left: 30 },
					width = containerWidth - margin.left - margin.right,
					height = 300 - margin.top - margin.bottom;

			var x = d3.time.scale().range([0, width]),
					y = d3.scale.linear().range([height, 0]);

			// DRAWING FUNCTIONS (WILL BE CALLED)
			// draws a line to the graph
			var line = d3.svg.line().interpolate("linear")
					.x(function(d) { return x(d[0]); })
					.y(function(d) { return y(d[1]); });

			// draws x and y axis
			var xAxis = d3.svg.axis().scale(x).orient("bottom"),
					yAxis = d3.svg.axis().scale(y).orient("left");

			// SETUP THE SVG ELEMENT
			var svg = d3.select(e)
					.append("svg")
						.attr("width", width + margin.left + margin.right)
						.attr("height", height + margin.top + margin.bottom)
					.append("defs")
					.append("clipPath")
						.attr("id", "clip")
					.append("rect")
						.attr("width", width)
						.attr("height", height);

			var loadMoreData = function(selection) {

				// NOTE Changing the 'selection' array on the shared data object in
				// scope should trigger the watcher in the raw controller and load
				// more sensor data from the sensor service. The watcher here should
				// then recognize the changed data object in our shared scope and
				// redraw the graph. Sounds easy. Apparently it is not.

				// NOTE This shouldn't even happen here, but in the brush directive.
				// You would do a selection on the brush, the brush directive would
				// change the selection in scope, raw controller sees it, loads more
				// data and chart directive sees that and redraws the graph. Either
				// way, pretty hard.

				scope.selection = brush.extent();
			};

			var drawGraph = function(data) {
				svg.selectAll("*").remove();

				svg = d3.select(e).selectAll("svg").data(data);

				/* jshint -W007 */
				data.forEach(function(d) {
					d.ts         = new Date((+d.starttime + +d.endtime) / 2);
					d.starttime  = +d.starttime;
					d.endtime    = +d.endtime;
				});

				chart = svg.append("g")
						.attr("transform", "translate(" + margin.left + "," + margin.top + ")")
						.attr("class", "chart");

				var brush = d3.svg.brush().x(x).on("brush", brush);

				// setup axis domain
				x.domain(d3.extent([].concat.apply([], data.map(function(d) {return [d.starttime, d.endtime]; }))));
				y.domain(d3.extent([].concat.apply([], data.map(function(d) {return [d.minx, d.miny, d.minz, d.maxx, d.maxy, d.maxz]; }))));

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
					.attr("transform", "translate(0," + height + ")")
					.call(xAxis);

				// y-axis
				chart.append("g")
					.attr("class", "y axis")
					.call(yAxis);

				// brush
				chart.append("g")
					.attr("class", "x brush")
					.call(brush)
					.selectAll("rect")
					.attr("y", -6)
					.attr("height", height + 7);
			};

			function brush() {
				x.domain(brush.empty() ? x.domain() : brush.extent());
				scope.$apply(function() { scope.selection = brush.extent(); });
				chart.select(".line0").attr("d", line(data.map(function(d) { return [d.ts, d.avgx]; })));
				chart.select(".line1").attr("d", line(data.map(function(d) { return [d.ts, d.avgy]; })));
				chart.select(".line2").attr("d", line(data.map(function(d) { return [d.ts, d.avgz]; })));
				chart.select(".x.axis").call(xAxis);
			}

			// WATCH FOR NEW DATA
			scope.$watchCollection("data", debounce(function(data, oldData) {
				if (!data || !data.$resolved) { return; }

				drawGraph(data);

			}, 1000));

			// WATCH FOR BRUSHES
			scope.$watchCollection("selection", debounce(function(sel, oldsel) {
				if (!sel) { return; }

				svg.select(".brush").call(brush.extent(scope.selection));

			}, 1000));

		} // end link
	}; // end return
}); // end directive
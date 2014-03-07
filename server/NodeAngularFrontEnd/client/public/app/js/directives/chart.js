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
			var brush;

			// TODO
			// A. Focus brush must be retained during these actions.
			// B. Data must be sorted correctly by ts to get a proper graph.
			// C. The brush domain must be calculated by min_ts and max_ts of all
			// used sensors, as they are not necessarily the same for one trip.

			// http://mbostock.github.io/d3/tutorial/bar-2.html
			// http://pothibo.com/2013/09/d3-js-how-to-handle-dynamic-json-data/
			// http://www.d3noob.org/2013/02/update-d3js-data-dynamically-button.html

			// https://stackoverflow.com/questions/15731409/how-to-call-a-brushs-on-function
			// https://stackoverflow.com/questions/18204352/multiple-force-layout-graphs-with-d3-in-seperate-svg-divs

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

			// NOTE Changing the 'selection' array on the shared data object in
			// scope triggers the watcher in the raw controller and loads more
			// sensor data from the sensor factory. The watcher here then recognizes
			// the changed data object and redraws the graph.

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

				// setup axis domain

				if (scope.selection) {
					x.domain(scope.selection);
				} else {
					x.domain(d3.extent([].concat.apply([], data.map(function(d) {return [d.starttime, d.endtime]; }))));
				}

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

				// BRUSH

				brush = d3.svg.brush()
					.x(x)
					.on("brushend", brushend);

				// brush
				chart.append("g")
					.attr("class", "brush")
					.call(brush)
					.selectAll("rect")
					.attr("height", height);

			};

			var clear_button;

			function brushend() {
				scope.$apply(function() { scope.selection = brush.extent(); }); // put selection in scope

				var get_button = d3.select(".clear-button");
				if(get_button.empty() === true) {
					clear_button = chart.append('text')
						.attr("y", 460)
						.attr("x", 825)
						.attr("class", "clear-button")
						.text("Clear Brush");
				}

				x.domain(brush.extent());
				transition_data();
				reset_axis();
				brush.clear();

				chart.select("brush").call(brush.clear());
				chart.select("brush").call(brush);

				clear_button.on('click', function(){
					x.domain([0, 50]);
					transition_data();
					reset_axis();
					clear_button.remove();
				});
			}

			function transition_data() {
				svg.selectAll(".line0").attr("d", line(data.map(function(d) { return [d.ts, d.avgx]; })));
				svg.selectAll(".line1").attr("d", line(data.map(function(d) { return [d.ts, d.avgy]; })));
				svg.selectAll(".line2").attr("d", line(data.map(function(d) { return [d.ts, d.avgz]; })));
			}

			function reset_axis() {
				svg //.transition().duration(500)
					.select(".x.axis")
					.call(xAxis);
			}

			// WATCH FOR NEW DATA
			scope.$watchCollection("data", function(data, oldData) {
				if (!data) { console.log("Error: drawGraph() has no data!"); return; }
				console.log("XXX " + data.length);
				drawGraph(data);
			});

		} // end link
	}; // end return
}); // end directive
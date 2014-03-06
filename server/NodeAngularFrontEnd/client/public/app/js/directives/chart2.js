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

			var data = scope.data;

			var e = element[0].children[1];

			var containerWidth = parseInt(d3.select(e).style("width"), 10);
			
			var margin = {top: 10, right: 30, bottom: 100, left: 30 },
					margin2 = {top: 230, right: 30, bottom: 20, left: 30 },
					width = containerWidth - margin.left - margin.right,
					height = 300 - margin.top - margin.bottom,
					height2 = 300 - margin2.top - margin2.bottom;

			var x = d3.time.scale().range([0, width]),
					x2 = d3.time.scale().range([0, width]),
					y = d3.scale.linear().range([height, 0]),
					y2 = d3.scale.linear().range([height2, 0]);

			// NOTE unused?
			// var parseDate = d3.time.format("%H %M").parse;

			var xAxis = d3.svg.axis().scale(x).orient("bottom"),
					xAxis2 = d3.svg.axis().scale(x2).orient("bottom"),
					yAxis = d3.svg.axis().scale(y).orient("left");

			var line = d3.svg.line()
					.interpolate("linear")
					.x(function(d) { return x(d[0]); })
					.y(function(d) { return y(d[1]); });

			var line2 = d3.svg.line()
					.interpolate("linear")
					.x(function(d) { return x2(d[0]); })
					.y(function(d) { return y2(d[1]); });

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

			// DRAW THE GRAPH ON DATA CHANGES

			scope.$watchCollection("data", debounce(function(data, oldData) {

				// exit if there's no new data or it's not ready, yet
				if (!data || !data.$resolved) {
					return;
				}
				
				// clear the elements inside of the directive
				svg.selectAll("*").remove();

				// select the svg element and append the data
				svg = d3.select(e).selectAll("svg").data(data);

				data.forEach(function(d) {
					d.ts         = new Date((+d.starttime + +d.endtime) / 2);
					d.starttime  = +d.starttime;
					d.endtime    = +d.endtime;
				});

				// the big one
				var focus = svg.append("g")
						.attr("transform", "translate(" + margin.left + "," + margin.top + ")")
						.attr("class", "chart");

				// the small one
				var context = svg.append("g")
						.attr("transform", "translate(" + margin2.left + "," + margin2.top + ")");

				// brush on the small one (needs data)
				var brush = d3.svg.brush()
						.x(x2)
						.on("brush", brushed);

				// setup axis
				x.domain(d3.extent([].concat.apply([], data.map(function(d) {return [d.starttime, d.endtime]; }))));
				y.domain(d3.extent([].concat.apply([], data.map(function(d) {return [d.minx, d.miny, d.minz, d.maxx, d.maxy, d.maxz]; }))));
				x2.domain(x.domain());
				y2.domain(y.domain());

				scope.domain = x.domain();

				// x-graph
				focus.append("path")
					.attr("class", "line line0")
					.attr("clip-path", "url(#clip)")
					.attr("d", line(data.map(function(d) { return [d.ts, d.avgx]; })));

				// y-graph
				focus.append("path")
					.attr("class", "line line1")
					.attr("clip-path", "url(#clip)")
					.attr("d", line(data.map(function(d) { return [d.ts, d.avgy]; })));

				// z-graph
				focus.append("path")
					.attr("class", "line line2")
					.attr("clip-path", "url(#clip)")
					.attr("d", line(data.map(function(d) { return [d.ts, d.avgz]; })));

				// x-axis
				focus.append("g")
					.attr("class", "x axis")
					.attr("transform", "translate(0," + height + ")")
					.call(xAxis);

				// y-axis
				focus.append("g")
					.attr("class", "y axis")
					.call(yAxis);

				// context x-graph line0 avgx
				context.append("path")
					.attr("class", "line line0")
					.attr("d", line2(data.map(function(d) { return [d.ts, d.avgx]; })));

				// context x-graph line1 avgy
				context.append("path")
					.attr("class", "line line1")
					.attr("d", line2(data.map(function(d) { return [d.ts, d.avgy]; })));

				// context x-graph line2 avgz
				context.append("path")
					.attr("class", "line line2")
					.attr("d", line2(data.map(function(d) { return [d.ts, d.avgz]; })));

				// context x-axis
				context.append("g")
					.attr("class", "x axis")
					.attr("transform", "translate(0," + height2 + ")")
					.call(xAxis2);

				// context brush
				context.append("g")
					.attr("class", "x brush")
					.call(brush)
					.selectAll("rect")
					.attr("y", -6)
					.attr("height", height2 + 7);
					// .on('click', function(d, i) { console.log("bla"); });

				// recall saved brush state
				if (scope.data.selection) {
					// https://groups.google.com/d/msg/d3-js/SN4-kJD6_2Q/SmQNwLm-5bwJ
					// svg.select(".brush").call(brush.clear());
					svg.select(".brush").call(brush.extent(scope.selection));
				}

				function brushed() {
					scope.$apply(function() { scope.selection = brush.extent(); });
					x.domain(brush.empty() ? x2.domain() : brush.extent());
					focus.selectAll(".line0").attr("d", line(data.map(function(d) { return [d.ts, d.avgx]; })));
					focus.selectAll(".line1").attr("d", line(data.map(function(d) { return [d.ts, d.avgy]; })));
					focus.selectAll(".line2").attr("d", line(data.map(function(d) { return [d.ts, d.avgz]; })));
					focus.selectAll(".x.axis").call(xAxis);
				}

			}, 1000)); // end watch

			// BRUSH ON SELECTION CHANGE

			// scope.$watchCollection("selection", $debounce(function(sel, oldSel) {
			// 	if (sel !== oldSel && brush !== undefined) {
			// 		// brush = d3.svg.brush().x(x2).on("brush", brushed);
			// 		// svg.select(".brush").call(brush.extent(scope.selection));
			// 		console.log(scope.sensor.toUpperCase() + ":   " + sel);
			// 	}
			// }, 1000));


		} // end link
	}; // end return
}); // end directive
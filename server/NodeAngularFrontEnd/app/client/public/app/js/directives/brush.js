/* jshint -W097 */
'use strict';

/* global app:true, console:true, confirm:true, d3:true */

app.directive("brush", function(debounce) {

	return {
		restrict: "E",
		scope: {selection: "="},
		link: function(scope, element) {

			var brush;
			var contextXScale;

			scope.$watchCollection('domain', function(newVal, oldVal) {
				if (scope.domain && newVal !== oldVal) {

			var e = element[0].children[1];

			var containerWidth = parseInt(d3.select(e).style("width"), 10);

			var margin = {top: 10, right: 30, bottom: 10, left: 30 },
					width = containerWidth - margin.left - margin.right,
					height = 100 - margin.top - margin.bottom;

			var svg = d3.select(e)
								.append("svg")
								.attr("width", width + margin.left + margin.right)
								.attr("height", (height + margin.top + margin.bottom));

			contextXScale = d3.time.scale()
													.range([0, width])
													.domain(scope.domain);

			var contextAxis = d3.svg.axis()
												.scale(contextXScale)
												// .tickSize(height)
												// .tickPadding(-10)
												.orient("bottom");

			var contextArea = d3.svg.area()
												.interpolate("monotone")
												.x(function(d) { return contextXScale(d.date); })
												.y0(height)
												.y1(0);

			brush = d3.svg.brush()
									.x(contextXScale)
									.on("brush", brushSelect);

			var context = svg.append("g")
					.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

			context.append("g")
						.attr("class", "x axis top")
						.attr("transform", "translate(0,0)")
						.call(contextAxis);

			context.append("g")
						.attr("class", "x brush")
						.call(brush)
						.selectAll("rect")
						.attr("y", 0)
						.attr("x", 0)
						.attr("height", height);

			context.append("text")
						.attr("class","instructions")
						.attr("transform", "translate(0," + (height + 20) + ")")
						.text('Click and drag above to zoom / pan the data');

				}
			});

			function brushSelect() {
				scope.$apply(function() { scope.selection = brush.extent(); });
			}

			scope.$watchCollection("selection", debounce(function(sel, oldSel) {
				if (sel !== oldSel) {
					console.log("BRUSH: " + sel);
					contextXScale.domain(sel);
				}
			}, 1000));

		} // end link
	}; // end return
}); // end directive

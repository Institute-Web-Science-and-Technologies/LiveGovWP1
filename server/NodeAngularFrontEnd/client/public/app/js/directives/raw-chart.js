'use strict';

app.directive('rawChart', function() {
    return {
        restrict: 'E',
        scope: {
            data: '='
        },
        link: function(scope, element, attrs) {

            // SETUP THE GRAPH (D3 DATA GOES HERE)

            // element[0].children[1] is div#chart
            var containerWidth = parseInt(d3.select(element[0].children[1]).style('width'), 10);

            var margin = {
                top: 10,
                right: 30,
                bottom: 100,
                left: 30
            },
                margin2 = {
                    top: 230,
                    right: 30,
                    bottom: 20,
                    left: 30
                },
                width = containerWidth - margin.left - margin.right,
                height = 300 - margin.top - margin.bottom,
                height2 = 300 - margin2.top - margin2.bottom;

            var x = d3.time.scale().range([0, width]),
                x2 = d3.time.scale().range([0, width]),
                y = d3.scale.linear().range([height, 0]),
                y2 = d3.scale.linear().range([height2, 0]);

            var parseDate = d3.time.format("%b %Y").parse;

            var xAxis = d3.svg.axis().scale(x).orient("bottom"),
                xAxis2 = d3.svg.axis().scale(x2).orient("bottom"),
                yAxis = d3.svg.axis().scale(y).orient("left");

            var line = d3.svg.line()
                .interpolate("monotone")
                .x(function(d) {
                    return x(d[0]);
                })
                .y(function(d) {
                    return y(d[1]);
                });

            var line2 = d3.svg.line()
                .interpolate("monotone")
                .x(function(d) {
                    return x2(d[0]);
                })
                .y(function(d) {
                    return y2(d[1]);
                });

            var svg = d3.select(element[0].children[1])
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
            scope.$watchCollection('data', function(data, oldData) {

                // exit if there's no data
                if (data.length == 0) {
                    return;
                }

                // clear the elements inside of the directive
                // svg.selectAll('*').remove();

                // select the svg element and append the data
                svg = d3.select(element[0].children[1]).selectAll("svg").data(data);

                // the big one
                var focus = svg.append("g")
                    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

                // the small one
                var context = svg.append("g")
                    .attr("transform", "translate(" + margin2.left + "," + margin2.top + ")");

                // needs data
                var brush = d3.svg.brush().x(x2).on("brush", brushed);

                // prepare the data (move to controller!)
                data.forEach(function(d) {
                    d.ts = new Date((parseInt(d.starttime) + parseInt(d.endtime)) / 2);
                    d.starttime = new Date(parseInt(d.starttime));
                    d.endtime = new Date(parseInt(d.endtime));
                });

                // setup axis
                x.domain(d3.extent([].concat.apply([], data.map(function(d) {
                    return [d.starttime, d.endtime]
                }))));
                // y.domain(d3.extent([].concat.apply([], data.map(function(d) { return [d.avgx, d.avgy, d.avgz] }))));
                y.domain(d3.extent([].concat.apply([], data.map(function(d) {
                    return [d.minx, d.miny, d.minz, d.maxx, d.maxy, d.maxz]
                }))));
                x2.domain(x.domain());
                y2.domain(y.domain());

                // ACTUALLY DRAW THE GRAPH

                // x-graph
                focus.append("path")
                    .attr("class", "line line0")
                    .attr("clip-path", "url(#clip)")
                    .attr("d", line(data.map(function(d) {
                        return [d.ts, d.avgx];
                    })));

                // y-graph
                focus.append("path")
                    .attr("class", "line line1")
                    .attr("clip-path", "url(#clip)")
                    .attr("d", line(data.map(function(d) {
                        return [d.ts, d.avgy];
                    })));

                // z-graph
                focus.append("path")
                    .attr("class", "line line2")
                    .attr("clip-path", "url(#clip)")
                    .attr("d", line(data.map(function(d) {
                        return [d.ts, d.avgz];
                    })));

                // x-axis
                focus.append("g")
                    .attr("class", "x axis")
                    .attr("transform", "translate(0," + height + ")")
                    .call(xAxis);

                // y-axis
                focus.append("g")
                    .attr("class", "y axis")
                    .call(yAxis);

                // context x-graph
                context.append("path")
                    .attr("class", "line line0")
                    .attr("d", line2(data.map(function(d) {
                        return [d.ts, d.avgx];
                    })));

                // context x-graph
                context.append("path")
                    .attr("class", "line line1")
                    .attr("d", line2(data.map(function(d) {
                        return [d.ts, d.avgy];
                    })));

                // context x-graph
                context.append("path")
                    .attr("class", "line line2")
                    .attr("d", line2(data.map(function(d) {
                        return [d.ts, d.avgz];
                    })));

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

                // fixme
                function brushed() {
                    x.domain(brush.empty() ? x2.domain() : brush.extent());
                    focus.select(".line0").attr("d", line(data.map(function(d) {
                        return [d.ts, d.avgx];
                    })));
                    focus.select(".line1").attr("d", line(data.map(function(d) {
                        return [d.ts, d.avgy];
                    })));
                    focus.select(".line2").attr("d", line(data.map(function(d) {
                        return [d.ts, d.avgz];
                    })));
                    focus.select(".x.axis").call(xAxis);
                }

            }); // end watch
        } // end link
    } // end return
}); // end directive
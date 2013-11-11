
var map;
var routes = L.layerGroup();


function updateMap() {
	// eat the event to prevent a unwanted click event
	return false;
}


function initilize(){
	$('input:checkbox').removeAttr('checked');
	// POINT(24.9396 60.17321)
	map = L.map('map').setView([60.16946,24.95667], 12);

	L.tileLayer('http://{s}.tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/997/256/{z}/{x}/{y}.png', {
		maxZoom: 18,
		attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>'
	}).addTo(map);


  map.on('dragend', this.updateMap, this);
  $("#uncheckall").click(function(){$('input:checkbox').removeAttr('checked');});
  $("input:checkbox").click( function(){
	  var postData = "";
	    $( "tr:has(input:checked) td:nth-child(2)" ).each(function( index ) {
	      var latlon = new L.LatLng( $( this ).text(), $( this ).next().text());
		  console.log( latlon.toString());
		  new L.marker(latlon).addTo(map);
		  postData += latlon.lat + ",";
		  postData += latlon.lng + ",";
		  postData += $(this).next().next().text() + ",";
		  postData += $(this).next().next().next().text() + "\n";
		  });
	    var start = new Date().getTime();
	    $.ajax({type:"POST",
	    	url:"/backend/ServiceLineDetection2",
	    	data: postData,
	    	success: function (data) {
//	    			routes.clearLayers();
//	    			var colors = ["#FF0000", "#00FF00"];
//	    			for(var r in data.routes){
//	    				routes.addLayer(L.geoJson(data.routes[r].geojson, {
//	    				    style: {
//		    				    "color": colors[parseInt(data.routes[r].routedir)-1],
//		    				    "weight": 5,
//		    				    "opacity": 0.65
//		    				}
//	    				}));
//			    			var marker = L.marker([data.routes[r].geojson.coordinates[0][1],data.routes[r].geojson.coordinates[0][0]]);
//			    			marker.bindPopup("<b>routecode: "+data.routes[r].routecode+"</b><br>direction: " + data.routes[r].routedir);
//			    			routes.addLayer(marker);
//			    			routes.addTo(map);
//	    			}
	    		var end = new Date().getTime();
	    		var table = $("#response");
	    		table.html("<tr><th>route_id</th><th>shape_id</th><th>trip_id</th><th>score</th>");
	    		  $.each(data.routes, function(index, item){
	    		         var table_row = $('<tr>');
	    		         table_row.append($('<td>').html(item.route_id));
	    		         table_row.append($('<td>').html(item.shape_id));
	    		         table_row.append($('<td>').html(item.trip_id));
	    		         table_row.append($('<td>').html(item.score));
	    		         table.append(table_row);
	    		    });
	    		  $("#timing").html(end-start + " ms");
	    		},
	    		dataType:"json"
	    });
	});
}

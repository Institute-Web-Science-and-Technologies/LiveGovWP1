
var markers = [];
var map;



function updateMap() {
	// eat the event to prevent a unwanted click event
	return false;
}


function initilize(){
	map = L.map('map').setView([60.16946,24.95667], 12);

	L.tileLayer('http://{s}.tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/997/256/{z}/{x}/{y}.png', {
		maxZoom: 18,
		attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>'
	}).addTo(map);


  map.on('dragend', this.updateMap, this);
  map.on('click', function(e){
	    new L.marker(e.latlng).addTo(map);
	    markers.push(e.latlng.toString());
	    $.get("/backend/InspectionServlet", { 'points[]': markers} ).done(
	    		function (data) {
	    			var colors = ["#000", "#FFEDA0"];
	    			for(var r in data.routes){
	    				L.geoJson(data.routes[r].geojson, {
	    				    style: {
		    				    "color": colors[parseInt(r)],
		    				    "weight": 5,
		    				    "opacity": 0.65
		    				}
	    				}).addTo(map);
			    			var myLayer = L.geoJson().addTo(map);
			    			myLayer.addData(data.routes[r].geojson);
			    			var marker = L.marker([data.routes[r].geojson.coordinates[0][1],data.routes[r].geojson.coordinates[0][0]]).addTo(map);
			    			marker.bindPopup("<b>routecode: "+data.routes[r].routecode+"</b><br>direction: " + data.routes[r].routedir);
	    			}
	    		});
	});
}

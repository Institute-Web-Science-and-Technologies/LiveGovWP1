
var markers = [];
var map;
var routes = L.layerGroup();


function updateMap() {
	// eat the event to prevent a unwanted click event
	return false;
}


function initilize(){
	// POINT(24.9396 60.17321)
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
	    			routes.clearLayers();
	    			var colors = ["#FF0000", "#00FF00"];
	    			for(var r in data.routes){
	    				routes.addLayer(L.geoJson(data.routes[r].geojson, {
	    				    style: {
		    				    "color": colors[parseInt(data.routes[r].routedir)-1],
		    				    "weight": 5,
		    				    "opacity": 0.65
		    				}
	    				}));
			    			var marker = L.marker([data.routes[r].geojson.coordinates[0][1],data.routes[r].geojson.coordinates[0][0]]);
			    			marker.bindPopup("<b>routecode: "+data.routes[r].routecode+"</b><br>direction: " + data.routes[r].routedir);
			    			routes.addLayer(marker);
			    			routes.addTo(map);
	    			}
	    		});
	});
}

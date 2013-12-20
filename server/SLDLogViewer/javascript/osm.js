
var markers;
var map;


function updateMap() {

  //markers.clearLayers();

}

function getdata(e){
  var ts = $(this).parent().next().text();
  $.ajax({
    url: "http://localhost:8080/" + ts,
    dataType: 'json',
    success: function(data, status){
      var myPoints = [];
      for(var c in data.inputCoordinates){
        myPoints[c] = [data.inputCoordinates[c].lat,data.inputCoordinates[c].lng];
        var m = L.marker(myPoints[c]).bindPopup("<b>" +
                          data.inputCoordinates[c].ts,
                          {minWidth:250} );
        markers.addLayer(m);
      }
      map.fitBounds(new L.LatLngBounds(myPoints));
      var routes = "<table border=1 ><tr><td>responseTime</td><td>" + data.responseTime +
                   "</td></tr><tr><td>samples</td><td>" +  data.inputCoordinates.length + "</td></tr>" + 
                   "</table><br><table border=1 >";
       for(var r in data.response.routes){
         routes+= "<tr><td>" + data.response.routes[r].route_id + "</td><td>" + 
	 data.response.routes[r].score + "</td><td>" + 
	 data.response.routes[r].trip_id + "</td><td>" + 
	 data.response.routes[r].transportation_mean + "</td></tr>";
       }
      $("#clusteringOnOff").html(routes + "</table>");
    },
    error: function(a,c) { alert("ajax: " + c);}
  });

}


function initilize(){
	map = L.map('map').setView([60.41742, 25.105215], 15);

   markers = L.layerGroup();
  //markers = new L.MarkerClusterGroup();


	L.tileLayer('http://{s}.tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/997/256/{z}/{x}/{y}.png', {
		maxZoom: 18
	}).addTo(map);

  // add a viewreset event listener for updating popups
  map.on('dragend', this.updateMap, this);
  // map.on('zoomend', this.updateMap, this);
  markers.addTo(map);


  //****************** timestamps ****************************
  $("#cat").html('<table id="catTable"><tr><th><input type="checkbox" id="checkall"/></th><th>Timestamp</th></tr></table>');

  

   $.ajax({
    url: "http://localhost:8080/keys",
    dataType: 'json',
    success: function(data, status){
      for(var k in data.keys){
	 $("#catTable tr:last").after('<tr><td><input class="cat" type="checkbox" /></td><td>' + data.keys[k] + '</td></tr>');
      }
      $("input.cat").click(getdata);
    },
    error: function(a,c) { alert("ajax: " + c);}
  });

}


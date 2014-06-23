
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
        var m = L.marker(myPoints[c], {icon:L.divIcon(),opacity:0.1,title:data.inputCoordinates[c].ts});
        markers.addLayer(m);
      }
      L.polyline(myPoints, {color: 'blue'}).addTo(map);
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


function initialize(){
	// map = L.map('map').setView([60.41742, 25.105215], 15);

   markers = L.layerGroup();
  //markers = new L.MarkerClusterGroup();


  var map = L.mapbox.map('map', 'rene.i6mdi15p', { // mapbox id
    legendControl: {
      position: 'topright'
    },
    maxZoom: 18
  }).setView([60.41742, 25.105215], 15);

  L.tileLayer('http://{s}.tiles.mapbox.com/v3/mapbox.mapbox-light/{z}/{x}/{y}.png', {
  attribution: '',
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
      data.keys.sort();
      for(var k = data.keys.length-1; k>=0; k--){
	 $("#catTable tr:last").after('<tr><td><input class="cat" type="checkbox" /></td><td>' + data.keys[k] + '</td></tr>');
      }
      $("input.cat").click(getdata);
    },
    error: function(a,c) { alert("ajax: " + c);}
  });

}


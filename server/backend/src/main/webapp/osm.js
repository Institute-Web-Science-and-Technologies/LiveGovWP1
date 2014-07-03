
var map;
var routes = L.layerGroup();
var sessionRouteCode = "";

function updateMap() {
	// eat the event to prevent a unwanted click event
	return false;
}


function initilize(){
	$('input:checkbox').removeAttr('checked');
	// POINT(24.9396 60.17321)
	map = L.map('map').setView([60.16946,24.95667], 12);

	L.tileLayer('https://{s}.tiles.mapbox.com/v3/examples.map-i87786ca/{z}/{x}/{y}.png', {
		maxZoom: 18,
		attribution: '<a href="http://www.mapbox.com/about/maps/" target="_blank">Terms &amp; Feedback</a>'
	}).addTo(map);


  map.on('dragend', this.updateMap, this);
  $("#uncheckall").click(function(){$('input:checkbox').removeAttr('checked');});
  $.ajax({type:"GET",
  	url:"/backend/LiveAPI",
  	success: function (data) {
  		var size = data.vehicles.length - 1;
  		var i =	Math.round(size * Math.random());
	     appendTableRow(data.vehicles[i]);
         sessionRouteCode = data.vehicles[i].trip_id;
  		},
  		dataType:"json"
  });
  
  window.setInterval(pollNewData, 5000);
  
  function pollNewData () {
	  $.ajax({type:"GET",
		  	url:"/backend/LiveAPI",
		  	success: function (data) {		  		
		  		  $.each(data.vehicles, function(index, item) {	  			     
		  		         if(sessionRouteCode == item.trip_id) {
		  		        	appendTableRow(item);
		  		         };
		  		  });
		  		},
		  		dataType:"json"
		  });
  }
  
  function appendTableRow(item){
	    var table = $("#vehicles");
        var table_row = $('<tr>');
        table_row.append($('<td>').html('<input type="checkbox">').click(checkboxCallback));
        table_row.append($('<td class="lat">').html(item.lat));
        table_row.append($('<td class="lon">').html(item.lon));
        table_row.append($('<td>').html(item.ts));
        table_row.append($('<td>').html(item.day));
        table_row.append($('<td>').html(item.route_id));
        table.append(table_row);
  };
  
  function checkboxCallback(){
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
	    $("#response").prepend('<div class="loading-centered"></div>');
	    $.ajax({type:"POST",
	    	url:"/backend/ServiceLineDetection",
	    	data: postData,
	    	headers:{"username":"test_user"},
	    	success: function (data) {
	    		var end = new Date().getTime();
	    		var table = $("#response");
	    		table.html("<tr><th>route_id</th><th>shape_id</th><th>trip_id</th><th>mean</th><th>score</th>");
	    		  $.each(data.routes, function(index, item){
	    		         var table_row = $('<tr>');
	    		         table_row.append($('<td>').html(item.route_id));
	    		         table_row.append($('<td>').html(item.shape_id));
	    		         table_row.append($('<td>').html(item.trip_id));
	    		         table_row.append($('<td>').html(item.transportation_mean));
	    		         table_row.append($('<td>').html(item.score));
	    		         table.append(table_row);
	    		    });
	    		  $("#timing").html(end-start + " ms");
	    		  $("#response").children('.loading-centered').remove();
	    		},
	    		dataType:"json"
	    });
	}
}

(function () {
  window.apiUrl = window.apiUrl || "http://localhost:3000/api/1";

  function Map() {
    this._map = L.map("domMap");
    this._map.on('load', function (e) {
      console.log("load!");
    });
    L.tileLayer('http://{s}.tile.cloudmade.com/{key}/{styleId}/256/{z}/{x}/{y}.png', {
      key: 'BC9A493B41014CAABB98F0471D759707',
      styleId: 997,
      attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://cloudmade.com">CloudMade</a>'
    }).addTo(this._map);
  }

  Map.prototype.clearAll = function() {
    for(i in this._map._layers) {
      if(this._map._layers[i]._path != undefined) {
        try {
          this._map.removeLayer(this._map._layers[i]);
        }
        catch(e) {
          console.log("problem with " + e + this._map._layers[i]);
        }
      }
    }
  };

  Map.prototype.addRoute = function(points) {
    var line = [];
    points.forEach(function (p) {
      line.push(new L.LatLng(p.lat, p.lon));
    });
    console.log(line[0]);
    this._map.setView(new L.LatLng(points[0].lat, points[0].lon), 13);
    L.polyline(line, {color: 'red'}).addTo(this._map);
  };

  Map.prototype.showAllForId = function(id) {
    var self = this;
    $.ajax({
      url: apiUrl + "/" + id + "/gps?limit=0"
    }).done(function (data) {
      self.addRoute(data);
    });
  };

  window.MyMap = Map;

})();
(function () {
  window.apiUrl = window.apiUrl || "/api/1";

  var maxTimeDifference = 5 * 60 * 1000;

  function Map() {
    this._map = L.map("domMap");
    L.tileLayer('http://{s}.tile.cloudmade.com/{key}/{styleId}/256/{z}/{x}/{y}.png', {
      key: 'BC9A493B41014CAABB98F0471D759707',
      styleId: 997,
      attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://cloudmade.com">CloudMade</a>'
    }).addTo(this._map);
    this._route = new Route('0');
    this._marker = false;
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

  Map.prototype.addMarker = function(lat, lon, txt) {
    if (this._marker) {
      this._map.removeLayer(this._marker);
    }
    var latLon = L.latLng(lat, lon);
    this._marker = new L.Marker(latLon);
    this._map.addLayer(this._marker);
    this._marker.bindPopup(txt).openPopup();
  };

  // Since every route has its own id now we dont have to seperate the points.
  // Only show the whole point list
  Map.prototype.addRoutes = function(points) {
    var self = this;
    self._route = new Route();
    if (points.length === 0) return;
    points.forEach(function (ele) {
      self._route.addPoint(ele);
    });
    self.drawRoute();
  };

  Map.prototype.showAllForId = function(id) {
    var self = this;
    $.ajax({
      url: apiUrl + "/" + id + "/gps?limit=0"
    }).done(function (data) {
      self.addRoutes(data);
    });
  };

  Map.prototype.drawRoute = function() {
    this.clearAll();
    this._route.draw(this._map);
  };

  window.MyMap = Map;
})();
(function () {
  
  function Route(label) {
    this._points = [];
    this._startTime = new Date();
    this._endTime = new Date();
    this.label = label;
  }

  Route.prototype.addPoint = function(point) {
    if(this._points.length === 0) {
      this._startTime = new Date(point.ts);
    }
    this._points.push(new L.LatLng(point.lat, point.lon));
    this._endTime = new Date(point.ts);
  };

  Route.prototype.draw = function(map) {
    map.setView(new L.LatLng(this._points[0].lat, this._points[0].lng), 13);
    L.polyline(this._points, {color: 'blue'}).addTo(map);
  };

  window.Route = Route;

})();
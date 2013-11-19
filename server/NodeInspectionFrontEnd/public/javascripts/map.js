(function () {
  window.apiUrl = window.apiUrl || "http://localhost:3000/api/1";

  var maxTimeDifference = 5 * 60 * 1000;

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
    this._routes = [];
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

  Map.prototype.addRoutes = function(points) {
    var self = this;
    self._routes = [];
    var currentRoute = new Route("0");
    var currentRouteNumber = 0;
    var lastTime = -1;
    var eleTime = 0;
    points.forEach(function (ele) {
      eleTime = new Date(ele.ts);
      if(lastTime >= 0 && eleTime - lastTime >= maxTimeDifference) {
        console.log("New Route");
        self._routes.push(currentRoute);
        currentRoute = new Route("" + (++currentRouteNumber));
      }
      currentRoute.addPoint(ele);
      lastTime = eleTime;
    });
    self._routes.push(currentRoute);
    $("#routeMenu").empty();
    self._routes.forEach(function (ele, index) {
      $("#routeMenu").append('<div class="item" data-value="'+ index +'">Route '+ ele.label + ' ('+ ele._points.length +') </div>');
      ele.draw(self._map);
    });
    $('#route').dropdown({
        onChange: function (value) {
          window.lMap.drawRoute(value);
        }
    });
  };

  Map.prototype.showAllForId = function(id) {
    var self = this;
    $.ajax({
      url: apiUrl + "/" + id + "/gps?limit=0"
    }).done(function (data) {
      self.addRoutes(data);
    });
  };

  Map.prototype.drawRoute = function(id) {
    this.clearAll();
    console.log(id);
    this._routes[id].draw(this._map);
    window.limitToTime(this._routes[id]._startTime, this._routes[id]._endTime);
  };

  window.MyMap = Map;

})();
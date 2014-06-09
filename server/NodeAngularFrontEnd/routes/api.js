// jshint esnext:true

// https://github.com/chilts/koa-pg
// https://github.com/basicdays/co-pg
// http://stackoverflow.com/a/23882624/220472

var schemas = {
  'gps': 'sensor_gps',
  'har': 'har_annotation',
  'lac': 'sensor_linear_acceleration',
  'acc': 'sensor_accelerometer',
  'gra': 'sensor_gravity'
};


module.exports = function(api) {
  api
    .get('trips', '/trips/:trip_id?', trips)
    .get('sensor', '/trips/:trip_id/sensors/:sensor', sensor)
    .get('count', '/trips/:trip_id/sensors/:sensor/count', count)
    .post('updateTrip', '/trips/:trip_id', updateTrip)
    .delete('destroyTrip', '/trips/:trip_id', destroyTrip);

  // arg: [ 1389559420371, 1389559423048 ]
  function extentToSQL(extent) {
    return ' AND ts >= ' + extent[0] + ' AND ts <= ' + extent[1];
  }

  function *trips() {
    var $1 = this.params.trip_id ? 'WHERE trip_id = ' + this.params.trip_id : "";
    var q = 'SELECT * FROM trip ' + $1 + ' ORDER BY trip_id DESC';
    var result;

    try {
      result = yield this.pg.db.client.query_(q);
    } catch (err) {
      throw new Error('COULD NOT GET TRIPS');
    }
    this.body = result.rows;
  }


  function *sensor() {
    if (Object.keys(schemas).indexOf(this.params.sensor) < 0) return;
    var extent = this.query.e ? extentToSQL(this.query.e) : '';
    var result, q;

    switch (this.params.sensor) {
      case 'gps':
        q = 'SELECT ts, ST_AsGeoJSON(lonlat)::json AS lonlat FROM ' + schemas[this.params.sensor] + ' WHERE trip_id = ' + this.params.trip_id + extent;
        break;
      case 'lac':
      case 'acc':
      case 'gra':
        if (this.query.w) { // query window size
          console.log('sensor');
          q = 'SELECT avg(x) AS x, avg(y) AS y, avg(z) AS z, min(ts) AS starttime, max(ts) AS endtime FROM (SELECT x, y, z, ts, NTILE(' + this.query.w + ') OVER (ORDER BY ts) AS w FROM ' + schemas[this.params.sensor] + ' WHERE trip_id = ' + this.params.trip_id + extent + ') A GROUP BY w ORDER BY w';
          break;
        }
        /* falls through */
      default:
        q = 'SELECT * FROM ' + schemas[this.params.sensor] + ' WHERE trip_id = ' + this.params.trip_id + extent;
    }

    try {
      result = yield this.pg.db.client.query_(q);
    } catch (err) {
      this.status = 500;
      throw new Error('COULD NOT GET SENSOR DATA');
    }
    this.body = result.rows;
  }


  function *count() {
    if (Object.keys(schemas).indexOf(this.params.sensor) < 0) return;
    var extent = this.query.e ? extentToSQL(this.query.e) : '';
    var q = "SELECT COUNT(ts) FROM " + schemas[this.params.sensor] + " WHERE trip_id = " + this.params.trip_id + extent;
    var result;

    try {
      result = yield this.pg.db.client.query_(q);
    } catch (err) {
      this.status = 500;
      throw new Error('COULD NOT COUNT');
    }
    this.body = result.rows;
  }


  function *updateTrip() {
    // parse the request body and create key-value-pairs as part of the sql statement
    var fields = Object.keys(this.request.body).map(function(k) {
      return k + ' = \'' + this.request.body[k] + '\'';
    }, this).join(", ");

    var q = 'UPDATE trip SET ' + fields + ' WHERE trip_id = ' + this.params.trip_id;

    try {
      var result = yield this.pg.db.client.query_(q);
    } catch (err) {
      this.status = 507;
      throw new Error('COULD NOT UPDATE TRIP');
    }
    this.status = 204;
  }


  function *destroyTrip() {
    var q = 'DELETE FROM trip WHERE trip_id = ' + this.params.trip_id;
    // add additional sql statements to delete the corresponding data from other tables
    for (var table in schemas) {
      q += '; DELETE FROM ' + schemas[table] + ' WHERE trip_id = ' + this.params.trip_id;
    }

    try {
      var result = yield this.pg.db.client.query_(q);
    } catch (err) {
      this.status = 500;
      throw new Error('COULD NOT DELETE TRIP');
    }
    this.status = 204;
  }
};


// jshint esnext:true

(function() {
	'use strict';
	var app = require('koa')();
	var mount = require('koa-mount');
  var serve = require('koa-static');

  var _ = require('lodash');

  // sensor_accelerometer       | table | liveandgov
  // sensor_gravity             | table | liveandgov
  // sensor_gyroscope           | table | liveandgov
  // sensor_linear_acceleration | table | liveandgov
  // sensor_magnetic_field      | table | liveandgov
  // sensor_rotation            | table | liveandgov

  // sensor_gact                | table | liveandgov
  // sensor_gps                 | table | liveandgov
  // sensor_har                 | table | liveandgov
  // sensor_proximity           | table | liveandgov
  // sensor_tags                | table | liveandgov
  // sensor_velocity            | table | liveandgov
  // sensor_waiting             | table | liveandgov

  // har_annotation             | table | liveandgov

  // service_sld_trips          | view  | postgres

  // trip                       | table | liveandgov

  // sensor tables w/ ts, x, y and z columns
  var sensors = [
    'sensor_accelerometer',
    'sensor_gravity',
    'sensor_gyroscope',
    'sensor_linear_acceleration',
    'sensor_magnetic_field',
    'sensor_rotation'
  ];

  var moreSensors = [
    'har_annotation',
    'sensor_gact',
    'sensor_gps',
    'sensor_har',
    'sensor_proximity',
    'sensor_tags',
    'sensor_velocity',
    'sensor_waiting',
    'service_sld_trips'
  ];

  // list all tables
  // SELECT c.relname FROM pg_catalog.pg_class c LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace WHERE c.relkind IN ('r','') AND n.nspname <> 'pg_catalog'AND n.nspname <> 'information_schema'AND n.nspname !~ '^pg_toast'AND pg_catalog.pg_get_userbyid(c.relowner) != 'postgres' AND pg_catalog.pg_table_is_visible(c.oid) ORDER BY 1;

  // list all columns for a table
  // SELECT attname FROM pg_attribute WHERE attrelid = 'public.sensor_tags'::regclass AND attnum > 0 AND NOT attisdropped ORDER BY attnum;

  // specify and generate sql queries here
  var queries = {
    trips:
      'SELECT trip_id AS id, user_id AS user, start_ts AS start, stop_ts AS stop, name AS comment FROM trip ORDER BY trip_id DESC',

    check: function(id) {
      return sensors.concat(moreSensors).map(function(sensor) {
        return 'SELECT EXISTS(SELECT 1 FROM ' + sensor + ' WHERE trip_id = ' + id + ') AS ' + sensor;
      }).join('; ');
    },

    count: function(id) {
      return sensors.concat(moreSensors).map(function(sensor) {
        return 'SELECT (SELECT COUNT(ts) FROM ' + sensor + ' WHERE trip_id = ' + id + ') AS ' + sensor;
      }).join('; ');
    },

    sensor: function(id, sensor, windowSize, extent) {
      var q;
      if (sensor === 'sensor_har') {
        q = 'SELECT ts, tag FROM sensor_har WHERE trip_id = ' + id + 'ORDER BY ts ASC';
      } else if (sensor === 'sensor_gps') {
        q = 'SELECT ts, ST_AsGeoJSON(lonlat)::json AS lonlat FROM sensor_gps WHERE trip_id = ' + id + 'ORDER BY ts ASC';
      } else if (sensor === 'test') {
        q = '\\dt';
      } else if (windowSize) {
        q = 'SELECT avg(x) AS x, avg(y) AS y, avg(z) AS z, min(ts) AS start, max(ts) AS stop FROM (SELECT x, y, z, ts, NTILE(' + windowSize + ') OVER (ORDER BY ts) AS w FROM ' + sensor + ' WHERE trip_id = ' + id + extent + ') A GROUP BY w ORDER BY w';
      } else {
        q = 'SELECT * from ' + sensor + ' WHERE trip_id = ' + id + extent + 'ORDER BY ts ASC';
      }
      return q;
    },

    delete: function(id) {
      // delete the trip from all available tables
      return sensors.concat('sensor_gps', 'sensor_har', 'trip').map(function(sensor) {
        return 'DELETE FROM ' + sensor + ' WHERE trip_id = ' + id;
      }).join('; ');
    },

    update: function(id, data) {
      // parse the request body and create key-value-pairs as part of the sql statement
      var fields = Object.keys(data).map(function(key) {
        return key + ' = \'' + data[key] + '\'';
      }, this).join(', ');

      return 'UPDATE trip SET ' + fields + ' WHERE trip_id = ' + id;
    },
  };

  app.use(require('koa-views')(__dirname + '/src/jade', { default: 'jade' }));
  app.use(require('koa-logger')());
  app.use(require('koa-body')());
  app.use(require('koa-compress')());
  app.use(serve('public'));
  app.use(mount('/bower_components', serve('bower_components')));
	app.use(require('koa-pg')('pg://postgres:liveandgov@localhost:3333/liveandgov_dev'));




  // app.use(function *(next) {
  //   var getAllTables = "SELECT c.relname FROM pg_catalog.pg_class c LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace WHERE c.relkind IN ('r','') AND n.nspname <> 'pg_catalog'AND n.nspname <> 'information_schema'AND n.nspname !~ '^pg_toast'AND pg_catalog.pg_get_userbyid(c.relowner) != 'postgres' AND pg_catalog.pg_table_is_visible(c.oid) ORDER BY 1";
  //   var getAllColumns = function(table) {
  //     return "SELECT attname FROM pg_attribute WHERE attrelid = 'public." + table + "'::regclass AND attnum > 0 AND NOT attisdropped ORDER BY attnum";
  //   };

  //   var tables = yield _.flatten(_.map((yield this.pg.db.client.query_(getAllTables)).rows, _.values));
  //   // var tables = yield this.pg.db.client.query_(getAllTables);

  //   var columns = _.map(tables, function(table) {
  //     // console.log(table);
  //     // yield table;
  //     var a = this.pg.db.client.query_(getAllColumns(table));
  //     console.log(a);
  //   }.bind(this));

  //   // console.log(_.flatten(_.map(tables.rows, _.values)));
  //   // console.log(tables);
  //   console.log(columns);
  //   yield next;
  // });




	var Router = require('koa-router');
	var api = new Router();

  // arg: [ 1389559420371, 1389559423048 ]
  function extentToSQL(extent) {
    var e = extent.split(',');
    return ' AND ts >= ' + e[0] + ' AND ts <= ' + e[1];
  }

  // check a trip for available sensor data
  //   curl -s localhost:3476/trips/850/check
  api.get('/trips/:tripId/check', function *() {
    var result = yield this.pg.db.client.query_(q);
    this.body = result.rows;
  });

  // count sensor data for a trip
  //   curl -s localhost:3476/trips/850/count
  api.get('/trips/:tripId/count', function *() {
    var result = yield this.pg.db.client.query_(queries.count(this.params.tripId));
    this.body = result.rows;
  });

  // get sensor data for a trip
  //   curl -s localhost:3476/trips/850/acc
  //   curl -s localhost:3476/trips/850/acc\?w=200
  //   curl -s localhost:3476/trips/850/acc\?w=200\&e=1394518675333,1394518346639
  api.get('/trips/:tripId/:sensor', function *() {
    var extent = this.query.e ? extentToSQL(this.query.e) : '';
    var result = yield this.pg.db.client.query_(
      queries.sensor(this.params.tripId, this.params.sensor, this.query.w, extent));
    this.body = result.rows;
  });

  // get all trips
  //   curl -s localhost:3476/trips
  api.get('/trips', function *() {
    var result = yield this.pg.db.client.query_(queries.trips);
    this.body = result.rows;
  });

  // delete a trip
  api.del('/trips/:tripId', function *() {
    yield this.pg.db.client.query_(queries.delete(this.params.tripId));
    this.status = 204;
  });

  // update a trip
  api.post('/trips/:tripId', function *() {
    yield this.pg.db.client.query_(queries.update(this.params.tripId, this.request.body));
    this.status = 204;
  });

  // get index html template
  api.get('/', function *index() {
    yield this.render('index');
  });

	app.use(mount('/', api.middleware()));

	app.listen(3476);
	console.log('running on http://localhost:3476');
}());

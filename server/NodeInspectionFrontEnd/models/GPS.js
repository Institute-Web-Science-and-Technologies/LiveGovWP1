var _ = require('underscore')
  , pg = require('pg')
  , config = require('../config.js');

/// Returns an array of gps points with the given id.
/// Use the options parameter to define a limit.
function getById (id, options, callback) {
  if(_.isFunction(options)) {
    // Check if we want to use the default options
    callback = options;
    options = { limit: 20 };
  } else if(!_.isObject(options)) {
    throw new TypeError("Bad arguments");
  }

  pg.connect(config.pgCon, function (err, client, done) {
    if(err) { callback(err); done(); return; }
    var query = 'SELECT ts,ST_AsGeoJSON(lonlat) FROM sensor_gps WHERE trip_id=$1 ORDER BY ts';
    var values = [id];
    if(options.limit && options.limit !== 0) {
      query += ' LIMIT $2';
      values.push(options.limit);
    }
    client.query(query, values, function (err, result) {
      done();
      if(err) { callback(err); return; }
      var data = _.map(result.rows, function (e) {
        var geo = JSON.parse(e.st_asgeojson);
        return { 
          ts: e.ts,
          lon: geo.coordinates[0],
          lat: geo.coordinates[1]
        }
      });
      callback(null, data);
    });
  });
};

function getCountForId (id, options, callback) {
  if(_.isFunction(options)) {
    // Check if we want to use the default options
    callback = options;
  } else if(!_.isObject(options)) {
    throw new TypeError("Bad arguments");
  }

  pg.connect(config.pgCon, function (err, client, done) {
    if(err) { callback(err); done(); return; }
    var query = 'SELECT COUNT(*) FROM sensor_gps WHERE trip_id=$1';
    client.query(query, [id], function (err, result) {
      done();
      if(err) { callback(err); return; }
      callback(null, {count: result.rows[0].count});
    });
  });
}

function getNearestToTimeWithId (id, ts, callback) {
  if (!_.isFunction(callback)) {
    throw new TypeError("Bad arguments");
  }

  pg.connect(config.pgCon, function (err, client, done) {
    if(err) { callback(err); done(); return; }
    var query = 'SELECT ABS( ts - $1 ) AS a, '
              + '       ST_AsGeoJSON(lonlat) AS geojson '
              + 'FROM sensor_gps '
              + 'WHERE trip_id=$2 '
              + 'ORDER BY a '
              + 'LIMIT 1';
    var values = [ts, id];
    client.query(query, values, function (err, result) {
      done();
      if(err) { callback(err); return; }
      var e = result.rows[0];
      var geo = JSON.parse(e.geojson);
      var data = { 
        diff: e.a,
        lon: geo.coordinates[0],
        lat: geo.coordinates[1]
      }
      callback(null, data);
    });
  });
}

function getRawForId (id, options, callback) {
  if(_.isFunction(options)) {
    // Check if we want to use the default options
    callback = options;
  } else if(!_.isObject(options)) {
    throw new TypeError("Bad arguments");
  }
  pg.connect(config.pgCon, function (err, client, done) {
    if(err) { callback(err); done(); return; }
    var query = 'SELECT id,ts,ST_AsGeoJSON(lonlat) FROM sensor_gps WHERE trip_id=$1'
    var values = [id];
    if(options.startTime && !options.endTime) {
      limitTime = "AND ts>=$2";
      values.push(options.startTime);
    } else if(!options.startTime && options.endTime) {
      limitTime = "AND ts<=$2";
      values.push(options.endTime);
    } else if(options.startTime && options.endTime) {
      limitTime = "AND ts>=$2 AND ts<=$3";
      values.push(options.startTime);
      values.push(options.endTime);
    }
    query += limitTime;
    client.query(query, values, function (err, result) {
      done();
      if(err) { callback(err); return; }
      var data = _.map(result.rows, function (e) {
        var geo = JSON.parse(e.st_asgeojson);
        return { 
          ts: e.ts,
          lon: geo.coordinates[0],
          lat: geo.coordinates[1]
        }
      });
      callback(null, data);
    });
  });
}

module.exports = {
  getById: getById,
  getCountForId: getCountForId,
  getRawForId: getRawForId,
  getNearestToTimeWithId: getNearestToTimeWithId
};
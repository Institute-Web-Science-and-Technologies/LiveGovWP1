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
    var query = 'SELECT ts,ST_AsGeoJSON(lonlat) FROM gps WHERE id=$1 ORDER BY ts';
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
    var query = 'SELECT COUNT(*) FROM gps WHERE id=$1';
    client.query(query, [id], function (err, result) {
      done();
      if(err) { callback(err); return; }
      callback(null, {count: result.rows[0].count});
    });
  });
}

function getNearestToTimeWithId (id, callback) {
  // body...
}

module.exports = {
  getById: getById,
  getCountForId: getCountForId
};
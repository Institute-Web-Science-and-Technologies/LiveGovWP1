var _ = require('underscore')
  , pg = require('pg')
  , config = require('../config.js');

function getById (id, options, callback) {
  if(_.isFunction(options)) {
    // Check if we want to use the default options
    callback = options;
  } else if(!_.isObject(options)) {
    throw new TypeError("Bad arguments");
  }

  pg.connect(config.pgCon, function (err, client, done) {
    if(err) { callback(err); done(); return; }
    var query = "SELECT * FROM har_annotation WHERE trip_id=$1";
    var values = [id];
    var limitTime = '';
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
      callback(null, result.rows);
    });
  });
}

module.exports = {
  getById: getById
};
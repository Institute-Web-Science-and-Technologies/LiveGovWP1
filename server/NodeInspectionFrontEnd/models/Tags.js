var _ = require('underscore')
  , pg = require('pg')
  , config = require('../config.js');

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
    var query = 'SELECT ts,tag FROM sensor_tags WHERE id=$1 ORDER BY ts';
    var values = [id];
    if(options.limit) {
      query += ' LIMIT $2';
      values.push(options.limit);
    }
    client.query(query, values, function (err, result) {
      done();
      if(err) { callback(err); return; }
      var data = _.map(result.rows, function (e) {
        var trimed = e.tag.trim();
        trimed = trimed.substr(1, trimed.length - 2);
        return { 
          ts: e.ts,
          tag: trimed
        };
      });
      callback(null, data);
    });
  });
};

module.exports = {
  getById: getById
};
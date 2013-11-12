var _ = require('underscore')
  , pg = require('pg')
  , config = require('../config.js');

function saveWindow (tag, id, start, end, callback) {
  pg.connect(config.pgCon, function (err, client, done) {
    if(err) { callback(err); done(); return; }
    start = parseInt(start);
    end = parseInt(end);
    var query = "INSERT INTO raw_training_data (type, ts, x, y, z, tag) SELECT 'acc' AS type,ts,x,y,z, $1 as tag";
    query += " FROM accelerometer WHERE id=$2 AND ts >= TIMESTAMP '"+(new Date(start)).toISOString()+"' AND ts <= TIMESTAMP '"+(new Date(end)).toISOString()+"'";
    var values = [tag, id];
    client.query(query, values, function (err, result) {
      done();
      if(err) { callback(err); return; }
      callback(null);
    });
  });
}

module.exports = {
  saveWindow: saveWindow
};
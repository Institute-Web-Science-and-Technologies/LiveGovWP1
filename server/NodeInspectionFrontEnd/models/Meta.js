var _ = require('underscore')
  , pg = require('pg')
  , config = require('../config.js');

function getAllIds(callback) {
  pg.connect(config.pgCon, function (err, client, done) {
    if(err) { callback(err); done(); return; }
    client.query("SELECT * FROM trips;", function (err, data) {
      done();
      if(err) { callback(err); return; }
      var result = _.map(data.rows, function(e) {
        return {
          trip_id: e.trip_id,
          userId: e.user_id,
          duration: e.duration,
          name: e.name
        };
      });
      callback(null, result);
    });
  });
}

module.exports = {
  getAllIds: getAllIds
};
var _ = require('underscore')
  , pg = require('pg')
  , config = require('../config.js');

function getAllIds(callback) {
  pg.connect(config.pgCon, function (err, client, done) {
    if(err) { callback(err); done(); return; }
    client.query("SELECT id, COUNT(*) FROM gps GROUP BY id;", function (err, data) {
      done();
      if(err) { callback(err); return; }
      var result = _.map(data.rows, function(e) {
        return {
          devId: e.id,
          gpsCount: e.count
        };
      });
      callback(null, result);
    });
  });
}

module.exports = {
  getAllIds: getAllIds
};
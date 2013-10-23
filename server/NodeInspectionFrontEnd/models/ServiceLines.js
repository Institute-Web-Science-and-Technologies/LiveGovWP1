var _ = require('underscore')
  , pg = require('pg')
  , config = require('../config.js');

function getAll(callback) {
  pg.connect(config.pgCon, function (err, client, done) {
    if(err) { callback(err); done(); return; }
    var query = "SELECT id, linename, transportmean FROM lineinfo WHERE language='s';";
    client.query(query, function (err, result) {
      done();
      if(err) { callback(err); return; }
      var data = _.map(result.rows, function (e) {
        return {
          id: e.id,
          linename: e.linename,
          type: e.transportmean
        };
      });
      callback(null, data);
    });
  });
}

module.exports = {
  getAll: getAll
};
var _ = require('underscore')
  , pg = require('pg')
  , config = require('../config.js')
  , moment = require('moment');


function getAllIds(callback) {
  pg.connect(config.pgCon, function (err, client, done) {
    if(err) { callback(err); done(); return; }
    client.query("SELECT * FROM trip ORDER BY trip_id DESC;", function (err, data) {
      done();
      if(err) { callback(err); return; }
      var result = _.map(data.rows, function(e) {
        var start = moment(parseInt(e.start_ts))
          , end = moment(parseInt(e.stop_ts));
        return {
          tripId: e.trip_id,
          userId: e.user_id,
          startTime: start.format('YYYY/MM/DD hh:mm:ss'),
          endTime: end.format('YYYY/MM/DD hh:mm:ss'),
          duration: moment(end.diff(start)).format('hh:mm:ss'),
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

var apiV1 = require('./routes/apiV1.js')
  , csv = require('./routes/csv.js');
  , trip = require('./routes/trip.js')
  , pg = require('pg');

function register (app) {
  app.get('/api/1/devices', apiV1.getAllIds);

  app.get('/api/1/:id/gps', apiV1.getGPS);
  app.get('/api/1/:id/gps/count', apiV1.getGPSCount);
  app.get('/api/1/:id/gps/:ts', apiV1.getGPSNearToTS);

  app.get('/api/1/:id/acc', apiV1.getAccWindow);
  app.get('/api/1/:id/acc/count', apiV1.getAccCount);

  app.get('/api/1/:id/lac', apiV1.getLacWindow);
  app.get('/api/1/:id/lac/count', apiV1.getLacCount);

  app.get('/api/1/:id/gra', apiV1.getGraWindow);
  app.get('/api/1/:id/gra/count', apiV1.getGraCount);

  app.get('/api/1/:id/tag', apiV1.getTags);

  app.get('/api/1/:id/har', apiV1.getHAR);

  app.post('/api/1/:id/window', apiV1.postWindow);

  app.get('/api/1/csv/:id/acc', csv.getAccRaw);
  app.get('/api/1/csv/:id/gps', csv.getGpsRaw);
  app.get('/api/1/csv/:id/lac', csv.getLacRaw);
  app.get('/api/1/csv/:id/gra', csv.getGraRaw);
  app.get('/trip/:id', trip.getTrip);

  app.get('/trips/:id/delete', function(req, res) {
    pg.connect("pg://postgres:liveandgov@localhost/liveandgov", function (err, client, done) {
      if (err) {callback(err, null); done(); return; }
      query = "DELETE FROM trip WHERE trip_id = " + req.params.id + ";";
      client.query(query, function (err, result) {
        done();
      });
      if (err) { res.end(err) } else { res.redirect('back'); }
    });
  });

  app.post('/trips/:id', function(req, res) {
    pg.connect("pg://postgres:liveandgov@localhost/liveandgov", function (err, client, done) {
      if (err) {callback(err, null); done(); return; }
      if (req.body.value === undefined || req.params.id === undefined) {
        done(); return;
      }
      query = "UPDATE trip SET name = '" + req.body.value + "' WHERE trip_id = " + req.params.id + ";";
      client.query(query, function (err, result) {
        console.log("XXX: " + req.body.value + ", " + req.params.id);
        done();
      });
      if (err) { res.end(err) } else { res.redirect('back'); }
    });
  });
}

module.exports = register;

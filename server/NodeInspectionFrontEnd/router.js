var apiV1 = require('./routes/apiV1.js')
  , trip = require('./routes/trip.js');

function register (app) {
  app.get('/api/1/devices', apiV1.getAllIds);

  app.get('/api/1/:id/gps', apiV1.getGPS);
  app.get('/api/1/:id/gps/count', apiV1.getGPSCount);

  app.get('/api/1/:id/acc', apiV1.getAccWindow);
  app.get('/api/1/:id/acc/count', apiV1.getAccCount);

  app.get('/api/1/:id/lac', apiV1.getLacWindow);
  app.get('/api/1/:id/lac/count', apiV1.getLacCount);

  app.get('/api/1/:id/gra', apiV1.getGraWindow);
  app.get('/api/1/:id/gra/count', apiV1.getGraCount);

  app.get('/api/1/:id/tag', apiV1.getTags);

  app.post('/api/1/:id/window', apiV1.postWindow);

  app.get('/trip/:id', trip.getTrip);
}

module.exports = register;
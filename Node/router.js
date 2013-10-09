var apiV1 = require('./routes/apiV1.js');

function register (app) {
  app.get('/api/1/devices', apiV1.getAllIds);

  app.get('/api/1/:id/gps', apiV1.getGPS);
  app.get('/api/1/:id/gps/count', apiV1.getGPSCount);

  app.get('/api/1/:id/acc', apiV1.getAccWindow);
  app.get('/api/1/:id/acc/count', apiV1.getAccCount);

  app.get('/api/1/:id/tag', apiV1.getTags);
}

module.exports = register;
var csv = require('express-csv')
  , gps = require('../models/GPS.js')
  , acc = require('../models/Accelerometer.js')
  , lac = require('../models/LinearAcceleration.js')
  , gra = require('../models/Gravity.js');

function getAccRaw (req, res) {
  options = {};
  if(req.query.startTime) options.startTime = parseInt(req.query.startTime);
  if(req.query.endTime)   options.endTime = parseInt(req.query.endTime);
  acc.getRawForId(req.params.id, options, function (err, data) {
    if(err) { res.send(err); console.error(err); return; }
    res.csv(data);
  });
}

function getGpsRaw (req, res) {
  options = {};
  if(req.query.startTime) options.startTime = parseInt(req.query.startTime);
  if(req.query.endTime)   options.endTime = parseInt(req.query.endTime);
  gps.getRawForId(req.params.id, options, function (err, data) {
    if(err) { res.send(err); console.error(err); return; }
    res.csv(data);
  });
}

function getLacRaw (req, res) {
  options = {};
  if(req.query.startTime) options.startTime = parseInt(req.query.startTime);
  if(req.query.endTime)   options.endTime = parseInt(req.query.endTime);
  lac.getRawForId(req.params.id, options, function (err, data) {
    if(err) { res.send(err); console.error(err); return; }
    res.csv(data);
  });
}

function getGraRaw (req, res) {
  options = {};
  if(req.query.startTime) options.startTime = parseInt(req.query.startTime);
  if(req.query.endTime)   options.endTime = parseInt(req.query.endTime);
  gra.getRawForId(req.params.id, options, function (err, data) {
    if(err) { res.send(err); console.error(err); return; }
    res.csv(data);
  });
}

module.exports = {
  getAccRaw: getAccRaw,
  getGpsRaw: getGpsRaw,
  getLacRaw: getLacRaw,
  getGraRaw: getGraRaw
};
var gps = require('../models/GPS.js')
  , meta = require('../models/Meta.js')
  , acc = require('../models/Accelerometer.js')
  , lac = require('../models/LinearAcceleration.js')
  , gra = require('../models/Gravity.js')
  , tag = require('../models/Tags.js')
  , trainingWindow = require('../models/TrainingWindow.js');

function getAllIds(req, res) {
  meta.getAllIds(function (err, data) {
    if(err) { res.send(err); console.error(err); return; }
    return res.send(data);
  });
}

/// @brief Returns the GPS values stored inside the database
function getGPS (req, res) {
  var options = {limit: 20};
  if(req.query.limit) options.limit = parseInt(req.query.limit);
  gps.getById(req.params.id, options, function (err, data) {
    if(err) { res.send(err); console.error(err); return; }
    return res.send(data);
  });
}

function getGPSCount (req, res) {
  gps.getCountForId(req.params.id, function (err, data) {
    if(err) { res.send(err); console.error(err); return; }
    return res.send(data);
  });
}

function getAccWindow (req, res) {
  var options = { windows: 200 };
  if(req.query.windows)   options.windows = parseInt(req.query.windows);
  if(req.query.startTime) options.startTime = new Date(parseInt(req.query.startTime));
  if(req.query.endTime)   options.endTime = new Date(parseInt(req.query.endTime));
  acc.getWindowsForId(req.params.id, options, function (err, data) {
    if(err) { res.send(err); console.error(err); return; }
    return res.send(data);
  });
}

function getAccCount (req, res) {
  acc.getCountForId(req.params.id, function (err, data) {
    if(err) { res.send(err); console.error(err); return; }
    return res.send(data);
  });
}

function getLacWindow (req, res) {
  var options = { windows: 200 };
  if(req.query.windows)   options.windows = parseInt(req.query.windows);
  if(req.query.startTime) options.startTime = new Date(parseInt(req.query.startTime));
  if(req.query.endTime)   options.endTime = new Date(parseInt(req.query.endTime));
  lac.getWindowsForId(req.params.id, options, function (err, data) {
    if(err) { res.send(err); console.error(err); return; }
    return res.send(data);
  });
}

function getLacCount (req, res) {
  lac.getCountForId(req.params.id, function (err, data) {
    if(err) { res.send(err); console.error(err); return; }
    return res.send(data);
  });
}

function getGraWindow (req, res) {
  var options = { windows: 200 };
  if(req.query.windows)   options.windows = parseInt(req.query.windows);
  if(req.query.startTime) options.startTime = new Date(parseInt(req.query.startTime));
  if(req.query.endTime)   options.endTime = new Date(parseInt(req.query.endTime));
  gra.getWindowsForId(req.params.id, options, function (err, data) {
    if(err) { res.send(err); console.error(err); return; }
    return res.send(data);
  });
}

function getGraCount (req, res) {
  gra.getCountForId(req.params.id, function (err, data) {
    if(err) { res.send(err); console.error(err); return; }
    return res.send(data);
  });
}

function getTags (req, res) {
  tag.getById(req.params.id, function (err, data) {
    if(err) { res.send(err); console.error(err); return; }
    return res.send(data);
  });
}

function postWindow (req, res) {
  trainingWindow.saveWindow(req.body.tag, req.params.id, req.body.start, req.body.end, function (err) {
    res.send(err);
  });
}

module.exports = {
  getGPS: getGPS,
  getGPSCount: getGPSCount,
  getAllIds: getAllIds,
  getAccWindow: getAccWindow,
  getAccCount: getAccCount,
  getLacWindow: getLacWindow,
  getLacCount: getLacCount,
  getGraWindow: getGraWindow,
  getGraCount: getGraCount,
  getTags: getTags,
  postWindow: postWindow
};
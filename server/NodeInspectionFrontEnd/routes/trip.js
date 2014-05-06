var gps = require("../models/GPS.js")
  , meta = require("../models/Meta.js")
  , sl = require("../models/ServiceLines.js")
  , async = require("async")
  , _ = require("underscore");


function getTrip (req, res) {
  res.render('trip', { title: 'Trip - LiveGovWP1' });
}

module.exports = {
  getTrip: getTrip
}
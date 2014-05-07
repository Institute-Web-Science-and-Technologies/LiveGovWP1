var gps = require("../models/GPS.js")
  , meta = require("../models/Meta.js")
  , sl = require("../models/ServiceLines.js")
  , async = require("async")
  , _ = require("underscore");



/*
 * GET home page.
 */

exports.index = function(req, res){
  function transportTypeString(id) {
    switch(id) {
      case 1:
        return "TransportType1";
      case 2:
        return "TransportType2";
    }
    return "Unknown type " + id;
  }

  async.parallel([
    function (callback) {
      meta.getAllIds(function (err, data) {
        callback(err, data);
      });
    },
    function (callback) {
      sl.getAll(function (err, data) {
        var result = _.map(data, function (e) {
          e.type = transportTypeString(e.type);
          return e;
        });
        callback(err, data);

      });
    }], function (err, result) {
      if(err) { res.send(err); return; }
      res.render('index', { title: 'LiveGovWP1', trips: result[0], serviceLines: result[1] });
    });
};
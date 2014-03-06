var zmq = require('zmq')
  , sock = zmq.socket('pull')
  , config = require('./config.js')
  , http = require('http')
  , sockjs = require('sockjs')
  , _ = require('underscore')
  , url = require('url')
  , fs = require('fs')
  , path = require('path')
  , static = require('node-static');


// ZMQ
sock.connect(config.socketAddress);
console.log("Connected to", config.socketAddress);

sock.on('message', function (msg) {
  msg = msg.toString();
  var matched = msg.match(config.actRE)
  if (!matched) {
    // No activity!
    return;
  }
  var m = JSON.stringify({
    ts: matched[1],
    id: matched[2],
    act: matched[3].toUpperCase()
  });
  _.each(connections, function (conn) {
    conn.write(m);
  });
});

// Websocket
connections = [];

var ws = sockjs.createServer();
ws.on('connection', function (conn) {
  connections.push(conn);
  console.log("Websocket connected");

  // Remove connection on close
  conn.on('close', function () {
    console.log("Websocket disconnected");
    connections = _.without(connections, conn);
  });
});

// Server HTML Files
var file = new static.Server('./public');
var server = http.createServer(function (req, res) {
  req.addListener('end', function () {
    file.serve(req, res);
  }).resume();
});
ws.installHandlers(server, {prefix:'/socket'});
server.listen(9999, '0.0.0.0');

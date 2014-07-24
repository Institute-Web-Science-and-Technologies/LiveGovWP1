var zmq = require('zmq')
  , sock = zmq.socket('push')
  , config = require('./config.js');;

sock.bindSync(config.socketAddress);
console.log('Producer bound to %s', config.socketAddress);

function getRandomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

var ids = [
  '"test"',
  '"hallo"',
  '"foo"',
  '"nexus5"'
];

var activities = [
  '"CYCLING"',
  '"RUNNING"',
  '"SITTING"',
  '"STAIRS"',
  '"WALKING"',
  '"UNKNOWN"'
]

function generateRandomMessage () {
  var id = ids[getRandomInt(0,3)];
  var act = activities[getRandomInt(0,5)];
  var ts = Date.now();
  return "ACT,"+ts+","+id+","+act
}

setInterval(function(){
  sock.send(generateRandomMessage());
}, 500);

// jshint esnext:true

var config = require('./config');
var app = require('koa')();

var Router = require('koa-router');
var mount = require('koa-mount');
var serve = require('koa-static');

app.use(serve('public'));
app.use(mount('/bower_components', serve('bower_components')));

app.use(require('koa-logger')());
app.use(require('koa-body')());

app.use(require('koa-views')('views', {
  default: 'jade',
  cache: false
}));

var pg = require ('koa-pg');
app.use(pg('pg://postgres:liveandgov@localhost:3333/liveandgov'));

// api routes (responds json)
var api = new Router();
require('./routes/api')(api);
app.use(mount('/api', api.middleware()));

// basic routes (renders template)
var routes = new Router();
require('./routes')(routes);
app.use(mount('/', routes.middleware()));

app.listen(3512);

var db = require('./database');
var csv = require('express-csv');

module.exports = function(app) {

	/* TRIP ROUTES */

	app.get('/trips.:format(csv)?', function(req, res, next) {
		db.index(function(err, data) {
			if (err) { res.send(err); console.error(err); return; }
			(req.params.format == "csv") ? res.csv(data) : res.json(data);
		});
	});

	app.get('/trips/:id([0-9]+).:format(csv)?', function(req, res, next) {
		db.show(req.params, req.query, function(err, data) {
			if (err) { res.send(err); console.error(err); return; }
			(req.params.format == "csv") ? res.csv(data) : res.json(data);
		});
	});

	app.put('/trips/:id([0-9]+).:format(csv)?', function(req, res, next) {
		db.update(req.params, req.body, function(err, data) {
			if (err) { res.send(err); console.error(err); return; }
			res.end(); // 205 Reset Content
		});
	});

	app.del('/trips/:id([0-9]+).:format(csv)?', function(req, res, next) {
		db.destroy(req.params, function(err, data) {
			if (err) { res.send(err); console.error(err); return; }
			res.end(); // 204 No Content
		});
	});

	/* SENSOR ROUTES */

	app.get('/trips/:id/:sensor(acc|har|goo|gps|lac|gra|tag).:format(csv)?', function(req, res, next) {
		db.show(req.params, req.query, function(err, data) {
			if (err) { res.send(err); console.error(err); return; }
			(req.params.format == "csv") ? res.csv(data) : res.json(data);
		});
	});

	app.get('/trips/:id/window/:sensor(acc|gra|lar).:format(csv)?', function(req, res, next) {
		db.window(req.params, req.query, function(err, data) {
			if (err) { res.send(err); console.error(err); return; }
			(req.params.format == "csv") ? res.csv(data) : res.json(data);
		});
	});

	/* DEVICE ROUTES (SEE WIKI) */

	// list all devices
	app.get('/devices.:format(csv)?', function(req, res, next) {
		db.devices(function(err, data) {
			if (err) { res.send(err); console.error(err); return; }
			(req.params.format == "csv") ? res.csv(data) : res.json(data);
		});
	});

	// show all records of a specific device
	app.get('/devices/:uuid.:format(csv)?', function(req, res, next) {
		db.show(req.params, req.query, function(err, data) {
			if (err) { res.send(err); console.error(err); return; }
			(req.params.format == "csv") ? res.csv(data) : res.json(data);
		});
	});

	// count records of a specific device
	app.get('/devices/:uuid/count.:format(csv)?', function(req, res, next) {
		db.count(req.params, function(err, data) {
			if (err) { res.send(err); console.error(err); return; }
			res.json(data);
		});
	});

	// show specific sensor data for a device
	app.get('/devices/:uuid/:sensor.:format(csv)?', function(req, res, next) {
		db.show(req.params, req.query, function(err, data) {
			if (err) { res.send(err); console.error(err); return; }
			(req.params.format == "csv") ? res.csv(data) : res.json(data);
		});
	});

	/* HTML (ANGULAR) */

	app.get('/partials/:name', function(req, res, next) {
		res.render('partials/' + req.params.name);
	});

	app.get('/', function(req, res, next) {
		res.render('index');
	});

//BOT
}

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

	app.get('/trips/:trip_id([0-9]+).:format(csv)?', function(req, res, next) {
		db.show(req.params, req.query, function(err, data) {
			if (err) { res.send(err); console.error(err); return; }
			(req.params.format == "csv") ? res.csv(data) : res.json(data);
		});
	});

	app.post('/trips/:trip_id([0-9]+).:format(csv)?', function(req, res, next) {
		db.update(req.params, req.body, function(err, data) {
			if (err) { res.send(err); console.error(err); return; }
			res.end(); // 205 Reset Content
		});
	});

	app.del('/trips/:trip_id([0-9]+).:format(csv)?', function(req, res, next) {
		db.destroy(req.params, function(err, data) {
			if (err) { res.send(err); console.error(err); return; }
			res.end(); // 204 No Content
		});
	});

	/* SENSOR ROUTES */

	app.get('/trips/:trip_id/:sensor(acc|har|goo|lac|gra|tag).:format(csv)?', function(req, res, next) {
		db.show(req.params, req.query, function(err, data) {
			if (err) { res.send(err); console.error(err); return; }
			(req.params.format == "csv") ? res.csv(data) : res.json(data);
		});
	});

	app.get('/trips/:trip_id/gps.:format(csv)?', function(req, res, next) {
		db.gps(req.params, req.query, function(err, data) {
			if (err) { res.send(err); console.error(err); return; }
			(req.params.format == "csv") ? res.csv(data) : res.json(data);
		});
	});

	app.get('/trips/:trip_id/:sensor(acc|lac|gra)/window.:format(csv)?', function(req, res, next) {
		db.window(req.params, req.query, function(err, data) {
			if (err) { res.send(err); console.error(err); return; }
			(req.params.format == "csv") ? res.csv(data) : res.json(data);
		});
	});

	// count sensor data of a specific trip
	app.get('/trips/:trip_id/:sensor(acc|lac|gra)/count.:format(csv)?', function(req, res, next) {
		db.count(req.params, function(err, data) {
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

	app.get('/partials/:name', function(req, res, next) {
		console.log('partials/' + req.params.name);
		res.render('partials/' + req.params.name);
	});

	app.get('/', function(req, res, next) {
		res.render('index');
	});

//BOT
}

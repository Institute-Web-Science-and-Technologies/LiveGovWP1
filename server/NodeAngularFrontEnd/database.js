var pg = require("pg");

// FIXME what a mess!

module.exports = {
	query: function(statement, callback) {
		pg.connect("pg://postgres:liveandgov@localhost/liveandgov", function(err, client, done) {
			if (err) { console.error(err); return; }
			client.query(statement, function(err, result) {
				done();
				if (err) { console.error(err); return; }
				console.log('\x1b[35m', JSON.stringify(statement, null, 2), '\x1b[0m'); // magenta
				callback(err, result.rows);
			});
		});
	},

	index: function(callback) {
		var text = "SELECT * FROM trip ORDER BY trip_id DESC";
		var statement = {
			name: "getAllTrips",
			text: text
		};

		this.query(statement, function(err, result) {
			if (err) { console.error(err); return; }
			callback(null, result);
		});
	},

	show: function(params, query, callback) {
		var statement;

		if (params.uuid !== undefined) {
			statement = {
				name: "getOneTripByUserId",
				text: "SELECT * FROM trip WHERE user_id = $1",
				values: [params.uuid]
			};
		} else if (params.sensor !== undefined) {
			switch (params.sensor) {
				case "acc": sensor = "sensor_accelerometer"; break;
				case "har": sensor = "har_annotation"; break;
				case "goo": sensor = "sensor_google_activity"; break;
				case "gps": sensor = "sensor_gps"; break;
				case "lac": sensor = "sensor_linear_acceleration"; break;
				case "gra": sensor = "sensor_gravity"; break;
				case "tag": sensor = "sensor_tags"; break;
			}
			statement = {
				name: "getSensor",
				text: "SELECT * FROM " + sensor + " WHERE trip_id = $1",
				// text: "SELECT ts, x, y, z FROM " + sensor + " WHERE trip_id = $1",
				values: [params.trip_id]
			};
		} else {
			statement = {
				name: "getOneTripByTripId",
				text: "SELECT * FROM trip WHERE trip_id = $1",
				values: [params.trip_id]
			};
		}

		this.query(statement, function(err, result) {
			if (err) { console.error(err); return; }
			callback(null, result);
		});
	},

	gps: function(params, body, callback) {
		var statement = {
			name: "getGps",
			text: "SELECT ts, ST_AsGeoJSON(lonlat)::json AS lonlat from sensor_gps WHERE trip_id = $1",
			values: [params.trip_id]
		};

		this.query(statement, function(err, result) {
			if (err) { console.error(err); return; }
			callback(null, result);
		});
	},

	update: function(params, body, callback) {
		var statement = {
			name: "updateTrip",
			text: "UPDATE trip SET name = $2 WHERE trip_id = $1",
			values: [params.trip_id, body.name]
		};

		this.query(statement, function(err, result) {
			if (err) { console.error(err); return; }
			callback(null, result);
		});
	},

	destroy: function(params, callback) {
		var statement = {
			name: "deleteTrip",
			text: "DELETE FROM trip WHERE trip_id = $1",
			values: [params.trip_id]
		};

		this.query(statement, function(err, result) {
			if (err) { console.error(err); return; }
			callback(null, result);
		});
	},

	window: function(params, query, callback) {
		switch(params.sensor) {
			case "acc": sensor = "sensor_accelerometer"; break;
			case "lac": sensor = "sensor_linear_acceleration"; break;
			case "gra": sensor = "sensor_gravity"; break;
		}

		var text = "SELECT avg(x) AS avgX, min(x) AS minX, max(x) AS maxX, avg(y) AS avgY, min(y) AS minY, max(y) AS maxY, avg(z) AS avgZ, min(z) AS minZ, max(z) AS maxZ, min(ts) AS startTime, max(ts) AS endTime FROM (SELECT x, y, z, ts, NTILE($2) OVER (ORDER BY ts) AS w FROM " + sensor + " WHERE trip_id = $1{{LIMIT_TIME}}) A GROUP BY w ORDER BY w;"; // FIXME

		if (query.window === undefined) { query.window = 200; } // 966 =~ 1024kb, 483 =~ 1024kb/3
		var values = [params.trip_id, query.window];

		var limitTime = "";
		if (query.startTime && !query.endTime) {
			limitTime = "AND ts >= $3";
			values.push(query.startTime);
		} else if (!query.startTime && query.endTime) {
			limitTime = "AND ts <= $3";
			values.push(query.endTime);
		} else if (query.startTime && query.endTime) {
			limitTime = "AND ts >= $3 AND ts <= $4";
			values.push(query.startTime);
			values.push(query.endTime);
		}

		var statement = {
			name: "getSensorWindow",
			text: text.replace("{{LIMIT_TIME}}", " " + limitTime),
			values: values
		};

		this.query(statement, function(err, result) {
			if (err) { console.error(err); return; }
			callback(null, result);
		});
	},

	// CHANGEME: abstract? utilize index route?
	devices: function(callback) {
		var statement = {
			text: "SELECT DISTINCT (user_id) user_id from trip ORDER BY user_id DESC"
		};

		this.query(statement, function(err, result) {
			if (err) { console.error(err); return; }
			callback(null, result);
		});
	},

	count: function(params, callback) {
		var statement;
		if (params.sensor !== undefined) {
			switch(params.sensor) {
				case "acc": sensor = "sensor_accelerometer"; break;
				case "lac": sensor = "sensor_linear_acceleration"; break;
				case "gra": sensor = "sensor_gravity"; break;
			}
			statement = {
				text: "SELECT COUNT(ts) FROM " + sensor + " WHERE trip_id = $1",
				values: [params.trip_id]
			};
		} else {
			statement = {
				text: "SELECT COUNT(*) FROM trip WHERE user_id = $1",
				values: [params.uuid]
			};
		}

		this.query(statement, function(err, result) {
			if (err) { console.error(err); return; }
			callback(null, result);
		});
	}

// BOT
};

'use strict';

/* FILTERS */

// {{ trip.start_ts | duration:trip.stop_ts | date:'HH:mm:ss' }}

app.filter('duration', function() {
	return function(start_ts, stop_ts) {
		// dirty fix for gmt+1: duration minus 1h
		return stop_ts - start_ts - 3600000;
	}
});

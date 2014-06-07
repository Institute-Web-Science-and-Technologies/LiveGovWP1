/* jshint -W097 */
'use strict';

/* global app:true */

/* FILTERS */

// FIXME DURATION FILTER IS EXTREMELY SLOW!

// http://stackoverflow.com/a/17971632/220472
// The main problem with the filter approach is that upon each change the dom is
// manipulated, so it's not the filter that's slow but the consequences. An
// alternative is to use something like
//
// ng-show="([item] | filter:searchFilter).length > 0"
//
// on the repeated element.

// calculate duration between two timestamps
// {{ trip.start_ts | duration:trip.stop_ts | date:'HH:mm:ss' }}
app.filter('duration', function () {
  // var t = new Date();
  return function (start_ts, stop_ts) {
    // console.info("duration filter took " + ((new Date() - t) / 1000) + " ms");
    return stop_ts - start_ts; // - 3600000; // dirty fix for gmt+1: duration minus 1h
  };
});

	};
});

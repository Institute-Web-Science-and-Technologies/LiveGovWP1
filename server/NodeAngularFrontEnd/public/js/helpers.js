(function() {
  'use strict';

  /* GENERAL */

  // flatten an array
  Array.prototype.flatten = function() {
    return [].concat.apply([], this);
  };

  // return array of property values, e.g. [[objs],...] -> [props]
  Array.prototype.select = function(properties) {
    return this.flatten().map(function(d) {
      return properties.map(function(p) {
        return d[p];
      });
    }).flatten();
  };

  // return the most occurring element of an array
  // NOTE: returns latest touched element if there are multiple top counts
  Array.prototype.most = function() {
    var that = this;
    return this.slice().sort(function(a, b) {
        return that.filter(function(v){ return v === a }).length -
               that.filter(function(v){ return v === b }).length;
    }).pop();
  };

  // FIXME abstract
  // calculate the most popular tag between t0 and t1
  function topActivity(har, t0, t1) {
    return getMaxOccurrence(har.map(function (d) {
      if (d.ts >= t0 && d.ts <= t1) { // get tags between t0 and t1
        return d.tag.replace(/\"/g, ""); // remove quotes
      }}).filter(function (d) { return d; }) // remove undefined
    );
  }

  /* SENSOR DATA */

  // merge two sensor data arrays, sorted w/o duplicates
  Array.prototype.merge = function(array) {
    if (!this.length) return array;
    if (!array.length) return this;

    // sort by timestamp first, then remove the ones where a[i+1].endtime is
    // bigger than a[i].endtime

    return this.concat(array)
      .sort(function(a,b) {
        return d3.ascending(a.ts, b.ts);
      })
      .filter(function(d,i,a) { // true returns d
        return (a[i+1] ? (a[i].endtime <= a[i+1].endtime) : true);
      })
      .filter(function(d,i,a) { // FIXME there are still remaining entries after the first filter run
        return (a[i+1] ? (a[i].endtime <= a[i+1].endtime) : true);
      });
  };

  /* D3 */

  Array.prototype.extent = function(a) {
    return d3.extent(this.select(a)); // e.g. ['avgx, avgy, avgz']
  };


}())
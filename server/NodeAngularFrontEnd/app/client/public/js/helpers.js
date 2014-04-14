(function() {
  'use strict';

  // flatten an array
  Array.prototype.flatten = function() {
    return [].concat.apply([], this)
  }

  // return array of property values, e.g. [[objs],...] -> [props]
  Array.prototype.select = function(properties) {
    return this.flatten().map(function(d) {
      return properties.map(function(p) {
        return d[p];
      })
    }).flatten();
  }

  // merge two sensor data arrays, sorted w/o duplicates
  Array.prototype.merge = function(array) {
    if (!this.length) return array;
    if (!array.length) return this;

    return this.concat(array)
      .sort(function(a,b) {
        return d3.ascending(a.starttime, b.starttime);
      })
      .filter(function(d,i,a) { // true returns d
        return (a[i+1] ? (a[i].endtime <= a[i+1].endtime) : true);
      })
      .filter(function(d,i,a) { // FIXME
        return (a[i+1] ? (a[i].endtime <= a[i+1].endtime) : true);
      })
  }

  // get array element which occures the most
  function getMaxOccurrence(array) {
    if (!array.length) return null;
    var len = array.length;
    var modeMap = {};
    var maxEl = array[0];
    var maxCount = 1;
    for (var i = 0; i < len; i++) {
      var el = array[i];
      if (modeMap[el] === null) modeMap[el] = 1;
      else modeMap[el]++;
      if (modeMap[el] > maxCount) {
        maxEl = el;
        maxCount = modeMap[el];
      }
    }
    return maxEl;
  }

  // FIXME abstract
  // calculate the most popular tag between t0 and t1
  function topActivity(har, t0, t1) {
    return getMaxOccurrence(har.map(function (d) {
      if (d.ts >= t0 && d.ts <= t1) { // get tags between t0 and t1
        return d.tag.replace(/\"/g, ""); // remove quotes
      }}).filter(function (d) { return d; }) // remove undefined
    );
  }

}())
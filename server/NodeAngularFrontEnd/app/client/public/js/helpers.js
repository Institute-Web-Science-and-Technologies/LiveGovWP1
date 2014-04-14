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

  // insert-merge two arrays of objects by property, sorted w/o duplicates
  Array.prototype.insert = function(array, property) {

    if (!this.length) return array;
    if (!array.length) return this;

    // returns index where to insert the array, e.g. ([1,2,3], [2,3]) -> 1
    var len = this.length;
    function index() {
      for (var i = 0; i < len; i++) {
        if (this[i][property] >= array[0][property]) return i;
      }
    };

    // returns fields to replace with array
    function range() {
      for (var i = len - 1; i >= 0; --i) {
        if (this[i][property] <= array[array.length - 1][property]) {
          var idx = index.apply(this);
          return [idx, i - idx + 1]; // [ from-here, n-fields ]
        }
      }
    }

    Array.prototype.splice.apply(this, range.apply(this).concat(array));

    if (this.length < array.length) console.warn('this.length < array.length', this.length, array.length)
    if (this.length < len) console.warn('this.length < len', this.length, len)
    if (this.length == len) console.warn('this.length == len', this.length, len)

    return this;
  }

}())
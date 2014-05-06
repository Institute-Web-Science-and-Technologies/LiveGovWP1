/* jshint strict:true, devel:true, debug:true */
/* globals d3 */
'use strict'; // jshint -W097

if (!d3.custom) d3.custom = {};

d3.custom.map = function module() {

  var dispatch = d3.dispatch(); // exposed functions

  function exports(selection) {
    selection.each(function(d, i) {
    });
  }

  d3.rebind(exports, dispatch, 'on');
  return exports;
};
/* global angular:true */
'use strict'; /* jshint -W097 */

/* MODULES */

var app = angular.module('inspectionFrontEnd', [
  'ngRoute',
  'ngResource',
  'xeditable',
  ]);

app.run(function (editableOptions, editableThemes) {
  editableThemes.bs3.inputClass = 'input-sm';
  editableThemes.bs3.buttonsClass = 'btn-sm';
  editableOptions.theme = 'bs3';
});

/* jshint strict:true, devel:true, debug:true */
/* global angular */
'use strict'; // jshint -W097

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

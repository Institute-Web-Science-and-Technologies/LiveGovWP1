'use strict';

var app = angular.module('inspectionFrontEnd', [
  'ngRoute',
  'ngResource',
  'xeditable',
  ]);

// FIXME
// app.config(function ($provide) {
//   $provide.value('sensors', ['acc', 'gra', 'lac']); // data sensors
//   $provide.value('windowSize', 200); // default postgres window size for data loading
// });

app.run(function (editableOptions, editableThemes) {
  editableThemes.bs3.inputClass = 'input-sm';
  editableThemes.bs3.buttonsClass = 'btn-sm';
  editableOptions.theme = 'bs3';
});

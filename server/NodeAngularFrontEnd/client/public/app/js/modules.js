'use strict';

/* MODULES */

var app = angular.module('nodeInspectionFrontEnd', [
	'ngRoute',
	'ngResource',
	// 'ngSanitize', // for the rec-name-popover
	// 'mgcrea.ngStrap.navbar', // replace bootstrap navbar, just because
	// 'mgcrea.ngStrap.aside', // change trip without returning to record table
	// 'mgcrea.ngStrap.popover', // record table -> editable name-colum
	// 'mgcrea.ngStrap.tooltip', // on disabled navbar elements, when there's no trip selected (and required by popover)
	'mgcrea.ngStrap.navbar',
	'mgcrea.ngStrap.modal',
	// 'mgcrea.ngMotion.fade',
	'xeditable'
	]);

app.run(function(editableOptions, editableThemes) {
  editableThemes.bs3.inputClass = 'input-sm';
  editableThemes.bs3.buttonsClass = 'btn-sm';
  editableOptions.theme = 'bs3';
});

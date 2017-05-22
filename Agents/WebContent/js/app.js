'use strict';
var app=angular.module('app',['ui.router','ngMaterial']);

app.config(function($stateProvider, $urlRouterProvider) {
	
	$urlRouterProvider.otherwise('/init');
	
	
	$stateProvider.state('init', {
		url : '/init',
	//	templateUrl : 'index.html',
		controller : 'ctrl'
	})
	
});
// App components
angular.module('webapp.controllers', []);

// The app
var webapp = angular.module('webapp',
    ['ionic', 'webapp.controllers']);

// Run
webapp.run(
    ['$log', '$ionicPlatform',
    function($log, $ionicPlatform) {

    $ionicPlatform.ready(function() {
        $log.debug('Ionic platform is ready');
        $log.debug('App build '
            + window.webappConstants.projectBuild + ' ('
            + window.webappConstants.projectStage + ')');
    });

}]);

// Config
webapp.config(
    ['$stateProvider', '$urlRouterProvider', '$logProvider',
    function($stateProvider, $urlRouterProvider, $logProvider) {

    // This only affects $log.debug() messages.
    var isDebugEnabled = (window.webappConstants.projectStage === 'development');
    $logProvider.debugEnabled(isDebugEnabled);

    // States
    $stateProvider

        .state('main', {
            url: '/main',
            templateUrl: '/static/partials/main.html',
            controller: 'MainCtrl'
        });

    // Default
    $urlRouterProvider.otherwise('/main');

}]);

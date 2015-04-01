var controllers = angular.module('webapp.controllers');

controllers.controller('MainCtrl', [
    '$scope', '$log',
    function($scope, $log) {

    'use strict';

    $scope.start = function() {
        $log.debug('MainCtrl');
    };

    $scope.start();

}]);

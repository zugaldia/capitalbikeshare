var controllers = angular.module('webapp.controllers');

controllers.controller('VoidCtrl', [
    '$scope', '$log',
    function($scope, $log) {

    'use strict';

    $scope.start = function() {
        $log.debug('VoidCtrl');
    };

    $scope.start();

}]);

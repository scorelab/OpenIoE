(function() {
    'use strict';

    angular
        .module('ioeApp')
        .controller('SensorDataDetailController', SensorDataDetailController);

    SensorDataDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'SensorData'];

    function SensorDataDetailController($scope, $rootScope, $stateParams, entity, SensorData) {
        var vm = this;

        vm.sensorData = entity;

        var unsubscribe = $rootScope.$on('ioeApp:sensorDataUpdate', function(event, result) {
            vm.sensorData = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

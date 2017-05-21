(function() {
    'use strict';

    angular
        .module('ioeApp')
        .controller('SensorDetailController', SensorDetailController);

    SensorDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Sensor', 'Device', 'Subscription', 'User', 'SensorData'];

    function SensorDetailController($scope, $rootScope, $stateParams, entity, Sensor, Device, Subscription, User, SensorData) {
        var vm = this;

        vm.sensor = entity;

        var unsubscribe = $rootScope.$on('ioeApp:sensorUpdate', function(event, result) {
            vm.sensor = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

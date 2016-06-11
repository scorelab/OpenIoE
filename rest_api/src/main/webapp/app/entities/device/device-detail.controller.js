(function() {
    'use strict';

    angular
        .module('ioeApp')
        .controller('DeviceDetailController', DeviceDetailController);

    DeviceDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Device', 'Sensor', 'Subscription', 'User'];

    function DeviceDetailController($scope, $rootScope, $stateParams, entity, Device, Sensor, Subscription, User) {
        var vm = this;

        vm.device = entity;

        var unsubscribe = $rootScope.$on('ioeApp:deviceUpdate', function(event, result) {
            vm.device = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

(function() {
    'use strict';

    angular
        .module('ioeApp')
        .controller('SensorDialogController', SensorDialogController);

    SensorDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Sensor', 'Device', 'Subscription', 'User'];

    function SensorDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Sensor, Device, Subscription, User) {
        var vm = this;

        vm.sensor = entity;
        vm.clear = clear;
        vm.save = save;
        vm.devices = Device.query();
        vm.subscriptions = Subscription.query();
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.sensor.id !== null) {
                Sensor.update(vm.sensor, onSaveSuccess, onSaveError);
            } else {
                Sensor.save(vm.sensor, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('ioeApp:sensorUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();

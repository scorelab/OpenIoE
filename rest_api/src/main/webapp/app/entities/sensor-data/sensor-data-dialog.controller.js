(function() {
    'use strict';

    angular
        .module('ioeApp')
        .controller('SensorDataDialogController', SensorDataDialogController);

    SensorDataDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'SensorData'];

    function SensorDataDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, SensorData) {
        var vm = this;

        vm.sensorData = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.sensorData.id !== null) {
                SensorData.update(vm.sensorData, onSaveSuccess, onSaveError);
            } else {
                SensorData.save(vm.sensorData, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('ioeApp:sensorDataUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.timestamp = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();

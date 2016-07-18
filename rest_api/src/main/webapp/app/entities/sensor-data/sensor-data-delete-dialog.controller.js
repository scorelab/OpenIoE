(function() {
    'use strict';

    angular
        .module('ioeApp')
        .controller('SensorDataDeleteController',SensorDataDeleteController);

    SensorDataDeleteController.$inject = ['$uibModalInstance', 'entity', 'SensorData'];

    function SensorDataDeleteController($uibModalInstance, entity, SensorData) {
        var vm = this;

        vm.sensorData = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            SensorData.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

(function() {
    'use strict';

    angular
        .module('ioeApp')
        .controller('SensorDeleteController',SensorDeleteController);

    SensorDeleteController.$inject = ['$uibModalInstance', 'entity', 'Sensor'];

    function SensorDeleteController($uibModalInstance, entity, Sensor) {
        var vm = this;

        vm.sensor = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Sensor.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

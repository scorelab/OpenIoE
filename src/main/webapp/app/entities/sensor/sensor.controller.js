(function() {
    'use strict';

    angular
        .module('ioeApp')
        .controller('SensorController', SensorController);

    SensorController.$inject = ['$scope', '$state', 'Sensor'];

    function SensorController ($scope, $state, Sensor) {
        var vm = this;
        
        vm.sensors = [];

        loadAll();

        function loadAll() {
            Sensor.query(function(result) {
                vm.sensors = result;
            });
        }
    }
})();

(function() {
    'use strict';

    angular
        .module('ioeApp')
        .controller('SensorDataController', SensorDataController);

    SensorDataController.$inject = ['$scope', '$state', 'SensorData'];

    function SensorDataController ($scope, $state, SensorData) {
        var vm = this;
        
        vm.sensorData = [];

        loadAll();

        function loadAll() {
            SensorData.query(function(result) {
                vm.sensorData = result;
            });
        }
    }
})();

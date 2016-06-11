(function() {
    'use strict';

    angular
        .module('ioeApp')
        .controller('DeviceController', DeviceController);

    DeviceController.$inject = ['$scope', '$state', 'Device'];

    function DeviceController ($scope, $state, Device) {
        var vm = this;
        
        vm.devices = [];

        loadAll();

        function loadAll() {
            Device.query(function(result) {
                vm.devices = result;
            });
        }
    }
})();

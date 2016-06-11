(function() {
    'use strict';

    angular
        .module('ioeApp')
        .controller('SubscriptionDialogController', SubscriptionDialogController);

    SubscriptionDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Subscription', 'Device', 'Sensor', 'User'];

    function SubscriptionDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Subscription, Device, Sensor, User) {
        var vm = this;

        vm.subscription = entity;
        vm.clear = clear;
        vm.save = save;
        vm.devices = Device.query();
        vm.sensors = Sensor.query();
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.subscription.id !== null) {
                Subscription.update(vm.subscription, onSaveSuccess, onSaveError);
            } else {
                Subscription.save(vm.subscription, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('ioeApp:subscriptionUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();

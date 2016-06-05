(function() {
    'use strict';

    angular
        .module('ioeApp')
        .controller('SubscriptionDeleteController',SubscriptionDeleteController);

    SubscriptionDeleteController.$inject = ['$uibModalInstance', 'entity', 'Subscription'];

    function SubscriptionDeleteController($uibModalInstance, entity, Subscription) {
        var vm = this;

        vm.subscription = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Subscription.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

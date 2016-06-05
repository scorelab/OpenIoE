(function() {
    'use strict';

    angular
        .module('ioeApp')
        .controller('SubscriptionController', SubscriptionController);

    SubscriptionController.$inject = ['$scope', '$state', 'Subscription'];

    function SubscriptionController ($scope, $state, Subscription) {
        var vm = this;
        
        vm.subscriptions = [];

        loadAll();

        function loadAll() {
            Subscription.query(function(result) {
                vm.subscriptions = result;
            });
        }
    }
})();

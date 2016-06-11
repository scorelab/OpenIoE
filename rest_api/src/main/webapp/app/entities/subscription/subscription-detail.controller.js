(function() {
    'use strict';

    angular
        .module('ioeApp')
        .controller('SubscriptionDetailController', SubscriptionDetailController);

    SubscriptionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Subscription', 'Device', 'Sensor', 'User'];

    function SubscriptionDetailController($scope, $rootScope, $stateParams, entity, Subscription, Device, Sensor, User) {
        var vm = this;

        vm.subscription = entity;

        var unsubscribe = $rootScope.$on('ioeApp:subscriptionUpdate', function(event, result) {
            vm.subscription = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

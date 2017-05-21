(function() {
    'use strict';

    angular
        .module('ioeApp')
        .controller('PublicationDetailController', PublicationDetailController);

    PublicationDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Publication', 'Device', 'Sensor', 'User'];

    function PublicationDetailController($scope, $rootScope, $stateParams, entity, Publication, Device, Sensor, User) {
        var vm = this;

        vm.publication = entity;

        var unsubscribe = $rootScope.$on('ioeApp:publicationUpdate', function(event, result) {
            vm.publication = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

(function() {
    'use strict';

    angular
        .module('ioeApp')
        .controller('PublicationController', PublicationController);

    PublicationController.$inject = ['$scope', '$state', 'Publication'];

    function PublicationController ($scope, $state, Publication) {
        var vm = this;
        
        vm.publications = [];

        loadAll();

        function loadAll() {
            Publication.query(function(result) {
                vm.publications = result;
            });
        }
    }
})();

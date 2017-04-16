(function() {
    'use strict';

    angular
        .module('ioeApp')
        .controller('PublicationDeleteController',PublicationDeleteController);

    PublicationDeleteController.$inject = ['$uibModalInstance', 'entity', 'Publication'];

    function PublicationDeleteController($uibModalInstance, entity, Publication) {
        var vm = this;

        vm.publication = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Publication.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

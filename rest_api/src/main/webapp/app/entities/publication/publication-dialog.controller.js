(function() {
    'use strict';

    angular
        .module('ioeApp')
        .controller('PublicationDialogController', PublicationDialogController);

    PublicationDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Publication', 'Device', 'Sensor', 'User'];

    function PublicationDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Publication, Device, Sensor, User) {
        var vm = this;

        vm.publication = entity;
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
            if (vm.publication.id !== null) {
                Publication.update(vm.publication, onSaveSuccess, onSaveError);
            } else {
                Publication.save(vm.publication, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('ioeApp:publicationUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();

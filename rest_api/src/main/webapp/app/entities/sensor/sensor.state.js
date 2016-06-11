(function() {
    'use strict';

    angular
        .module('ioeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('sensor', {
            parent: 'entity',
            url: '/sensor',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ioeApp.sensor.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/sensor/sensors.html',
                    controller: 'SensorController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('sensor');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('sensor-detail', {
            parent: 'entity',
            url: '/sensor/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ioeApp.sensor.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/sensor/sensor-detail.html',
                    controller: 'SensorDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('sensor');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Sensor', function($stateParams, Sensor) {
                    return Sensor.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('sensor.new', {
            parent: 'sensor',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sensor/sensor-dialog.html',
                    controller: 'SensorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                sensorId: null,
                                name: null,
                                description: null,
                                type: null,
                                serial: null,
                                storeType: null,
                                tableId: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('sensor', null, { reload: true });
                }, function() {
                    $state.go('sensor');
                });
            }]
        })
        .state('sensor.edit', {
            parent: 'sensor',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sensor/sensor-dialog.html',
                    controller: 'SensorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Sensor', function(Sensor) {
                            return Sensor.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('sensor', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('sensor.delete', {
            parent: 'sensor',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sensor/sensor-delete-dialog.html',
                    controller: 'SensorDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Sensor', function(Sensor) {
                            return Sensor.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('sensor', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

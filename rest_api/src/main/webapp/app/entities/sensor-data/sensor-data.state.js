(function() {
    'use strict';

    angular
        .module('ioeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('sensor-data', {
            parent: 'entity',
            url: '/sensor-data',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ioeApp.sensorData.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/sensor-data/sensor-data.html',
                    controller: 'SensorDataController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('sensorData');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('sensor-data-detail', {
            parent: 'entity',
            url: '/sensor-data/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ioeApp.sensorData.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/sensor-data/sensor-data-detail.html',
                    controller: 'SensorDataDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('sensorData');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'SensorData', function($stateParams, SensorData) {
                    return SensorData.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('sensor-data.new', {
            parent: 'sensor-data',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sensor-data/sensor-data-dialog.html',
                    controller: 'SensorDataDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                data: null,
                                description: null,
                                timestamp: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('sensor-data', null, { reload: true });
                }, function() {
                    $state.go('sensor-data');
                });
            }]
        })
        .state('sensor-data.edit', {
            parent: 'sensor-data',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sensor-data/sensor-data-dialog.html',
                    controller: 'SensorDataDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['SensorData', function(SensorData) {
                            return SensorData.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('sensor-data', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('sensor-data.delete', {
            parent: 'sensor-data',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sensor-data/sensor-data-delete-dialog.html',
                    controller: 'SensorDataDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['SensorData', function(SensorData) {
                            return SensorData.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('sensor-data', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

(function() {
    'use strict';

    angular
        .module('ioeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('publication', {
            parent: 'entity',
            url: '/publication',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ioeApp.publication.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/publication/publications.html',
                    controller: 'PublicationController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('publication');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('publication-detail', {
            parent: 'entity',
            url: '/publication/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ioeApp.publication.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/publication/publication-detail.html',
                    controller: 'PublicationDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('publication');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Publication', function($stateParams, Publication) {
                    return Publication.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('publication.new', {
            parent: 'publication',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/publication/publication-dialog.html',
                    controller: 'PublicationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                publicationId: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('publication', null, { reload: true });
                }, function() {
                    $state.go('publication');
                });
            }]
        })
        .state('publication.edit', {
            parent: 'publication',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/publication/publication-dialog.html',
                    controller: 'PublicationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Publication', function(Publication) {
                            return Publication.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('publication', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('publication.delete', {
            parent: 'publication',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/publication/publication-delete-dialog.html',
                    controller: 'PublicationDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Publication', function(Publication) {
                            return Publication.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('publication', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

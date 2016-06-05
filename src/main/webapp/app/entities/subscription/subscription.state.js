(function() {
    'use strict';

    angular
        .module('ioeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('subscription', {
            parent: 'entity',
            url: '/subscription',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ioeApp.subscription.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/subscription/subscriptions.html',
                    controller: 'SubscriptionController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('subscription');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('subscription-detail', {
            parent: 'entity',
            url: '/subscription/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ioeApp.subscription.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/subscription/subscription-detail.html',
                    controller: 'SubscriptionDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('subscription');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Subscription', function($stateParams, Subscription) {
                    return Subscription.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('subscription.new', {
            parent: 'subscription',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/subscription/subscription-dialog.html',
                    controller: 'SubscriptionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                subscriptionId: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('subscription', null, { reload: true });
                }, function() {
                    $state.go('subscription');
                });
            }]
        })
        .state('subscription.edit', {
            parent: 'subscription',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/subscription/subscription-dialog.html',
                    controller: 'SubscriptionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Subscription', function(Subscription) {
                            return Subscription.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('subscription', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('subscription.delete', {
            parent: 'subscription',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/subscription/subscription-delete-dialog.html',
                    controller: 'SubscriptionDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Subscription', function(Subscription) {
                            return Subscription.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('subscription', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

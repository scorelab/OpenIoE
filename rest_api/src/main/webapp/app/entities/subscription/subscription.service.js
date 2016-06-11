(function() {
    'use strict';
    angular
        .module('ioeApp')
        .factory('Subscription', Subscription);

    Subscription.$inject = ['$resource'];

    function Subscription ($resource) {
        var resourceUrl =  'api/subscriptions/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();

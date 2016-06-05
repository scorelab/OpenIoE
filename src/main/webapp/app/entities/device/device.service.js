(function() {
    'use strict';
    angular
        .module('ioeApp')
        .factory('Device', Device);

    Device.$inject = ['$resource'];

    function Device ($resource) {
        var resourceUrl =  'api/devices/:id';

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

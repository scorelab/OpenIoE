(function() {
    'use strict';
    angular
        .module('ioeApp')
        .factory('Sensor', Sensor);

    Sensor.$inject = ['$resource'];

    function Sensor ($resource) {
        var resourceUrl =  'api/sensors/:id';

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

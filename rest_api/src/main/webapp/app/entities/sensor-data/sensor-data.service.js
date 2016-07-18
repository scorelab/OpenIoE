(function() {
    'use strict';
    angular
        .module('ioeApp')
        .factory('SensorData', SensorData);

    SensorData.$inject = ['$resource', 'DateUtils'];

    function SensorData ($resource, DateUtils) {
        var resourceUrl =  'api/sensor-data/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.timestamp = DateUtils.convertDateTimeFromServer(data.timestamp);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();

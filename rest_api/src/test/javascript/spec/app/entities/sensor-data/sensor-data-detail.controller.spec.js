'use strict';

describe('Controller Tests', function() {

    describe('SensorData Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockSensorData;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockSensorData = jasmine.createSpy('MockSensorData');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'SensorData': MockSensorData
            };
            createController = function() {
                $injector.get('$controller')("SensorDataDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'ioeApp:sensorDataUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

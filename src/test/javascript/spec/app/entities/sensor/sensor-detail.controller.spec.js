'use strict';

describe('Controller Tests', function() {

    describe('Sensor Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockSensor, MockDevice, MockSubscription, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockSensor = jasmine.createSpy('MockSensor');
            MockDevice = jasmine.createSpy('MockDevice');
            MockSubscription = jasmine.createSpy('MockSubscription');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Sensor': MockSensor,
                'Device': MockDevice,
                'Subscription': MockSubscription,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("SensorDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'ioeApp:sensorUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

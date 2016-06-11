'use strict';

describe('Controller Tests', function() {

    describe('Device Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockDevice, MockSensor, MockSubscription, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockDevice = jasmine.createSpy('MockDevice');
            MockSensor = jasmine.createSpy('MockSensor');
            MockSubscription = jasmine.createSpy('MockSubscription');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Device': MockDevice,
                'Sensor': MockSensor,
                'Subscription': MockSubscription,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("DeviceDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'ioeApp:deviceUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

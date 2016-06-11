'use strict';

describe('Controller Tests', function() {

    describe('Subscription Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockSubscription, MockDevice, MockSensor, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockSubscription = jasmine.createSpy('MockSubscription');
            MockDevice = jasmine.createSpy('MockDevice');
            MockSensor = jasmine.createSpy('MockSensor');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Subscription': MockSubscription,
                'Device': MockDevice,
                'Sensor': MockSensor,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("SubscriptionDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'ioeApp:subscriptionUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

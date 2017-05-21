'use strict';

describe('Controller Tests', function() {

    describe('Publication Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPublication, MockDevice, MockSensor, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPublication = jasmine.createSpy('MockPublication');
            MockDevice = jasmine.createSpy('MockDevice');
            MockSensor = jasmine.createSpy('MockSensor');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Publication': MockPublication,
                'Device': MockDevice,
                'Sensor': MockSensor,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("PublicationDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'ioeApp:publicationUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

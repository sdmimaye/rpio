angular.module('rpio').config(function ($routeProvider) {
    $routeProvider.when('/gpio/edit/:id', {
        templateUrl: 'app/gpio/edit-gpio.html',
        controller: 'EditGpioCtrl',
        resolve:{
            model: function (gpio, $route) {
                return gpio.getById($route.current.params.id).then(function (res) {
                    return res.data;
                });
            },
            pins: function (gpio) {
                return gpio.getPinList().then(function (res) {
                    return res.data;
                });
            }
        }
    });
});

angular.module("rpio").controller('EditGpioCtrl', function($scope, $location, pins, gpio, model, message, error) {
    $scope.model = {
        gpio: model
    };

    $scope.view = {
        getCapablePins: function () {
            var search = "DIGITAL_" + $scope.model.gpio.mode;
            return pins.filter(function (pin) {
                return pin.supportedPinModes.some(function (mode) {
                    return mode === search;
                });
            });
        },
        submit: function(){
            gpio.update($scope.model.gpio).then(function(){
                message.info($scope.loc.gpio.edit.messages.success);
                $location.path("/overview");
            }, function(res){
                error.show("gpio.general.messages", "gpio.edit.messages", res);
            });
        }
    };
});
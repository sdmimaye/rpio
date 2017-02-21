angular.module('rpio').config(function ($routeProvider) {
    $routeProvider.when('/gpio/add', {
        templateUrl: 'app/gpio/add-gpio.html',
        controller: 'AddGpioCtrl',
        resolve: {
            pins: function (gpio) {
                return gpio.getPinList().then(function (res) {
                    return res.data;
                });
            }
        }
    });
});

angular.module("rpio").controller('AddGpioCtrl', function ($scope, $location, pins, gpio, message, error) {
    $scope.model = {
        gpio: {
            mode: "INPUT",
            ouputMode: "TOGGLE"
        }
    };

    console.log("Pins: ", pins);
    $scope.view = {
        getCapablePins: function () {
            var search = "DIGITAL_" + $scope.model.gpio.mode;
            return pins.filter(function (pin) {
                return pin.supportedPinModes.some(function (mode) {
                    return mode === search;
                });
            });
        },
        submit: function () {
            gpio.insert($scope.model.gpio).then(function () {
                message.info($scope.loc.gpio.add.messages.success);
                $location.path("/overview");
            }, function (res) {
                error.show("gpio.general.messages", "gpio.add.messages", res);
            });
        }
    }
    ;
});
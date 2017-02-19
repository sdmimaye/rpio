angular.module('rpio').config(function ($routeProvider) {
    $routeProvider.when('/gpio/add', {
        templateUrl: 'app/gpio/add-gpio.html',
        controller: 'AddGpioCtrl',
        resolve:{

        }
    });
});

angular.module("rpio").controller('AddGpioCtrl', function($scope) {

});
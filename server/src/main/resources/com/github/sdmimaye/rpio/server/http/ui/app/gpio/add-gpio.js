angular.module('rpio').config(function ($routeProvider) {
    $routeProvider.when('/gpio/add', {
        templateUrl: 'app/gpio/add-gpio.html',
        controller: 'AddGpioCtrl',
        resolve:{

        }
    });
});

angular.module("rpio").controller('AddGpioCtrl', function($scope, $location, gpio, message, error) {
    $scope.model = {
        gpio:{
            mode: "INPUT",
            ouputMode: "TOGGLE"
        }
    };

    $scope.view = {
        numbers:[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20],
        submit: function(){
            gpio.insert($scope.model.gpio).then(function(){
                message.info($scope.loc.gpio.add.messages.success);
                $location.path("/overview");
            }, function(res){
                error.show("gpio.general.messages", "gpio.add.messages", res);
            });
        }
    };
});
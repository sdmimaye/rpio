angular.module('rpio').config(function ($routeProvider) {
    $routeProvider.when('/gpio/edit/:id', {
        templateUrl: 'app/gpio/edit-gpio.html',
        controller: 'EditGpioCtrl',
        resolve:{
            model: function (gpio, $route) {
                return gpio.getById($route.current.params.id).then(function (res) {
                    return res.data;
                });
            }
        }
    });
});

angular.module("rpio").controller('EditGpioCtrl', function($scope, $location, gpio, model, message, error) {
    $scope.model = {
        gpio: model
    };

    $scope.view = {
        numbers:[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20],
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
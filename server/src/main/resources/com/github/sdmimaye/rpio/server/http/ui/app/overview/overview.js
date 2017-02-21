angular.module('rpio').config(function ($routeProvider) {
    $routeProvider.when('/overview', {
        templateUrl: 'app/overview/overview.html',
        controller: 'OverviewCtrl',
        resolve:{
            all: function (gpio) {
                return gpio.getAll().then(function(res){
                    return res.data;
                });
            }
        }
    }).otherwise({redirectTo: '/overview'});
});

angular.module("rpio").controller('OverviewCtrl', function($scope, $location, gpio, all, message, lang, error) {
    $scope.model = {
        gpios: all
    };

    console.log(all);

    gpio.register(function (message) {
        if(!$scope.model.gpios.some(function(pin){return pin.number === message.data.number}))
            $scope.model.gpios.push(message.data);

        $scope.model.gpios.forEach(function(pin){
            if(pin.number !== message.data.number) return;

            pin.state = message.data.state;
        });
    });

    $scope.view = {
        change: function (pin) {
            gpio.change(pin);
        },
        edit: function(pin){
            $location.path("/gpio/edit/" + pin.id)
        },
        delete: function(pin){
            gpio.delete(pin).then(function(res){
                $scope.model.gpios = $scope.model.gpios.filter(function(pin){ return pin.id !== res.data.id;});
                message.info(lang.translate("gpio.delete.messages.success"));
            }, function(res){
                error.show("gpio.general.messages", "gpio.delete.messages", res);
            });
        }
    }
});
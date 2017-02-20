angular.module('rpio').config(function ($routeProvider) {
    $routeProvider.when('/overview', {
        templateUrl: 'app/overview/overview.html',
        controller: 'OverviewCtrl',
        resolve:{

        }
    }).otherwise({redirectTo: '/overview'});
});

angular.module("rpio").controller('OverviewCtrl', function($scope, gpio) {
    $scope.model = {
        gpios: []
    };
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
        }
    }
});
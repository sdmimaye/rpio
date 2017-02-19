angular.module('rpio').config(function ($routeProvider) {
    $routeProvider.when('/overview', {
        templateUrl: 'app/overview/overview.html',
        controller: 'OverviewCtrl',
        resolve:{
            gpios: function(gpio){
                return gpio.getAll().then(function(res){
                    return res.data;
                });
            }
        }
    }).otherwise({redirectTo: '/overview'});
});

angular.module("rpio").controller('OverviewCtrl', function($scope, gpios) {
    console.log("GPIO's: ", gpios);
});
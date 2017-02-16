angular.module('rpio').config(function ($routeProvider) {
    $routeProvider.when('/overview', {
        templateUrl: 'app/overview/overview.html',
        controller: 'OverviewCtrl',
        resolve:{
            pins: function(pin){
                return pin.getAll().then(function(res){
                    return res.data;
                });
            }
        }
    }).otherwise({redirectTo: '/overview'});
});

angular.module("rpio").controller('OverviewCtrl', function($scope, pins) {
    console.log("Pins: ", pins);
});
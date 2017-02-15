angular.module('rpio').config(function ($routeProvider) {
    $routeProvider.when('/overview', {
        templateUrl: 'app/overview/overview.html',
        controller: 'OverviewCtrl',
        resolve:{

        }
    }).otherwise({redirectTo: '/overview'});
});

angular.module("rpio").controller('OverviewCtrl', function($scope) {

});
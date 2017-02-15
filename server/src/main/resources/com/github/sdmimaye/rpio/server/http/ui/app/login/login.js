angular.module('rpio').config(function ($routeProvider) {
    $routeProvider.when('/login', {
        templateUrl: 'app/login/login.html',
        controller: 'LoginCtrl',
        resolve:{

        }
    });
});

angular.module("rpio").controller('LoginCtrl', function($scope, $location, session) {
    session.register(function(event, current){
        if(!current || !current.loggedIn) return;

        $location.path("/overview")
    });

    $scope.view = {
        name: null,
        password: null,
        submit: function(){
            session.login($scope.view.name, $scope.view.password)
        }
    }
});
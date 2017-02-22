angular.module('rpio').config(function ($routeProvider) {
    $routeProvider.when('/user/edit/:id', {
        templateUrl: 'app/user/edit-user.html',
        controller: 'EditUserCtrl',
        resolve:{
            model: function (user, $route) {
                return user.getById($route.current.params.id).then(function (res) {
                    return res.data;
                });
            }
        }
    });
});

angular.module("rpio").controller('EditUserCtrl', function($scope, $location, user, model, message, error) {
    $scope.model = {
        user: model
    };

    $scope.view = {
        submit: function(){
            user.update($scope.model.user).then(function(){
                message.info($scope.loc.user.edit.messages.success);
                $location.path("/overview");
            }, function(res){
                error.show("user.general.messages", "user.edit.messages", res);
            });
        }
    };
});
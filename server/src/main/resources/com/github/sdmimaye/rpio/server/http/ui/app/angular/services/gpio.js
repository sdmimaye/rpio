angular.module('rpio').service('gpio', function($http){
    return {
        getAll: function () {
            return $http.get('api/gpio/');
        },
        getById: function (id) {
            return $http.get('api/gpio/' + id);
        }
    };
});
angular.module('rpio').service('pin', function($http){
    return {
        getAll: function () {
            return $http.get('api/pins/');
        },
        getById: function (id) {
            return $http.get('api/pins/' + id);
        }
    };
});
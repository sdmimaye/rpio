angular.module('rpio').service('user', function ($http) {
    return {
        getById: function (id) {
            return $http.get('api/user/' + id);
        },
        update: function (user) {
            return $http.put('api/user/' + user.id, user);
        }
    };
});
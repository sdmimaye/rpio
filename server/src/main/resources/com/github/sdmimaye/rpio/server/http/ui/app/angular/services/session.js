angular.module('rpio').service('session', function($http){
    var service = {
        session: null,
        callbacks: [],
        register: function(callback){
            service.callbacks.push(callback);
            service.callbacks.forEach(function(cb){
                cb('register', service.session);
            });
        },
        broadcast: function(event){
            service.callbacks.forEach(function(cb){
                cb(event, service.session);
            });
        },
        login: function (username, password) {
            return $http.post('api/session', {loginName: username, password: password}).then(function(res){
                service.session = res.data;
                service.broadcast('login');
            });
        },
        logout: function(){
            return $http.delete('api/session').then(function(){
                service.session = null;
                service.broadcast('logout');
            });
        },
        validate: function(){
            return $http.get('api/session').then(function(res){
                service.session = res.data;
                service.broadcast('validate');
            });
        }
    };

    return service;
});
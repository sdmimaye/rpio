angular.module('rpio').service('session', function($http, message, lang, gpio){
    var service = {
        session: null,
        callbacks: [],
        isValidSession: function(){
            if(!service.session) return false;

            return service.session.loggedIn;
        },
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

            if(service.isValidSession()){
                gpio.start();
            }else{
                gpio.stop();
            }
        },
        login: function (username, password) {
            return $http.post('api/session', {loginName: username, password: password}).then(function(res){
                service.session = res.data;
                service.broadcast('login');

                var translated = service.isValidSession() ? lang.translate('login.messages.success') : lang.translate('login.messages.error');
                var func = service.isValidSession() ? message.info : message.error;

                func(translated);
            }, function(){
                message.error(lang.translate('login.messages.error'));
            });
        },
        logout: function(){
            return $http.delete('api/session').then(function(){
                service.session = null;
                service.broadcast('logout');
                message.info(lang.translate('logout.messages.success'));
            }, function () {
                message.error(lang.translate('logout.messages.error'));
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
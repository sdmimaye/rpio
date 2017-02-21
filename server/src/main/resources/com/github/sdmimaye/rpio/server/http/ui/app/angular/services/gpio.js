angular.module('rpio').service('gpio', function ($http, $location, async) {
    var service = {
        socket: null,
        listeners: [],
        getAll: function () {
            return $http.get('api/gpio/');
        },
        getById: function (id) {
            return $http.get('api/gpio/' + id);
        },
        insert: function (gpio) {
            return $http.post('api/gpio/', gpio);
        },
        update: function (gpio) {
            return $http.put('api/gpio/' + gpio.id, gpio);
        },
        delete: function(gpio){
            return $http.delete('api/gpio/' + gpio.id);
        },
        isWebsocketConnected: function () {
            return service.socket !== null && service.socket;
        },
        start: function () {
            if (service.isWebsocketConnected()) return;

            service.stop();
            service.socket = async.listen('/api/async/gpio', function (message) {
                service.listeners.forEach(function (cb) {
                    cb(message);
                });
            });
        },
        stop: function () {
            if (!service.isWebsocketConnected()) return;

            service.socket.close();
            service.socket = null;
        },
        register: function (cb) {
            service.listeners.push(cb);
        },
        change: function (pin) {
            $http.put('/api/async/gpio/', {}, {
                headers: {
                    "command": "change",
                    "number": pin.number
                }
            });
        }
    };

    return service;
});
'use strict';

angular.module('rpio', ['ngRoute', 'ngSanitize', 'ngAnimate', 'ngCookies', 'angular.atmosphere', 'ngMaterial'])
    .config(['$locationProvider', function ($locationProvider) {
        $locationProvider.html5Mode(true);
    }])
    .config(['$httpProvider', function ($httpProvider) {
        var interceptor = ['$q', '$location', function ($q, $location) {
            return {
                response: function (response) {
                    return response;
                }, responseError: function (response) {
                    if (response.status === 403) {
                        $location.path("/login");
                    }

                    return $q.reject(response);
                }
            };
        }];

        $httpProvider.interceptors.push(interceptor);
    }])
    .config(['$httpProvider', function ($httpProvider) {
        var interceptor = ['$q', '$rootScope', function ($q, $rootScope) {
            return {
                request: function (config) {
                    if (config.method == 'GET' && config.url.indexOf('template/') !== 0) {
                        var separator = config.url.indexOf('?') === -1 ? '?' : '&';
                        config.url = config.url + separator + 'noCache=' + new Date().getTime();
                    }
                    return config;
                }
            };
        }];

        $httpProvider.interceptors.push(interceptor);
    }])
    .run(['session', function(session){
        session.validate();
    }]);
angular.module('rpio').service('lang', function($rootScope, de_DE, en_US){
    var service = {
        setLang: function(name){
            switch (name){
                case "de":
                case "de_DE":
                    $rootScope.loc = de_DE;
                    break;
                case "en":
                case "en_US":
                    $rootScope.loc = en_US;
                    break;
            }
        },
        getLangCode: function(){
            if(!$rootScope.loc) return null;


            return $rootScope.loc.code.long;
        },
        getLang: function(){
            return $rootScope.loc;
        },
        translate: function (path) {
            if(!angular.isString(path))
                throw 'Invalid translation attempt with path: ' + path;

            var value = $rootScope.loc;
            var paths = path.split(".");

            paths.forEach(function(p){
                if(!value) return;

                value = value[p];
            });

            return value ? value : '[' + path + ']';
        },
        initialize: function(){
            service.setLang("de_DE");
        }
    };

    return service;
});
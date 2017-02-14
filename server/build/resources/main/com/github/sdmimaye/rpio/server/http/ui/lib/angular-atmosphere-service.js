(function () {
'use strict';
angular.module('angular.atmosphere', [])
.service('atmosphereService', ['$rootScope', function ($rootScope) {
    var responseParameterDelegateFunctions = ['onOpen', 'onClientTimeout', 'onReopen', 'onMessage', 'onClose', 'onError'];
    var delegateFunctions = angular.copy(responseParameterDelegateFunctions);
    delegateFunctions.push('onTransportFailure');
    delegateFunctions.push('onReconnect');

    $rootScope.safeApply = $rootScope.safeApply || function(fn) {
        var phase = this.$root.$$phase;
        if(phase == '$apply' || phase == '$digest') {
          if(fn && (typeof(fn) === 'function')) {
            fn();
          }
        } else {
          this.$apply(fn);
        }
      };

    return {
      subscribe: function(r){
        var result = {};
        angular.forEach(r, function(value, property){
          if(typeof value === 'function' && delegateFunctions.indexOf(property) >= 0){
            if(responseParameterDelegateFunctions.indexOf(property) >= 0)
              result[property] = function(response){
                $rootScope.safeApply(function(){
                  r[property](response);
                });
              };
            else if(property === 'onTransportFailure')
              result.onTransportFailure = function(errorMsg, request){
                $rootScope.safeApply(function(){
                  r.onTransportFailure(errorMsg, request);
                });
              };
            else if(property === 'onReconnect')
              result.onReconnect = function(request, response){
                $rootScope.safeApply(function(){
                  r.onReconnect(request, response);
                });
              };
          }else
            result[property] = r[property];
        });

        return atmosphere.subscribe(result);
      },
      unsubscribe: atmosphere.unsubscribe
    };
}]);
})();
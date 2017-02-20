angular.module('rpio').service('message', function($mdToast){
    return {
        info: function(message){
            $mdToast.show(
                $mdToast.simple()
                    .textContent(message)
                    .hideDelay(3000)
                    .toastClass('toast toast-info')
                    .capsule(true)
            );
        },
        error: function(message){
            $mdToast.show(
                $mdToast.simple()
                    .textContent(message)
                    .hideDelay(3000)
                    .toastClass('toast toast-error')
                    .capsule(true)
            );
        }
    };
});
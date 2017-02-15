angular.module('rpio').component('loginState', {
    template:   '<div>' +
                    '<md-button ng-hide="$ctrl.model.session.loggedIn" class="md-icon-button" aria-label="Login" href="login">' +
                        '<md-icon md-font-icon="material-icons">account_circle</md-icon>' +
                    '</md-button>' +
                    '<md-button ng-show="$ctrl.model.session.loggedIn" class="md-icon-button md-warn" aria-label="Logout" ng-click="$ctrl.view.logout()">' +
                        '<md-icon md-font-icon="material-icons">cancel</md-icon>' +
                    '</md-button>' +
                '</div>',
    controller: function(session){
        var self = this;

        self.model = {};
        self.view = {
            logout: function(){
                session.logout();
            }
        };

        session.register(function(event, session){
            self.model.session = session;
            console.log(session);
        });
    }
});
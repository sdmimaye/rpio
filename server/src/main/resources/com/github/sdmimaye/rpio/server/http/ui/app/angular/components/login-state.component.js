angular.module('rpio').component('loginState', {
    template:   '<div>' +
                    '<md-button ng-hide="$ctrl.model.session.loggedIn" class="md-icon-button" aria-label="Login" href="/#!/login">' +
                        '<md-tooltip md-direction="left">{{$ctrl.loc.login.title}}</md-tooltip>' +
                        '<md-icon md-font-icon="material-icons">account_circle</md-icon>' +
                    '</md-button>' +
                    '<md-button ng-show="$ctrl.model.session.loggedIn" class="md-icon-button" aria-label="Logout" ng-click="$ctrl.view.logout()">' +
                        '<md-tooltip md-direction="left">{{$ctrl.loc.logout.title}}</md-tooltip>' +
                        '<md-icon md-font-icon="material-icons">cancel</md-icon>' +
                    '</md-button>' +
                '</div>',
    controller: function(session, lang, $location){
        var self = this;

        self.loc = lang.getLang();
        self.model = {
            session: null
        };
        self.view = {
            logout: function(){
                session.logout();
                $location.path('/login')
            }
        };

        session.register(function(event, session){
            self.model.session = session;
        });
    }
});
angular.module('rpio').component('loginState', {
    templateUrl: 'app/angular/components/login-state.component.html',
    controller: function(session, lang, $location){
        var self = this;

        self.loc = lang.getLang();
        self.model = {
            session: null
        };
        self.view = {
            language: function(){
                console.log("Changing-Lang");
                self.loc = lang.change();
            },
            logout: function(){
                session.logout();
                $location.path('/login')
            },
            user: function(){
                $location.path('/user/edit/' + self.model.session.id)
            }
        };

        session.register(function(event, session){
            self.model.session = session;
        });
    }
});
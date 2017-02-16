(function () {
    'use strict';

    angular.module('rpio').constant('de_DE', {
        code: {
            short: "de",
            long: "de_DE"
        },
        login:{
            title: "Login",
            name: "Benutzername",
            password: "Passwort",
            submit: "Einloggen",
            messages:{
                success: "Sie wurden erfolgreich eingeloggt",
                error: "Beim Login ist ein Fehler aufgetretten"
            }
        },
        logout:{
            title: "Logout",
            messages:{
                success: "Sie wurden erfolgreich ausgeloggt",
                error: "Beim Logout ist ein Fehler aufgetretten"
            }
        },
        pin:{
            add:{
                title: "Neuen GPIO-Pin hinzufügen"
            }
        }
    });
})();
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
        overview:{
            title: "Übersicht"
        },
        gpio:{
            general:{
                description: "Beschreibung",
                number: "GPIO Pin-Nummer",
                mode: "Modus",
                modes:{
                    input: "Eingang",
                    output: "Ausgang"
                },
                ouputMode: "Betriebsart",
                ouputModes:{
                    toggle: "Ein-/Ausschalten",
                    timeout: "Zeitgesteuert"
                },
                timeout: "Zeitlimit (in ms)",
                messages:{
                    missingDescription: "Bitte vergeben Sie eine Beschreibung",
                    missingNumber: "Bitte wählen Sie einen GPIO-Pin",
                    missingMode: "Bitte wählen Sie einen Modus",
                    pinInUse: "Dieser GPIO-Pin wird bereits verwendet",
                    descriptionInUse: "Diese Beschreibung wird bereits bei einem anderen GPIO-Pin verwendet"
                }
            },
            add:{
                title: "Neuen GPIO-Pin hinzufügen",
                messages:{
                    success: "Der GPIO-Pin wurde erfolgreich hinzugefügt",
                    error: "Der GPIO-Pin konnte nicht hinzugefügt werden"
                }
            },
            edit:{
                title: "GPIO-Pin bearbeiten",
                messages:{
                    success: "Der GPIO-Pin wurde erfolgreich bearbeitet",
                    error: "Der GPIO-Pin konnte nicht bearbeitet werden"
                }
            },
            delete:{
                messages:{
                    success: "Der GPIO-Pin wurde erfolgreich gelöscht",
                    error: "Beim Löschen des GPIO-Pins ist ein Fehler aufgetretten"
                }
            }
        }
    });
})();
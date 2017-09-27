(function () {
    'use strict';

    angular.module('rpio').constant('de_DE', {
        code: {
            short: "de",
            long: "de_DE"
        },
        lang:{
            title: "Sprache ändern"
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
            title: "Übersicht",
            empty: "Legen Sie einen neuen GPIO-Pin an"
        },
        user:{
            general:{
                loginName: "Benutzername",
                password1: "Passwort",
                password2: "Password wiederholen",
                email: "E-Mail Adresse",
                messages:{
                    passwordEmpty: "Bitte vergeben Sie ein gültiges Passwort",
                    loginNameEmpty: "Bitte vergeben Sie einen gültigen Benutzernamen",
                    passwordMismatch: "Die beiden Passwörter stimmen nicht überein",
                    invalidEmailAddress: "Die angegebene E-Mail Adresse ist ungültig",
                    loginTaken: "Dieser Benutzername wird bereits genutzt",
                    roleNotFound: "Eine Role konnte nicht gefunden werden"
                }
            },
            edit:{
                title: "Benutzereinstellungen bearbeiten",
                messages:{
                    success: "Die Benutzereinstellungen wurden erfolgreich übernommen",
                    error: "Beim aktualisieren der Benutzereinstellungen ist ein Fehler aufgetretten"
                }
            }
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
                logic: "Logik",
                logics:{
                    normal: "Normal",
                    inverted: "Invertiert"
                },
                ouputMode: "Betriebsart",
                ouputModes:{
                    toggle: "Ein-/Ausschalten",
                    timeout: "Zeitgesteuert"
                },
                timeout: "Zeitinterval (in ms)",
                messages:{
                    missingDescription: "Bitte vergeben Sie eine Beschreibung",
                    missingNumber: "Bitte wählen Sie einen GPIO-Pin",
                    missingMode: "Bitte wählen Sie einen Modus",
                    pinInUse: "Dieser GPIO-Pin wird bereits verwendet",
                    descriptionInUse: "Diese Beschreibung wird bereits bei einem anderen GPIO-Pin verwendet",
                    missingOutputMode: "Bitte geben Sie eine Betriebsart an",
                    missingTimeout: "Bitte geben Sie ein Zeitinterval an"
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
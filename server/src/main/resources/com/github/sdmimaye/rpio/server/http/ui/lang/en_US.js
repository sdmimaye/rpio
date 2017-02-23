(function () {
    'use strict';

    angular.module('rpio').constant('en_US', {
        code: {
            short: "en",
            long: "en_US"
        },
        lang:{
            title: "Change language"
        },
        login:{
            title: "Login",
            name: "Username",
            password: "Password",
            submit: "Login",
            messages:{
                success: "Login successful",
                error: "An error occured while logging in"
            }
        },
        logout:{
            title: "Logout",
            messages:{
                success: "Logout successful",
                error: "An error occured while logging out"
            }
        },
        overview:{
            title: "Overview",
            empty: "Add a new GPIO-Pin"
        },
        user:{
            general:{
                loginName: "Username",
                password1: "Password",
                password2: "Repeat Password",
                email: "E-Mail",
                messages:{
                    passwordEmpty: "Please choose a valid password",
                    loginNameEmpty: "Please choose a valid username",
                    passwordMismatch: "Passwords mismatch",
                    invalidEmailAddress: "Invalid E-Mail address",
                    loginTaken: "Username is already in use",
                    roleNotFound: "Role not found"
                }
            },
            edit:{
                title: "Edit user settings",
                messages:{
                    success: "User settings saved",
                    error: "An error occured while changing the user settings"
                }
            }
        },
        gpio:{
            general:{
                description: "Description",
                number: "GPIO Pin number",
                mode: "Mode",
                modes:{
                    input: "Input",
                    output: "Output"
                },
                ouputMode: "Output-Mode",
                ouputModes:{
                    toggle: "Toggle (On/Off)",
                    timeout: "Timeout"
                },
                timeout: "Timeout (in ms)",
                messages:{
                    missingDescription: "Please choose a valid/unique description",
                    missingNumber: "Please choose a valid GPIO-Pin number",
                    missingMode: "Please choose a valod operation mode",
                    pinInUse: "The selected GPIO-Pin is already in use",
                    descriptionInUse: "The selected description is already in use",
                    missingOutputMode: "Please choose a valid output mode",
                    missingTimeout: "Please choose a valid timeout"
                }
            },
            add:{
                title: "Add GPIO-Pin",
                messages:{
                    success: "New GPIO-Pin added",
                    error: "An error occured while adding a new GPIO-Pin"
                }
            },
            edit:{
                title: "Edit GPIO-Pin",
                messages:{
                    success: "GPIO-Pin changes saved",
                    error: "An error occured while saving the changes"
                }
            },
            delete:{
                messages:{
                    success: "GPIO-Pin deleted",
                    error: "An error occured while deleting the GPIO-Pin"
                }
            }
        }
    });
})();
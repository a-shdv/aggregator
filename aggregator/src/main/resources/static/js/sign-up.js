'use strict';

document.addEventListener('DOMContentLoaded', function () {
    // Selecting form elements
    const emailInput = document.getElementById('email');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const passwordConfirmInput = document.getElementById('passwordConfirm');

    // Adding event listeners to form elements
    emailInput.addEventListener('input', saveFormValues);
    usernameInput.addEventListener('input', saveFormValues);
    passwordInput.addEventListener('input', saveFormValues);
    passwordConfirmInput.addEventListener('input', saveFormValues);

    // Function to save form field values in local storage
    function saveFormValues() {
        // Saving values to local storage
        localStorage.setItem('registerEmail', emailInput.value);
        localStorage.setItem('registerUsername', usernameInput.value);
        localStorage.setItem('registerPassword', passwordInput.value);
        localStorage.setItem('registerPasswordConfirm', passwordConfirmInput.value);
    }

    // Function to load form field values from local storage
    function loadFormValues() {
        // Loading values from local storage
        emailInput.value = localStorage.getItem('registerEmail') || '';
        usernameInput.value = localStorage.getItem('registerUsername') || '';
        passwordInput.value = localStorage.getItem('registerPassword') || '';
        passwordConfirmInput.value = localStorage.getItem('registerPasswordConfirm') || '';
    }

    // Load form values when the page loads
    loadFormValues();
});

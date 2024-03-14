'use strict';

document.addEventListener('DOMContentLoaded', function () {
    // Selecting form elements
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');

    // Adding event listeners to form elements
    usernameInput.addEventListener('input', saveFormValues);
    passwordInput.addEventListener('input', saveFormValues);

    // Function to save form field values in local storage
    function saveFormValues() {
        // Saving values to local storage
        localStorage.setItem('loginUsername', usernameInput.value);
        localStorage.setItem('loginPassword', passwordInput.value);
    }

    // Function to load form field values from local storage
    function loadFormValues() {
        // Loading values from local storage
        usernameInput.value = localStorage.getItem('loginUsername') || '';
        passwordInput.value = localStorage.getItem('loginPassword') || '';
    }

    // Load form values when the page loads
    loadFormValues();
});

'use strict';

document.addEventListener('DOMContentLoaded', function () {
    // Selecting form elements
    const oldPasswordInput = document.getElementById('oldPassword');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmNewPasswordInput = document.getElementById('confirmNewPassword');

    // Adding event listeners to form elements
    oldPasswordInput.addEventListener('input', saveFormValues);
    newPasswordInput.addEventListener('input', saveFormValues);
    confirmNewPasswordInput.addEventListener('input', saveFormValues);

    // Function to save form field values in local storage
    function saveFormValues() {
        // Saving values to local storage
        localStorage.setItem('oldPassword', oldPasswordInput.value);
        localStorage.setItem('newPassword', newPasswordInput.value);
        localStorage.setItem('confirmNewPassword', confirmNewPasswordInput.value);
    }

    // Function to load form field values from local storage
    function loadFormValues() {
        // Loading values from local storage
        oldPasswordInput.value = localStorage.getItem('oldPassword') || '';
        newPasswordInput.value = localStorage.getItem('newPassword') || '';
        confirmNewPasswordInput.value = localStorage.getItem('confirmNewPassword') || '';
    }

    // Load form values when the page loads
    loadFormValues();
});

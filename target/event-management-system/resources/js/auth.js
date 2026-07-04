// Authentication JavaScript
const API_BASE_URL = '/event-management-system/api';

// Tab switching for login page
function showTab(event, tabName) {
    // Remove active class from all tabs and forms
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.auth-form').forEach(form => form.classList.remove('active'));
    
    // Add active class to selected tab and form
    event.target.classList.add('active');
    document.getElementById(`${tabName}-login-form`).classList.add('active');
}

// Show alert message
function showAlert(message, type = 'info') {
    const existingAlert = document.querySelector('.alert');
    if (existingAlert) existingAlert.remove();
    
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;
    
    const form = document.querySelector('.auth-form.active');
    form.insertBefore(alert, form.firstChild);
    
    setTimeout(() => alert.remove(), 5000);
}

// Password strength checker
function checkPasswordStrength(password) {
    const strengthIndicator = document.getElementById('password-strength');
    if (!strengthIndicator) return;
    
    if (password.length === 0) {
        strengthIndicator.classList.remove('show', 'weak', 'medium', 'strong');
        return;
    }
    
    strengthIndicator.classList.add('show');
    strengthIndicator.classList.remove('weak', 'medium', 'strong');
    
    let strength = 0;
    if (password.length >= 6) strength++;
    if (password.length >= 10) strength++;
    if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++;
    if (/\d/.test(password)) strength++;
    if (/[^a-zA-Z\d]/.test(password)) strength++;
    
    if (strength <= 2) strengthIndicator.classList.add('weak');
    else if (strength <= 4) strengthIndicator.classList.add('medium');
    else strengthIndicator.classList.add('strong');
}

// Gmail validation helper
function isGmail(email) {
    return email.toLowerCase().endsWith("@gmail.com");
}

// ----------------- User Login -----------------
if (document.getElementById('user-login-form')) {
    document.getElementById('user-login-form').addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const username = document.getElementById('user-username').value;
        const password = document.getElementById('user-password').value;
        const submitBtn = this.querySelector('button[type="submit"]');
        
        submitBtn.disabled = true;
        submitBtn.classList.add('loading');
        
        try {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });
            
            const data = await response.json();
            
            if (response.ok) {
                showAlert('Login successful! Redirecting...', 'success');
                setTimeout(() => { window.location.href = 'user-dashboard.html'; }, 1000);
            } else {
                showAlert(data.error || 'Login failed', 'error');
                submitBtn.disabled = false;
                submitBtn.classList.remove('loading');
            }
        } catch (error) {
            console.error('Login error:', error);
            showAlert('An error occurred. Please try again.', 'error');
            submitBtn.disabled = false;
            submitBtn.classList.remove('loading');
        }
    });
}

// ----------------- Admin Login -----------------
if (document.getElementById('admin-login-form')) {
    document.getElementById('admin-login-form').addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const username = document.getElementById('admin-username').value;
        const password = document.getElementById('admin-password').value;
        const submitBtn = this.querySelector('button[type="submit"]');
        
        submitBtn.disabled = true;
        submitBtn.classList.add('loading');
        
        try {
            const response = await fetch(`${API_BASE_URL}/auth/admin-login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });
            
            const data = await response.json();
            
            if (response.ok) {
                showAlert('Admin login successful! Redirecting...', 'success');
                setTimeout(() => { window.location.href = 'admin-dashboard.html'; }, 1000);
            } else {
                showAlert(data.error || 'Admin login failed', 'error');
                submitBtn.disabled = false;
                submitBtn.classList.remove('loading');
            }
        } catch (error) {
            console.error('Admin login error:', error);
            showAlert('An error occurred. Please try again.', 'error');
            submitBtn.disabled = false;
            submitBtn.classList.remove('loading');
        }
    });
}

// ----------------- User Registration -----------------
if (document.getElementById('register-form')) {
    const emailInput = document.getElementById('email');
    const emailError = document.getElementById('emailError');
    const passwordInput = document.getElementById('password');
    
    // Password strength indicator
    passwordInput.addEventListener('input', function() { checkPasswordStrength(this.value); });

    // Real-time Gmail validation
    emailInput.addEventListener('input', function() {
        if (!isGmail(emailInput.value.trim())) {
            emailError.textContent = "Email must end with @gmail.com";
            emailInput.style.borderColor = "red";
        } else {
            emailError.textContent = "";
            emailInput.style.borderColor = "green";
        }
    });

    // Submit-time validation
    document.getElementById('register-form').addEventListener('submit', async function(e) {
        e.preventDefault();

        const username = document.getElementById('username').value.trim();
        const email = emailInput.value.trim();
        const fullName = document.getElementById('full-name').value.trim();
        const phone = document.getElementById('phone').value.trim();
        const password = passwordInput.value;
        const confirmPassword = document.getElementById('confirm-password').value;
        const submitBtn = this.querySelector('button[type="submit"]');

        if (!isGmail(email)) {
            emailError.textContent = "Email must end with @gmail.com";
            emailInput.style.borderColor = "red";
            return; // stop submit
        }
        emailError.textContent = "";
        emailInput.style.borderColor = "";

        if (password !== confirmPassword) { showAlert('Passwords do not match!', 'error'); return; }
        if (password.length < 6) { showAlert('Password must be at least 6 characters!', 'error'); return; }

        submitBtn.disabled = true;
        submitBtn.classList.add('loading');

        try {
            const response = await fetch(`${API_BASE_URL}/auth/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, email, fullName, phone, password })
            });

            const data = await response.json();

            if (response.ok) {
                showAlert('Registration successful! Redirecting...', 'success');
                setTimeout(() => { window.location.href = 'user-dashboard.html'; }, 1500);
            } else {
                showAlert(data.error || 'Registration failed', 'error');
                submitBtn.disabled = false;
                submitBtn.classList.remove('loading');
            }
        } catch (error) {
            console.error('Registration error:', error);
            showAlert('An error occurred. Please try again.', 'error');
            submitBtn.disabled = false;
            submitBtn.classList.remove('loading');
        }
    });
}
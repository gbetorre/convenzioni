// Shared Form Utilities
// This file contains common functionality used across all login forms

class FormUtils {
    static validateEmail(value) {
        if (!value) {
            return { isValid: false, message: 'Email address is required' };
        }
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) {
            return { isValid: false, message: 'Please enter a valid email address' };
        }
        return { isValid: true };
    }
    
    static validatePassword(value) {
        if (!value) {
            return { isValid: false, message: 'Password is required' };
        }
        if (value.length < 8) {
            return { isValid: false, message: 'Password must be at least 8 characters long' };
        }
        if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(value)) {
            return { isValid: false, message: 'Password must contain uppercase, lowercase, and number' };
        }
        return { isValid: true };
    }
    
    static showError(fieldName, message) {
        const formGroup = document.getElementById(fieldName).closest('.form-group');
        const errorElement = document.getElementById(fieldName + 'Error');
        
        if (formGroup && errorElement) {
            formGroup.classList.add('error');
            errorElement.textContent = message;
            errorElement.classList.add('show');
            
            // Add shake animation to the field
            const field = document.getElementById(fieldName);
            if (field) {
                field.style.animation = 'shake 0.5s ease-in-out';
                setTimeout(() => {
                    field.style.animation = '';
                }, 500);
            }
        }
    }
    
    static clearError(fieldName) {
        const formGroup = document.getElementById(fieldName).closest('.form-group');
        const errorElement = document.getElementById(fieldName + 'Error');
        
        if (formGroup && errorElement) {
            formGroup.classList.remove('error');
            errorElement.classList.remove('show');
            setTimeout(() => {
                errorElement.textContent = '';
            }, 300);
        }
    }
    
    static showSuccess(fieldName) {
        const field = document.getElementById(fieldName);
        const wrapper = field?.closest('.input-wrapper');
        
        if (wrapper) {
            // Add subtle success indication
            wrapper.style.borderColor = '#22c55e';
            setTimeout(() => {
                wrapper.style.borderColor = '';
            }, 2000);
        }
    }
    
    static simulateLogin(email, password) {
        return new Promise((resolve, reject) => {
            // Simulate network delay
            setTimeout(() => {
                // Demo: reject if email is 'admin@demo.com' and password is 'wrongpassword'
                if (email === 'admin@demo.com' && password === 'wrongpassword') {
                    reject(new Error('Invalid email or password'));
                } else {
                    resolve({ success: true, user: { email } });
                }
            }, 2000);
        });
    }
    
    static showNotification(message, type = 'info', container = null) {
        const targetContainer = container || document.querySelector('form');
        if (!targetContainer) return;
        
        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        
        let backgroundColor, borderColor, textColor;
        switch (type) {
            case 'error':
                backgroundColor = 'rgba(239, 68, 68, 0.1)';
                borderColor = 'rgba(239, 68, 68, 0.3)';
                textColor = '#ef4444';
                break;
            case 'success':
                backgroundColor = 'rgba(34, 197, 94, 0.1)';
                borderColor = 'rgba(34, 197, 94, 0.3)';
                textColor = '#22c55e';
                break;
            default:
                backgroundColor = 'rgba(6, 182, 212, 0.1)';
                borderColor = 'rgba(6, 182, 212, 0.3)';
                textColor = '#06b6d4';
        }
        
        notification.innerHTML = `
            <div style="
                background: ${backgroundColor}; 
                backdrop-filter: blur(10px); 
                border: 1px solid ${borderColor}; 
                border-radius: 12px; 
                padding: 12px 16px; 
                margin-top: 16px; 
                color: ${textColor}; 
                text-align: center;
                font-size: 14px;
                animation: slideIn 0.3s ease;
            ">
                ${message}
            </div>
        `;
        
        targetContainer.appendChild(notification);
        
        // Remove notification after 3 seconds
        setTimeout(() => {
            notification.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => {
                notification.remove();
            }, 300);
        }, 3000);
    }
    
    static setupFloatingLabels(form) {
        const inputs = form.querySelectorAll('input');
        inputs.forEach(input => {
            // Check if field has value on page load
            if (input.value.trim() !== '') {
                input.classList.add('has-value');
            }
            
            input.addEventListener('input', () => {
                if (input.value.trim() !== '') {
                    input.classList.add('has-value');
                } else {
                    input.classList.remove('has-value');
                }
            });
        });
    }
    
    static setupPasswordToggle(passwordInput, toggleButton) {
        if (toggleButton && passwordInput) {
            toggleButton.addEventListener('click', () => {
                const isPassword = passwordInput.type === 'password';
                const eyeIcon = toggleButton.querySelector('.eye-icon');
                
                passwordInput.type = isPassword ? 'text' : 'password';
                if (eyeIcon) {
                    eyeIcon.classList.toggle('show-password', isPassword);
                }
                
                // Add smooth transition effect
                toggleButton.style.transform = 'scale(0.9)';
                setTimeout(() => {
                    toggleButton.style.transform = 'scale(1)';
                }, 150);
                
                // Keep focus on password input
                passwordInput.focus();
            });
        }
    }
    
    static addEntranceAnimation(element, delay = 100) {
        if (element) {
            element.style.opacity = '0';
            element.style.transform = 'translateY(30px)';
            
            setTimeout(() => {
                element.style.transition = 'all 0.6s cubic-bezier(0.4, 0, 0.2, 1)';
                element.style.opacity = '1';
                element.style.transform = 'translateY(0)';
            }, delay);
        }
    }
    
    static addSharedAnimations() {
        // Add CSS animations to document head if not already present
        if (!document.getElementById('shared-animations')) {
            const style = document.createElement('style');
            style.id = 'shared-animations';
            style.textContent = `
                @keyframes slideIn {
                    from { opacity: 0; transform: translateY(-10px); }
                    to { opacity: 1; transform: translateY(0); }
                }
                
                @keyframes slideOut {
                    from { opacity: 1; transform: translateY(0); }
                    to { opacity: 0; transform: translateY(-10px); }
                }
                
                @keyframes shake {
                    0%, 100% { transform: translateX(0); }
                    25% { transform: translateX(-5px); }
                    75% { transform: translateX(5px); }
                }
                
                @keyframes checkmarkPop {
                    0% { transform: scale(0); }
                    50% { transform: scale(1.3); }
                    100% { transform: scale(1); }
                }
                
                @keyframes successPulse {
                    0% { transform: scale(0); }
                    50% { transform: scale(1.1); }
                    100% { transform: scale(1); }
                }
                
                @keyframes spin {
                    0% { transform: translate(-50%, -50%) rotate(0deg); }
                    100% { transform: translate(-50%, -50%) rotate(360deg); }
                }
            `;
            document.head.appendChild(style);
        }
    }
}
// Custom JavaScript

// Initialize tooltips
document.addEventListener('DOMContentLoaded', function() {
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});

// Initialize popovers
document.addEventListener('DOMContentLoaded', function() {
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function(popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
});

// Form validation
document.addEventListener('DOMContentLoaded', function() {
    var forms = document.querySelectorAll('.needs-validation');
    Array.prototype.slice.call(forms).forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
});

// Auto-hide alerts
document.addEventListener('DOMContentLoaded', function() {
    var alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
});

// Confirm delete
document.addEventListener('DOMContentLoaded', function() {
    var deleteButtons = document.querySelectorAll('[data-confirm]');
    deleteButtons.forEach(function(button) {
        button.addEventListener('click', function(event) {
            if (!confirm(this.dataset.confirm)) {
                event.preventDefault();
            }
        });
    });
});

// Tab persistence
document.addEventListener('DOMContentLoaded', function() {
    var tabEls = document.querySelectorAll('button[data-bs-toggle="tab"]');
    tabEls.forEach(function(tabEl) {
        tabEl.addEventListener('shown.bs.tab', function(event) {
            localStorage.setItem('lastTab', event.target.id);
        });
    });

    var lastTab = localStorage.getItem('lastTab');
    if (lastTab) {
        var tab = document.querySelector('#' + lastTab);
        if (tab) {
            var tabInstance = new bootstrap.Tab(tab);
            tabInstance.show();
        }
    }
});

// Form autosave
document.addEventListener('DOMContentLoaded', function() {
    var forms = document.querySelectorAll('form[data-autosave]');
    forms.forEach(function(form) {
        var formId = form.id;
        var savedData = localStorage.getItem('form_' + formId);
        
        if (savedData) {
            var data = JSON.parse(savedData);
            Object.keys(data).forEach(function(key) {
                var input = form.querySelector('[name="' + key + '"]');
                if (input) {
                    input.value = data[key];
                }
            });
        }

        form.addEventListener('input', function() {
            var formData = new FormData(form);
            var data = {};
            for (var pair of formData.entries()) {
                data[pair[0]] = pair[1];
            }
            localStorage.setItem('form_' + formId, JSON.stringify(data));
        });

        form.addEventListener('submit', function() {
            localStorage.removeItem('form_' + formId);
        });
    });
}); 
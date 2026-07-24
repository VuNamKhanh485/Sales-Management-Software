document.addEventListener('DOMContentLoaded', function() {
    const selects = document.querySelectorAll('select:not(.no-custom)');
    
    selects.forEach(select => {
        if (select.dataset.customSelectProcessed === 'true') return;
        select.dataset.customSelectProcessed = 'true';
        
        // Hide original select
        select.style.display = 'none';

        // Create container
        const container = document.createElement('div');
        container.className = 'custom-select-container ' + (select.className || '');
        
        // Create trigger
        const trigger = document.createElement('div');
        trigger.className = 'custom-select-trigger';
        
        const text = document.createElement('span');
        text.className = 'custom-select-text';
        
        const selectedOption = select.options[select.selectedIndex];
        text.textContent = selectedOption ? selectedOption.text : 'Chọn...';
        
        const icon = document.createElement('span');
        icon.className = 'custom-select-icon';
        icon.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#64748b" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="6 9 12 15 18 9"></polyline></svg>';
        
        trigger.appendChild(text);
        trigger.appendChild(icon);
        
        // Create menu
        const menu = document.createElement('div');
        menu.className = 'custom-select-menu';
        
        // Populate options
        Array.from(select.options).forEach((option) => {
            const item = document.createElement('div');
            item.className = 'custom-select-item';
            if (option.selected) item.classList.add('active');
            item.textContent = option.text;
            item.dataset.value = option.value;
            
            item.addEventListener('click', function(e) {
                e.stopPropagation();
                // Update text
                text.textContent = option.text;
                // Update native select
                select.value = option.value;
                // Trigger change event
                select.dispatchEvent(new Event('change', { bubbles: true }));
                
                // Update active class
                menu.querySelectorAll('.custom-select-item').forEach(i => i.classList.remove('active'));
                item.classList.add('active');
                
                menu.classList.remove('show');
                container.classList.remove('focused');
            });
            menu.appendChild(item);
        });
        
        container.appendChild(trigger);
        container.appendChild(menu);
        
        // Insert after select
        select.parentNode.insertBefore(container, select.nextSibling);
        
        // Toggle menu
        trigger.addEventListener('click', function(e) {
            e.stopPropagation();
            // Close all other open menus
            document.querySelectorAll('.custom-select-menu.show').forEach(m => {
                if (m !== menu) {
                    m.classList.remove('show');
                    m.parentElement.classList.remove('focused');
                }
            });
            
            const isShowing = menu.classList.toggle('show');
            if (isShowing) {
                container.classList.add('focused');
            } else {
                container.classList.remove('focused');
            }
        });
    });
    
    // Close on click outside
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.custom-select-container')) {
            document.querySelectorAll('.custom-select-menu.show').forEach(m => {
                m.classList.remove('show');
                m.parentElement.classList.remove('focused');
            });
        }
    });
});

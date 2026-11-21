const pageModal = document.getElementById('pageModal');
const pageContent = document.getElementById('pageContent');
const closeModalBtn = document.getElementById('closeModal');

if (closeModalBtn && pageModal) {
	closeModalBtn.addEventListener('click', () => pageModal.close());
}
if (pageModal) {
	pageModal.addEventListener('click', (event) => {
		if (event.target === pageModal) pageModal.close();
	});
}

function submitForm(btn, configMsg) {
    const form = btn.closest('form');
    if (!form) return;

    // If there is a message, we must confirm. 
    // If the user clicks 'Cancel', we exit the function.
    if (configMsg && configMsg.trim() !== "") {
        if (!confirm(configMsg)) {
            return; // Stop here if they hit Cancel
        }
    }

    // If no message was provided OR they confirmed the message, submit.
    form.submit();
}

function openModal(url) {
	fetch(url)
	.then(response => {
		// Check if the request was successful
		if (!response.ok) {
			throw new Error(`HTTP error! status: ${response.status}`);
		}
		// Parse the response as plain text
		return response.text();
	})
	.then(htmlContent => {
		// Set the innerHTML of the target div to the fetched content
		pageContent.innerHTML = htmlContent;
	})
	.catch(error => {
		console.error('Error loading HTML:', error);
	});
	pageModal.showModal();
}

function showDebug(index, rand)
{
	toggle(document.querySelectorAll('.debug_'+rand+'_'+index));
}

function toggle(elements, specifiedDisplay)
{
	var element, index;

	elements = elements.length ? elements : [elements];
	for (index = 0; index < elements.length; index++)
	{
		element = elements[index];

		if (isElementHidden(element))
		{
			element.style.display = '';

			// If the element is still hidden after removing the inline display
			if (isElementHidden(element))
			{
				element.style.display = specifiedDisplay || 'block';
			}
		}
		else
		{
			element.style.display = 'none';
		}
	}

	function isElementHidden (element)
	{
		return window.getComputedStyle(element, null).getPropertyValue('display') === 'none';
	}
}

async function copy_detail_data(btn)
{
	const container = $(btn).closest('tr').find('.detail-control');
	const text = $(btn).closest('tr').find('.detail-input').val();

	$('.detail-control').removeClass('copied');
	try {
		await navigator.clipboard.writeText(text);
		$(container).addClass('copied');
	} catch (err) {
		console.error('failed to copy text: ', err);
		$(container).addClass('error');
	}
}

function addAddress() {
    const container = document.getElementById('address-container');
    const templateElement = document.getElementById('address-template');
    
    if (!templateElement) {
        console.error("Address template not found!");
        return;
    }

    // Use .innerHTML to get the raw string
    let templateHtml = templateElement.innerHTML;
    
    const nextIndex = container.querySelectorAll('.address-row').length;
    
    // 1. Replace __INDEX__ (handles both literal and encoded brackets)
    // We use a global regex to find every instance
    templateHtml = templateHtml.replace(/INDEX_VAR/g, nextIndex);
    
    // 2. Replace the display number
    templateHtml = templateHtml.replace(/INDEX_DISPLAY_VAR/g, nextIndex + 1);
    
    // 3. Append to the container
    container.insertAdjacentHTML('beforeend', templateHtml);
}

function removeAndReindex(btn) {
    const container = document.getElementById('address-container');
    btn.closest('.address-row').remove();
    
    // Loop through all remaining rows to fix indices (0, 1, 2...)
    container.querySelectorAll('.address-row').forEach((row, index) => {
        row.querySelectorAll('input, select, label').forEach(el => {
            // 1. Fix Spring indices in [name] and [id] attributes
            // This specifically targets 'addresses[0]' and changes the number
            ['name', 'id', 'for'].forEach(attr => {
                if (el.hasAttribute(attr)) {
                    const oldVal = el.getAttribute(attr);
                    // Regex looks for '[digits]' and replaces with '[newIndex]'
                    const newVal = oldVal.replace(/\[\d+\]/g, `[${index}]`)
                                         .replace(/_\d+_/g, `_${index}_`); // Fixes ID underscores
                    el.setAttribute(attr, newVal);
                }
            });

            // 2. Fix 1Password / Autocomplete sections
            if (el.hasAttribute('autocomplete')) {
                const auto = el.getAttribute('autocomplete');
                el.setAttribute('autocomplete', auto.replace(/section-\d+/, `section-${index}`));
            }
        });
    });
}

function parse_address(element) {
    const row = element.closest('.address-row');
    if (!row) return;

    let full = element.value.trim();
    
    // Regex breakdown:
    // (.*)             -> Group 1: Everything before the city/state (Street/Ext)
    // ,\s*             -> REQUIRED: A comma and optional space
    // ([A-Za-z\s.\-]+) -> Group 2: City Name
    // [,\s]+           -> FLEXIBLE: A comma OR space(s) between City and State
    // ([A-Z]{2}|[A-Za-z\s]+) -> Group 3: State (ID or Idaho)
    // [,\s]+           -> FLEXIBLE: A comma OR space(s) between State and Zip
    // (\d{5}(?:-\d{4})?) $ -> Group 4: Zip Code at the very end
    const addressRegex = /^(.*),\s*([A-Za-z\s.\-]+)[,\s]+([A-Z]{2}|[A-Za-z\s]+)[,\s]+(\d{5}(?:-\d{4})?)$/;
    
    const match = full.match(addressRegex);

    if (match) {
        let streetPart = match[1].trim(); // Everything before the first comma
        let city = match[2].trim();
        let state = match[3].trim();
        let zip = match[4].trim();
        
        let street = streetPart;
        let streetExt = '';

        // Optional: Check the streetPart for a second comma to extract StreetExt
        if (streetPart.includes(',')) {
            let subParts = streetPart.split(',');
            street = subParts[0].trim();
            streetExt = subParts.slice(1).join(', ').trim();
        }

        // Helper to update fields
        const set = (sel, val) => { 
            const el = row.querySelector(sel);
            if (el) el.value = val;
        };

        set('input[name*=".street"]', street);
        set('input[name*=".streetExt"]', streetExt);
        set('input[name*=".city"]', city);
        set('input[name*=".zip"]', zip);

        // Update State Dropdown
        const stateSelect = row.querySelector('select[name*=".stateAbbr"]');
        if (stateSelect) {
            const opt = [...stateSelect.options].find(o => 
                [o.value, o.text].some(v => v.toLowerCase() === state.toLowerCase())
            );
            if (opt) stateSelect.value = opt.value;
        }
    } else {
        // If it fails the regex (e.g., no comma), we do nothing.
        // The text stays in the street input exactly as the user (or 1Password) typed it.
        console.log("Address format not recognized; skipping auto-parse.");
    }
}

function copyInvite(btn) {
    const name = btn.getAttribute('data-name');
    const key = btn.getAttribute('data-key');
    const url = window.location.origin + '/join/verify?key=' + key;
    
    const emailBody = `Hi ${name},\n\nYou have been invited! Please click the link below to verify your account:\n\n${url}\n\nBest regards,\nAdmin Team`;

    navigator.clipboard.writeText(emailBody).then(() => {
        const originalText = btn.innerHTML;
        btn.classList.replace('btn-outline-primary', 'btn-success');
        btn.innerHTML = '<i class="fa-solid fa-check"></i> Copied!';
        
        setTimeout(() => {
            btn.classList.replace('btn-success', 'btn-outline-primary');
            btn.innerHTML = originalText;
        }, 2000);
    });
}

function confirmDelete() {
    return confirm("Are you absolutely sure you want to delete this item? This action cannot be undone.");
}

document.addEventListener('click', (event) => {
	const submitBtn = event.target.closest('[data-confirm-submit]');
	if (submitBtn) {
		submitForm(submitBtn, submitBtn.getAttribute('data-confirm-submit'));
		return;
	}

	const deleteBtn = event.target.closest('[data-confirm-delete]');
	if (deleteBtn && !confirmDelete()) {
		event.preventDefault();
		return;
	}

	const copyInviteBtn = event.target.closest('[data-copy-invite]');
	if (copyInviteBtn) {
		copyInvite(copyInviteBtn);
	}
});

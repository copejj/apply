$(document).ready(function () {
	if ($('#logsTable').length) {
		$('#logsTable').DataTable({
			pageLength: 25,
			lengthMenu: [[10, 25, 50, -1], [10, 25, 50, 'All']],
			paging: true,
			lengthChange: true,
			searching: true,
			ordering: true,
			info: true,
			autoWidth: false,
			order: [{ idx: 1, dir: 'desc' }, { idx: 2, dir: 'asc' }],
			responsive: true,
			initComplete: function () {
				$(this).addClass('d-some');
			}
		});
	}

	if ($('#weeksTable').length) {
		$('#weeksTable').DataTable({
			pageLength: 10,
			lengthMenu: [[10, 25, 50, -1], [10, 25, 50, 'All']],
			paging: true,
			lengthChange: true,
			searching: true,
			ordering: true,
			info: true,
			autoWidth: false,
			order: [[0, 'desc']],
			columnDefs: [
				{ width: '40%', targets: 0 },
				{ width: '30%', targets: 1 },
				{ type: 'num', width: '10%', targets: 2 }
			],
			responsive: true,
			initComplete: function () {
				$(this).show();
			}
		});
	}

	$('.detailsDashboardTable').on('click', '.copy-grid-btn', function () {
		const textToCopy = $(this).attr('data-copy-val');
		if (!textToCopy) return;

		const buttonElement = this;
		navigator.clipboard.writeText(textToCopy)
			.then(() => {
				const icon = buttonElement.querySelector('i');
				icon.className = 'fa-solid fa-check text-success';
				setTimeout(() => {
					icon.className = 'fa-regular fa-copy small';
				}, 1000);
			})
			.catch(err => console.error('Clipboard failure: ', err));
	});

	const logsTable = document.getElementById('logsTable');
	if (logsTable) {
		logsTable.addEventListener('change', function (event) {
			if (event.target && event.target.classList.contains('status-dropdown')) {
				const dropdown = event.target;
				const logId = dropdown.getAttribute('data-log-id');
				const statusId = dropdown.value;

				const selectedText = dropdown.options[dropdown.selectedIndex].text;
				const newStatusText = dropdown.value === '' ? '' : selectedText;

				fetch('/logs/update-status', {
					method: 'POST',
					headers: {
						'Content-Type': 'application/x-www-form-urlencoded',
						'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]')?.content
					},
					body: new URLSearchParams({
						logId: logId,
						statusId: statusId
					})
				})
					.then(response => {
						if (response.ok) {
							dropdown.style.borderColor = '#198754';
							setTimeout(() => (dropdown.style.borderColor = ''), 1500);

							const cellElement = dropdown.closest('td');
							cellElement.setAttribute('data-search', newStatusText);
							cellElement.setAttribute('data-order', newStatusText);

							const hiddenSpan = cellElement.querySelector('span');
							if (hiddenSpan) hiddenSpan.textContent = newStatusText;

							if ($.fn.DataTable.isDataTable('#logsTable')) {
								const dtTable = $('#logsTable').DataTable();
								const rowIndex = dtTable.row(dropdown.closest('tr')).index();
								const colIndex = dtTable.column(cellElement).index();

								dtTable.cell(rowIndex, colIndex).invalidate();
								dtTable.rows().invalidate();
							}
						} else {
							alert('Failed to save status. Reloading page to reset...');
							window.location.reload();
						}
					})
					.catch(error => {
						console.error('Error saving status:', error);
						alert('A connection error occurred. Status not saved.');
					});
			}
		});
	}
});

function copyDashboardToBuffer() {
	const bufferContent = document.getElementById('rawTextBufferBox').value;
	const actionBtn = document.getElementById('btnCopyToClipboard');
	if (!bufferContent || bufferContent.trim() === '' || bufferContent.startsWith('No logs')) {
		alert('No structural application data found to capture.');
		return;
	}
	navigator.clipboard.writeText(bufferContent)
		.then(() => {
			const savedMarkup = actionBtn.innerHTML;
			actionBtn.innerHTML = '<i class="fa-solid fa-check me-1"></i> Copied Content!';
			actionBtn.className = 'btn btn-outline-success px-4';
			setTimeout(() => {
				actionBtn.innerHTML = savedMarkup;
				actionBtn.className = 'btn btn-success px-4';
			}, 2000);
		})
		.catch(err => {
			alert('System clipboard write operations were rejected.');
			console.error('Clipboard failure: ', err);
		});
}

$(document).ready(function () {
	const baseConfig = {
		pageLength: 25,
		lengthMenu: [[10, 25, 50, -1], [10, 25, 50, 'All']],
		paging: true,
		lengthChange: true,
		searching: true,
		ordering: true,
		info: true,
		autoWidth: false,
		responsive: true,
		initComplete: function () {
			$(this).show();
		}
	};

	const tableConfigs = {
		config: {
			order: [{ idx: 2, dir: 'asc' }, { idx: 1, dir: 'asc' }],
			columnDefs: [
				{ width: '2%', targets: [0] },
				{ width: '5%', targets: [1] },
				{ orderable: false, targets: [0] }
			]
		},
		invites: {
			order: [{ idx: 2, dir: 'asc' }, { idx: 1, dir: 'asc' }],
			columnDefs: [
				{ width: '2%', targets: [0] },
				{ width: '20%', targets: [1, 2, 3] },
				{ width: '15%', targets: [4], orderable: false }
			]
		},
		labels: {
			order: [{ idx: 1, dir: 'asc' }],
			columnDefs: [
				{ width: '2%', targets: [0] },
				{ width: '30%', targets: [1, 2] },
				{ orderable: false, targets: [0] }
			]
		},
		migrations: {
			pageLength: -1,
			order: [[0, 'asc']],
			columnDefs: [
				{ width: '200px', targets: [0, 1] }
			]
		},
		users: {
			pageLength: -1,
			order: { idx: 2, dir: 'asc' },
			columnDefs: [
				{ width: '2%', targets: [0, 1] },
				{ orderable: false, targets: [0] }
			],
			initComplete: function () {
				$(this).addClass('d-some');
			}
		}
	};

	$('table[data-admin-table]').each(function () {
		const type = $(this).data('admin-table');
		if (tableConfigs[type]) {
			$(this).DataTable({ ...baseConfig, ...tableConfigs[type] });
		}
	});

	if ($('#addInviteForm[data-auto-show="true"]').length) {
		$('#addInviteForm').addClass('show');
	}
});

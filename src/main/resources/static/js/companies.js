$(document).ready(function () {
	if ($('#companyTable').length) {
		$('#companyTable').DataTable({
			pageLength: 25,
			lengthMenu: [[10, 25, 50, -1], [10, 25, 50, 'All']],
			paging: true,
			lengthChange: true,
			searching: true,
			ordering: true,
			info: true,
			autoWidth: false,
			order: [[0, 'asc']],
			columnDefs: [
				{ width: '45%', targets: 0 },
				{ width: '35%', targets: 1 },
				{ type: 'num', width: '10%', targets: 2 }
			],
			responsive: true,
			initComplete: function () {
				$(this).show();
			}
		});
	}
});

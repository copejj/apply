$(document).ready(function () {
	$('#pw, #pw_confirm').on('keyup', function () {
		if ($('#pw').val() == $('#pw_confirm').val()) {
			$('#msg').html('Passwords match').css('color', 'green');
			$('#submitBtn').prop('disabled', false);
		} else {
			$('#msg').html('Passwords do not match').css('color', 'red');
			$('#submitBtn').prop('disabled', true);
		}
	});
});

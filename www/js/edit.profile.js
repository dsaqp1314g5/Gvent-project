var API_URL = "http://147.83.7.159:8080/gvent-api/";

var userURL;
$(document).ready(function() {
	userURL = $.cookie('link-user');
	getUser(userURL, function(user){
		var userlog= new User(user);
		$('#name').val(user.name);
		$('#password').val(user.userpass);
		$('#verify').val(user.userpass);
		$('#email').val(user.email);
	});
});

$("#save_btn").click(function(e) {
		e.preventDefault();
		$("#result_edit_process").text('');
		if ($('#password').val() != $('#verify').val()) {

				$('<div class="alert alert-danger">Los passwords no coinciden</div>').appendTo($("#result_edit_process"));
		}else {
				var user = new Object();
				user.username = $.cookie('username');
				user.userpass =  $('#password').val();
				user.name = $('#name').val();
				user.email = $('#email').val();
				updateUser(userURL, 'application/vnd.gvent.api.user+json', JSON	.stringify(user), function(user) {
							window.location.replace("user_profile.html");
				});
		}
});

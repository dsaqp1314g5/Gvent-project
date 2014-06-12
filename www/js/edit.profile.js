var API_URL = "http://localhost:8080/gvent-api/";
var USERNAME = "";
var PASSWORD = "";
// Autenticacion
/*
 * $.ajaxSetup({ headers: { 'Authorization': "Basic "+
 * btoa(USERNAME+':'+PASSWORD) } });
 */

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
				console.log($('#password').val());
				console.log($('#verify').val());

				$('<div class="alert alert-danger">Los passwords no coinciden</div>').appendTo($("#result_edit_process"));
		}else {
				var user = new Object();
				user.username = $.cookie('username');
				user.name = $('#name').val();
				if($('#password').val()==null || $('#password').val()=='' ){
					alert(cookie('pass_editar'));
					user.userpass = $.cookie('pass_editar');
				}else{
					user.userpass = $('#password').val();
				}
				user.email = $('#email').val();
				console.log(user.name);
				console.log($.cookie('username'));
				console.log(user.userpass);
				console.log(user.email);
				console.log(JSON.stringify(user));
				updateUser(userURL, 'application/vnd.gvent.api.user+json', JSON	.stringify(user), function(user) {
							window.location.replace("user_profile.html");
				});
		}
});

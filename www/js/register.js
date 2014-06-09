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
	loadRootAPI(function(rootAPI) {
		userURL = rootAPI.getLink('create-user');
	});
});

$("#register_btn").click(function(e) {
		e.preventDefault();
		$("#result_register_process").text('');
		if ($('#username').val() == "" || $('#name').val() == "" || $('#email').val() == ""	|| $('#password').val() == "" || $('#verify').val() == "") {
				$('<div class="alert alert-danger">Rellena todos los campos por favor </div>').appendTo($("#result_register_process"));
		}else if ($('#password').val() != $('#verify').val()) {
				console.log($('#password').val());
				console.log($('#verify').val());

				$('<div class="alert alert-danger">Los passwords no coinciden</div>').appendTo($("#result_register_process"));
		}else {
				console.log("creando user");
				var user = new Object();
				user.name = $('#name').val();
				user.username = $('#username').val();
				user.userpass = $('#password').val();
				user.email = $('#email').val();
				console.log(user.name);
				console.log(user.username);
				console.log(user.userpass);
				console.log(user.email);
				createUser(userURL.href, userURL.type, JSON	.stringify(user), function(user) {
							window.location.replace("/index.html");
				});
		}
});

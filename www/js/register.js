var API_URL = "http://147.83.7.159:8080/gvent-api/";

var userURL;

$(document).ready(function() {
	loadRootAPI(function(rootAPI) {
		userURL = rootAPI.getLink('create-user');
	});
});

$("#register_btn").click(function(e) {
		e.preventDefault();
		$("#result_register_process").text('');
		if($('#username').val().length > 50 || $('#name').val().length > 50 || $('#email').val().length > 50 || $('#password').val().length > 32){
			$('<div class="alert alert-danger">Algun campo es demasiado largo (Username, name, email : 50 caracteres max. Password: 32 caracteres max</div>').appendTo($("#result_register_process"));
		}else if ($('#username').val() == "" || $('#name').val() == "" || $('#email').val() == ""	|| $('#password').val() == "" || $('#verify').val() == "") {
				$('<div class="alert alert-danger">Rellena todos los campos por favor </div>').appendTo($("#result_register_process"));
		}else if ($('#password').val() != $('#verify').val()) {
				$('<div class="alert alert-danger">Los passwords no coinciden</div>').appendTo($("#result_register_process"));
		}else {
				
				var user = new Object();
				user.name = $('#name').val();
				user.username = $('#username').val();
				user.userpass = $('#password').val();
				user.email = $('#email').val();
				createUser(userURL.href, userURL.type, JSON	.stringify(user), function(user) {
							window.location.replace("/index.html");
				});
		}
});

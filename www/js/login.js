
var AUTH_URL= "http://147.83.7.159:8080/gvent-auth/ServletLogin";

$("#login_btn").click(function(e){
	e.preventDefault();
	Login($("#username_login").val(), $("#pass_login").val() );
	
});

function Login(username, password){
	$("#result_login").text('');
	
	var credentials = 'username='+username+'&password='+password+'';
	
	$.ajax({
		
		url : AUTH_URL,
		type : 'POST',
		crossDomain : true,
		data: credentials,
		dataType: 'html',
		beforeSend: function (request)

		{
		request.setRequestHeader('content-type', 'application/x-www-form-urlencoded');
		},
		success : function(ata, status, jqxhr) {
		var response = jqxhr.responseText;

		if (response=="successadmin")
		{
			url = "http://147.83.7.159:8080/gvent-api/" + 'users/'+ username;
			getUser(url, function(user){
				var userlog= new User(user);
					$.cookie('username', username);
					$.cookie('link-user', userlog.getLink('self').href);
					$.cookie('rol', 'admin');
					window.location.replace("/home.html");
			});

		}
		else if (response=="successusuario"){
		url = "http://147.83.7.159:8080/gvent-api/" + 'users/'+ username;
		getUser(url, function(user){
			var userlog= new User(user);
				$.cookie('username', username);
				$.cookie('link-user', userlog.getLink('self').href);
				$.cookie('rol', 'registered');
				window.location.replace("/home.html");
		});

		}
		else if (response=="wrongpass"){
			$('<div class="alert alert-danger">Password incorrecto</div>').appendTo($("#result_login"));
		}
		else if (response==""){

			$('<div class="alert alert-danger">Usuario no valido</div>').appendTo($("#result_login"));
		}

		},
		//error : function(jqXHR, options, error) {}

		});
}
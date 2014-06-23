
var AUTH_URL= "http://localhost:8080/gvent-auth/ServletLogin";
var USERNAME = "";
var PASSWORD = "";
//Autenticacion
/*$.ajaxSetup({
	headers: { 'Authorization': "Basic "+ btoa(USERNAME+':'+PASSWORD) }
});*/

$("#login_btn").click(function(e){
	e.preventDefault();
	Login($("#username_login").val(), $("#pass_login").val() );
	console.log($("#username_login").val());
	console.log($("#pass_login").val());
	
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
			url = "http://localhost:8080/gvent-api/" + 'users/'+ username;
			getUser(url, function(user){
				var userlog= new User(user);
					$.cookie('username', username);
					$.cookie('link-user', userlog.getLink('self').href);
					$.cookie('rol', 'admin');
					window.location.replace("/home.html");
			});

		}
		else if (response=="successusuario"){
		url = "http://localhost:8080/gvent-api/" + 'users/'+ username;
		getUser(url, function(user){
			var userlog= new User(user);
				$.cookie('username', username);
				$.cookie('link-user', userlog.getLink('self').href);
				$.cookie('rol', 'registered');
				window.location.replace("/home.html");
		});

		}
		else if (response=="wrongpass"){
			$('<div class="alert alert-danger">Contraseña incorrecta</div>').appendTo($("#result_login"));
		}
		else if (response==""){

			$('<div class="alert alert-danger">Usuario no valido</div>').appendTo($("#result_login"));
		}

		},
		//error : function(jqXHR, options, error) {}

		});
}


/*var API_URL= "http://localhost:8080/gvent-api/";
var USERNAME = "";
var PASSWORD = "";
//Autenticacion
/*$.ajaxSetup({
	headers: { 'Authorization': "Basic "+ btoa(USERNAME+':'+PASSWORD) }
});

$("#login_btn").click(function(e){
	e.preventDefault();
	Login($("#username_login").val(), $("#pass_login").val() );
	console.log($("#username_login").val());
	console.log($("#pass_login").val());
	
});

function Login(username, password){
	var url = API_URL + 'users/'+ username;
	console.log(url);
	getUser(url, function(user){
		var userlog= new User(user);
		if(password == userlog.userpass){
			$.cookie('username', username);
			$.cookie('link-user', userlog.getLink('self').href);
			window.location.replace("/home.html");
		}else{
			$('<div class="alert alert-danger">Contraseña incorrecta</div>').appendTo($("#result_login"));
		}
	});
}
*/
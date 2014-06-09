var API_URL= "http://localhost:8080/gvent-api/";
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

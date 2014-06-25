var API_URL= "http://147.83.7.159:8080/gvent-api/";

var eventsURL;
var markers = [];
var iterator = 0;

var usersURL = API_URL + 'users';
$("#search_btn").click(function(e){
	e.preventDefault();
	loadUsersBy(usersURL, $('#search_user').val());
});


$('#logout_btn').click(function(e){
	deleteCookie('username');
});

$(document).ready(function(){

	if($.cookie('username')==undefined){
		window.location.replace("index.html");
	}
	$('<a id="username_loged">'+ $.cookie('username') +'</a>').appendTo($('#user_loged'));
	$('<h1>'+ $.cookie('username') +'</h1>').appendTo($('#username'));
	loadUsersBy(usersURL, "");
	
});

function loadUsersBy(url, username){
	$('#result_users').text('');
	if(username == ""){
		var urlSearch=url;
	}else{
		var urlSearch=url+'/search?username='+username;
	}
	var users = getUsers(urlSearch, function(userCollection){
		$.each(userCollection.users, function(index,item){
			var user = new User(item);

			var link = $('<div class="well well-sm"><div class="media" ><a class="thumbnail pull-left"> <img class="media-object" src="./img/profile.png" height="70" width="70"></a><div class="media-body"><h4 class="media-heading">'+user.username+'</h4><p><a class="btn btn-xs btn-default" id="profile"><span class="glyphicon glyphicon-user"></span>Ver perfil</a></p></div></div></div>');
			link.click(function(e){
				 $.cookie('link-friend',  user.getLink('self').href);
				 window.location.replace("/friend_profile.html");
			});
			
			var div = $('<div></div>');
			div.append(link);
			$('#result_users').append(div);
		});
	});	

}

		   

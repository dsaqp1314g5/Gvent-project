var API_URL= "http://localhost:8080/gvent-api/";
var USERNAME = "";
var PASSWORD = "";
// Autenticacion
/*
 * $.ajaxSetup({ headers: { 'Authorization': "Basic "+
 * btoa(USERNAME+':'+PASSWORD) } });
 */


$(document).ready(function() {
	$('<a id="username_loged">'+ $.cookie('username') +'</a>').appendTo($('#user_loged'));
	console.log($.cookie('username'));
});

var usernameFriend;
$('#logout_btn').click(function(e){
	deleteCookie('username');
});

$('#delete_friend').click(function(e){
	var user = new Object();
	user.username = usernameFriend;
	url = $.cookie('link-user')+'/friends';
	type = 'application/vnd.gvent.api.user+json';
	deleteFriend(url, type, JSON.stringify(user), function(user){
		window.location.reload();
	});
});


$('#add_friend').click(function(e){
	var user = new Object();
	user.username = usernameFriend;
	url = $.cookie('link-user');
	addFriend(url, type, JSON.stringify(user), function(user){
		window.location.reload();
	});
});

$('#delete_user_btn').click(function(e){
	url = $.cookie('link-friend');
	deleteUser(url, function(){
		window.location.replace("/home.html");
	});
});

$(document).ready(function(){

	loadRootAPI(function(rootAPI){
		eventsURL = rootAPI.getLink('events').href;
		//loadEvents(rootAPI.getLink('events').href);
	});
	console.log("el usuario es " + $.cookie('rol'));
	if($.cookie('rol')=='admin'){
		$('#delete_user').show();
	}
	var followedEventsURL=$.cookie('link-friend')+'/events/followed';
	var myEventsURL=$.cookie('link-friend')+'/events';
	var FriendsURL=$.cookie('link-friend')+'/friends';
	var myCommentsURL=$.cookie('link-friend') +'/comments';
	var myURL =$.cookie('link-friend');
	loadMyEvents(myEventsURL);
	loadFriends(FriendsURL);
	loadMyProfile(myURL);
	loadMyFriends($.cookie('link-user')+'/friends');
	loadMyComments(myCommentsURL);
});

function loadMyEvents(url){
	var events = getEvents(url, function(eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			var link = $('<div class="well well-sm"><div class="media" ><div class="media-body"><h4 class="media-heading">'+event.title+'</h4><h6>Followers: '+event.popularity+'</h6><h6>Estado: '+event.state+'</h6><p><a class="btn btn-xs btn-default"><span class="glyphicon glyphicon-map-marker"></span>Ver evento</a></p></div></div></div>');
			link.click(function(e){
				 $.cookie('link-event',  event.getLink("self").href);
				 window.location.replace("/event.html");
			});
			
			var div = $('<div></div>');
			div.append(link);
			$('#result_events').append(div);
		});
		
	});
	
}
function loadFriends(url){
	console.log("hola");
	var users = getUsers(url, function(userCollection){
		console.log("hola2");
		$.each(userCollection.users, function(index,item){
			var user = new User(item);
			console.log(user.name);

			var link = $('<div class="well well-sm"><div class="media" ><a class="thumbnail pull-left"> <img class="media-object" src="./img/profile.png" height="70" width="70"></a><div class="media-body"><h4 class="media-heading">'+user.username+'</h4><p><a class="btn btn-xs btn-default" id="profile"><span class="glyphicon glyphicon-user"></span>Ver perfil</a></p></div></div></div>');
			
			link.click(function(e){
				 $.cookie('link-friend',  user.getLink('self').href);
				 window.location.replace("/friend_profile.html");
			});
			
			var div = $('<div></div>');
			div.append(link);
			$('#result_friends').append(div);
		});
	});	
}

function loadMyFriends(url){
	var users = getUsers(url, function(userCollection){

		var alreadyFriend;
		$.each(userCollection.users, function(index,item){
			var user = new User(item);
			console.log("usernameFriend : " +usernameFriend);
			if(user.username == usernameFriend){
				alreadyFriend = true;
			}
		});

		if(alreadyFriend){
			$('#delete_friend').show();
		}else{
			$('#add_friend').show();
		}
	});	
	
}


function loadMyProfile(url){
	console.log("la url es " + url);
	getUser(url, function(user){
		var date = new Date(user.registerDate);
		var day = date.getDate();
		var month = date.getMonth() + 1;
		var year = date.getFullYear();
		var register_date = day+'/'+month+'/'+year;

		usernameFriend=user.username;
		$('<h1>'+ user.username +'</h1>').appendTo($('#username'));
		$('<li class="list-group-item text-right"><span class="pull-left"><strong>Fecha de registro</strong></span>' + register_date + '</li>').appendTo($('#result_profile'));
		$('<li class="list-group-item text-right"><span class="pull-left"><strong>Nombre</strong></span>' + user.name + '</li>').appendTo($('#result_profile'));
		$('<li class="list-group-item text-right"><span class="pull-left"><strong>E-mail</strong></span>' + user.email + '</li>').appendTo($('#result_profile'));
	});
	

}

function loadMyComments(url){
	var comments = getComments(url, function(commentCollection){
		$.each(commentCollection.comments, function(index, item){
			var comment = new Comment(item);
			var date = new Date(comment.lastModified);
			var hours = date.getHours();
			var minutes = date.getMinutes();
			var seconds = date.getSeconds();
			var day = date.getDate();
			var month = date.getMonth() + 1;
			var year = date.getFullYear();
			var date_format = day+'/'+month+'/'+year+' '+hours+':'+minutes+':'+seconds;
			$('<div class="well well-sm"><div class="media" ><div class="media-body"><class="media-heading">'+comment.comment+'<p><a class="btn btn-xs btn-default pull-right"><span class="glyphicon glyphicon-comment"></span> '+comment.username+'</a><a class="btn btn-xs btn-default pull-right"><span class="glyphicon glyphicon-dashboard"></span> '+date_format+'</a></p></div></div></div>').appendTo($('#result_comments'));
		});
	});
	
}

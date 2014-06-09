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

$("#friends").click(function(e){
	console.log("hola");
	e.preventDefault();
	var myFriendsURL=$.cookie('link-user')+'/friends';
	loadMyFriends(myFriendsURL);
});


$(document).ready(function(){
	loadRootAPI(function(rootAPI){
		eventsURL = rootAPI.getLink('events').href;
		//loadEvents(rootAPI.getLink('events').href);
	});
	;
	var followedEventsURL=$.cookie('link-friend')+'/events/followed';
	var myEventsURL=$.cookie('link-friend')+'/events';
	var myFriendsURL=$.cookie('link-friend')+'/friends';
	var myURL =$.cookie('link-friend');
	loadMyEvents(myEventsURL);
	loadMyFriends(myFriendsURL);
	loadMyProfile(myURL);
});

function loadMyEvents(url){
	var events = getEvents(url, function(eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			$('<tr><td>' + event.title +'</td><td>' + event.category + '</td><td>' + event.popularity + '</td><td>' + event.state +'</td>' ).appendTo($('#result_my_events'));
		});
		
	});
	
}
function loadMyFriends(url){
	var users = getUsers(url, function(userCollection){
		$.each(userCollection.users, function(index,item){
			var user = new User(item);
			console.log(user.name);
			$('<div class="well well-sm"><div class="media" ><a class="thumbnail pull-left"> <img class="media-object" src="./img/error.png" height="70" width="70"></a><div class="media-body"><h4 class="media-heading">'+user.name+'</h4><p><a href="'+user.getLink('self').href+'"class="btn btn-xs btn-default"><span class="glyphicon glyphicon-comment"></span>Ver perfil</a></p></div></div></div>').appendTo($('#result_friends'));
		});
	});	
}

function loadMyProfile(url){
	console.log("hola");
	console.log("la url es " + url);
	getUser(url, function(user){
		var date = new Date(user.registerDate);
		var day = date.getDate();
		var month = date.getMonth() + 1;
		var year = date.getFullYear();
		var register_date = day+'/'+month+'/'+year;
		$('<h1>'+ user.name +'</h1>').appendTo($('#username'));
		$('<li class="list-group-item text-right"><span class="pull-left"><strong>Fecha de registro</strong></span>' + register_date + '</li>').appendTo($('#result_profile'));
		$('<li class="list-group-item text-right"><span class="pull-left"><strong>Nombre</strong></span>' + user.name + '</li>').appendTo($('#result_profile'));
		$('<li class="list-group-item text-right"><span class="pull-left"><strong>E-mail</strong></span>' + user.email + '</li>').appendTo($('#result_profile'));
	});
	

}

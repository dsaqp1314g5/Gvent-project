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
	$('<h1>'+ $.cookie('username') +'</h1>').appendTo($('#username'));
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
	var followedEventsURL=$.cookie('link-user')+'/events/followed';
	var myEventsURL=$.cookie('link-user')+'/events';
	var myFriendsURL=$.cookie('link-user')+'/friends';
	var myURL =$.cookie('link-user');
	loadMyEvents(myEventsURL);
	//loadFollowedEvents(followedEventsURL);
	loadMyFriends(myFriendsURL);
	loadMyProfile(myURL);
});

/*function loadEvents(url){
	var events = getEvents(url, function (eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			var link = $('<a id="event-link" class="list-group-item" href="'+event.getLink("self").href+'">'+event.title +'</a>');
			var commentsURL = event.getLink("comments").href;
			loadComments(commentsURL, event.title, event.getLink("self").href);
			console.log(commentsURL);
			var div = $('<div></div>')
			div.append(link);
			$('#result_last_events').append(div);
		});
	});
}*/

/*function loadComments(url, title, eventURL){
	var comments = getComments(url, function(commentCollection){
		console.log(commentCollection.comments.length);
		if(commentCollection.comments.length != 0){
			$('<br><a href="'+ eventURL + '"><h6>'+ title + '</h6><br>').appendTo($('#result_last_comments'));
		}
		$.each(commentCollection.comments, function(index, item){
			var comment = new Comment(item);
			//$('<a id="comment_id" class="list-group-item">'+ comment.comment +'</a>').appendTo($('#result_last_comments'));
			$('<div class="well well-sm"><div class="media" ><div class="media-body"><class="media-heading">'+comment.comment+'<p><a class="btn btn-xs btn-default pull-right">'+comment.username+'</a></p></div></div></div>').appendTo($('#result_last_comments'));
		});
	});
	
}
*/
function loadMyEvents(url){
	var events = getEvents(url, function(eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			$('<tr><td>' + event.title +'</td><td>' + event.category + '</td><td>' + event.popularity + '</td><td>' + event.state +'</td>' ).appendTo($('#result_my_events'));
		});
		
	});
	
}

/*function loadFollowedEvents(url){
	var events = getEvents(url, function(eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			$('<a id="event_id" class="list-group-item">'+ event.title +'</a>').appendTo($('#result_followed_events'));
		});
		
	});
	
}*/

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
		$('<li class="list-group-item text-right"><span class="pull-left"><strong>Fecha de registro</strong></span>' + register_date + '</li>').appendTo($('#result_profile'));
		$('<li class="list-group-item text-right"><span class="pull-left"><strong>Nombre</strong></span>' + user.name + '</li>').appendTo($('#result_profile'));
		$('<li class="list-group-item text-right"><span class="pull-left"><strong>E-mail</strong></span>' + user.email + '</li>').appendTo($('#result_profile'));
	});
	

}

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

var eventsURL;
var eventName;
$(document).ready(function(){
	loadRootAPI(function(rootAPI){
		eventsURL = rootAPI.getLink('events').href;
		loadEvents(rootAPI.getLink('events').href);
	});
	;
	var followedEventsURL=$.cookie('link-user')+'/events/followed';
	var myEventsURL=$.cookie('link-user')+'/events';
	var myFriendsURL=$.cookie('link-user')+'/friends';
	loadMyEvents(myEventsURL);
	loadFollowedEvents(followedEventsURL);
	loadMyFriends(myFriendsURL);
});

function loadEvents(url){
	var events = getEvents(url, function (eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			var link = $('<a id="event-link" class="list-group-item">'+event.title+'</a>');
			var commentsURL = event.getLink("comments").href;
			//loadComments(commentsURL, event.title, event.getLink("self").href);
			console.log(commentsURL);
			//console.log(event.getLink("create-comment"));
			 link.click(function(e){ 
				 e.preventDefault();
				 $.cookie('link-event',  event.getLink("self").href);
				 window.location.replace("/event.html");
				 
			 });
			var div = $('<div></div>')
			div.append(link);
			$('#result_last_events').append(div);
		});
	});
}

function loadComments(url, title, eventURL){
	var comments = getComments(url, function(commentCollection){
		console.log(commentCollection.comments.length);
		if(commentCollection.comments.length != 0){
			$('<br><a href="'+ eventURL + '"><h6>'+ title + '</h6><br>').appendTo($('#result_last_comments'));
		}
		$.each(commentCollection.comments, function(index, item){
			var comment = new Comment(item);
			$('<div class="well well-sm"><div class="media" ><div class="media-body"><class="media-heading">'+comment.comment+'<p><a class="btn btn-xs btn-default pull-right"><span class="glyphicon glyphicon-comment"></span> '+comment.username+'</a></p></div></div></div>').appendTo($('#result_last_comments'));
		});
	});
	
}

function loadMyEvents(url){
	var events = getEvents(url, function (eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			var link = $('<a id="event-link" class="list-group-item">'+event.title+'</a>');
			 link.click(function(e){ 
				 e.preventDefault();
				 $.cookie('link-event',  event.getLink("self").href);
				 window.location.replace("/event.html");
				 
			 });
			var div = $('<div></div>')
			div.append(link);
			$('#result_my_events').append(div);
		});
	});
	
}

function loadFollowedEvents(url){
	var events = getEvents(url, function (eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			var link = $('<a id="event-link" class="list-group-item">'+event.title+'</a>');
			var commentsURL = event.getLink("comments").href;
			loadComments(commentsURL, event.title, event.getLink("self").href);
			 link.click(function(e){ 
				 e.preventDefault();
				 $.cookie('link-event',  event.getLink("self").href);
				 window.location.replace("/event.html");
				 
			 });
			var div = $('<div></div>')
			div.append(link);
			$('#result_followed_events').append(div);
		});
	});
	
}

function loadMyFriends(url){
	var users = getUsers(url, function(userCollection){
		$.each(userCollection.users, function(index,item){
			var user = new User(item);
			console.log(user.name);

			var link = $('<div class="well well-sm"><div class="media" ><a class="thumbnail pull-left"> <img class="media-object" src="./img/error.png" height="70" width="70"></a><div class="media-body"><h4 class="media-heading">'+user.name+'</h4><p><a class="btn btn-xs btn-default" id="profile"><span class="glyphicon glyphicon-user"></span>Ver perfil</a></p></div></div></div>');
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
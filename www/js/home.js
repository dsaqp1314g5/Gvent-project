var API_URL= "http://localhost:8080/gvent-api/";
var USERNAME = "";
var PASSWORD = "";
// Autenticacion
/*
 * $.ajaxSetup({ headers: { 'Authorization': "Basic "+
 * btoa(USERNAME+':'+PASSWORD) } });
 */


$('#logout_btn').click(function(e){
	deleteCookie('username');
});

var eventsURL;
var followedEventsURL;
var myEventsURL;
var myFriendsURL;
$(document).ready(function(){
	$('<a id="username_loged">'+ $.cookie('username') +'</a>').appendTo($('#user_loged'));
	console.log($.cookie('username'));
	loadRootAPI(function(rootAPI){
		eventsURL = rootAPI.getLink('events').href;
		loadEvents(rootAPI.getLink('events').href);
	});
	loadLinks($.cookie('link-user'));
	//var followedEventsURL=$.cookie('link-user')+'/events/followed';
	//var myEventsURL=$.cookie('link-user')+'/events';
	//var myFriendsURL=$.cookie('link-user')+'/friends';
	console.log("events url " + myEventsURL);
	console.log("followed url " + followedEventsURL);
	console.log("friends url " + myFriendsURL);
	//loadMyEvents(myEventsURL);
	//loadFollowedEvents(followedEventsURL);
	//loadMyFriends(myFriendsURL);
});

function loadLinks(url){
	getUser(url, function(user){
		var userlog= new User(user);
		console.log("obteniendo links");
		console.log(userlog.getLink('followed').href);
		followedEventsURL = userlog.getLink('followed').href;
		myEventsURL = userlog.getLink('events').href;
		myFriendsURL = userlog.getLink('friends').href;
		console.log("he acbado");
		loadMyEvents(myEventsURL);
		loadFollowedEvents(followedEventsURL);
		loadMyFriends(myFriendsURL);
	});
}

function loadEvents(url){
	var events = getEvents(url, function (eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			if(event.state == "Abierto"){
				var link = $('<a id="event-link" class="list-group-item">'+event.title+'</a><div style="background:green;color:white;height:3px;"></div>');
			}else{
				var link = $('<a id="event-link" class="list-group-item">'+event.title+'</a><div style="background:red;color:white;height:3px;"></div>');
			}
			var commentsURL = event.getLink("comments").href;			
			console.log(commentsURL);
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
		/*if(commentCollection.comments.length != 0){
			$('<br><a href="'+ eventURL + '"><h6>'+ title + '</h6><br>').appendTo($('#result_last_comments'));
		}*/
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
			$('<div class="well well-sm"><a class="btn btn-xs btn-default pull-left">'+title+'</a><br><div class="media" ><div class="media-body"><class="media-heading">'+comment.comment+'<p><a class="btn btn-xs btn-default pull-right"><span class="glyphicon glyphicon-comment"></span> '+comment.username+'</a><a class="btn btn-xs btn-default pull-right"><span class="glyphicon glyphicon-dashboard"></span> '+date_format+'</a></p></div></div></div>').appendTo($('#result_last_comments'));
		});
	});
	
}

function loadMyEvents(url){
	var events = getEvents(url, function (eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			if(event.state == "Abierto"){
				var link = $('<a id="event-link" class="list-group-item">'+event.title+'</a><div style="background:green;color:white;height:3px;"></div>');
			}else{
				var link = $('<a id="event-link" class="list-group-item">'+event.title+'</a><div style="background:red;color:white;height:3px;"></div>');
			}
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
			if(event.state == "Abierto"){
				var link = $('<a id="event-link" class="list-group-item">'+event.title+'</a><div style="background:green;color:white;height:3px;"></div>');
			}else{
				var link = $('<a id="event-link" class="list-group-item">'+event.title+'</a><div style="background:red;color:white;height:3px;"></div>');
			}
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

			var link = $('<div class="well well-sm"><div class="media" ><a class="thumbnail pull-left"> <img class="media-object" src="./img/profile.png" height="70" width="70"></a><div class="media-body"><h4 class="media-heading">'+user.name+'</h4><p><a class="btn btn-xs btn-default" id="profile"><span class="glyphicon glyphicon-user"></span>Ver perfil</a></p></div></div></div>');
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

var API_URL= "http://localhost:8080/gvent-api/";
var USERNAME = "";
var PASSWORD = "";
// Autenticacion
/*
 * $.ajaxSetup({ headers: { 'Authorization': "Basic "+
 * btoa(USERNAME+':'+PASSWORD) } });
 */

var eventURL;
var eventID;
var commentsURL;

$("#post_comment").click(function(e){
	postComment();
});

$("#settings_btn").click(function(e){
	window.location.replace("/edit_event.html");
});

$(document).ready(function(){
	eventURL=$.cookie('link-event');
	commentsURL=$.cookie('link-comment');
	$('<a id="username_loged">'+ $.cookie('username') +'</a>').appendTo($('#user_loged'));
	$('<h1>'+ $.cookie('username') +'</h1>').appendTo($('#username'));
	loadRootAPI(function(rootAPI){
		eventsURL = rootAPI.getLink('events').href;
		loadEvent(eventURL);
	});
	
	
	var followedEventsURL=$.cookie('link-user')+'/events/followed';
	var myEventsURL=$.cookie('link-user')+'/events';
	var myFriendsURL=$.cookie('link-user')+'/friends';
	var myURL =$.cookie('link-user');
	//loadMyEvents(myEventsURL);
	loadFollowers(eventURL+"/users");
	//loadMyProfile(myURL);
});

function loadEvent(url){
	getEvent(url, function (event){
		var date = new Date(event.creationDate);
		var day = date.getDate();
		var month = date.getMonth() + 1;
		var year = date.getFullYear();
		var event_date = day+'/'+month+'/'+year;
		var eventID= event.id;
		$('<h3>' + event.owner + '</h3>').appendTo($('#event_owner'));
		$('<h1>' + event.title + '</h1>').appendTo($('#info_event'));
		$('<h2>' + event.category + '</h2>').appendTo($('#info_event'));
		$('<h3>' + event_date + '</h3>').appendTo($('#info_event'));
		$('<h6>' + event.description + '</h6>').appendTo($('#event_description'));
		//console.log(event.getLink());
		if(event.owner ==  $.cookie('username')){
			$('#event_settings').show();
		}
		init_map(event.coordX, event.coordY);
		loadComments(eventURL+'/comments');
	});
}


function init_map(coordX, coordY) {
	var var_location = new google.maps.LatLng(coordX, coordY);

	var var_mapoptions = {
		center : var_location,
		zoom : 14
	};

	var var_marker = new google.maps.Marker({
		position : var_location,
		map : var_map,
		title : "EPSC UPC"
	});

	var var_map = new google.maps.Map(document
			.getElementById("map-container"), var_mapoptions);

	var_marker.setMap(var_map);

	google.maps.event.addDomListener(window, 'load', init_map);
}




function loadComments(url){
	var comments = getComments(url, function(commentCollection){
		console.log(commentCollection.comments.length);
		$.each(commentCollection.comments, function(index, item){
			var comment = new Comment(item);
			$('<hr><div class="well well-sm"><div class="media" ><div class="media-body"><class="media-heading">'+comment.comment+'<p><a class="btn btn-xs btn-default pull-right">'+comment.username+'</a></p></div></div></div>').appendTo($('#result_comments'));
		});
	});
	
}
 
function loadMyEvents(url){
	var events = getEvents(url, function(eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			$('<tr><td>' + event.title +'</td><td>' + event.category + '</td><td>' + event.popularity + '</td><td>' + event.state +'</td>' ).appendTo($('#result_my_events'));
		});
		
	});
	
}

function loadFollowers(url){
	var users = getUsers(url, function(userCollection){
		$.each(userCollection.users, function(index,item){
			var user = new User(item);
			console.log(user.name);
			$('<div class="well well-sm"><div class="media" ><a class="thumbnail pull-left"> <img class="media-object" src="./img/error.png" height="70" width="70"></a><div class="media-body"><h4 class="media-heading">'+user.name+'</h4><p><a href="'+user.getLink('self').href+'"class="btn btn-xs btn-default"><span class="glyphicon glyphicon-comment"></span>Ver perfil</a></p></div></div></div>').appendTo($('#result_followers'));
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

function postComment(){
		
		var comment = new Object();
		comment.username= $.cookie('username');
		comment.comment = $('#comment_text').val();
		comment.eventId = eventID;
		console.log(comment.comment);
		var type = 'application/vnd.gvent.api.comment+json';
		createComment(eventURL+'/comments', type, JSON.stringify(comment), function(comment){
			window.location.reload();
		});
}

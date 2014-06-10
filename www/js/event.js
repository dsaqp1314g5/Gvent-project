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
var lat;
var lng;
var eventTitle;
var link_owner;
$("#post_comment").click(function(e){
	postComment();
});

$("#settings_btn").click(function(e){
	window.location.replace("/edit_event.html");
});

$("#follow_btn").click(function(e){
	joinEvent();
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
		eventTitle=event.title;
		console.log("la coord X es" + event.coordX);
		console.log("la coord Y es" + event.coordY);
		$('<h3>' + event.owner + '</h3>').appendTo($('#event_owner'));
		$('<h1>' + event.title + '</h1>').appendTo($('#info_event'));
		$('<h2>' + event.category + '</h2>').appendTo($('#info_event'));
		$('<h3>' + event_date + '</h3>').appendTo($('#info_event'));
		$('<h6>' + event.description + '</h6>').appendTo($('#event_description'));
		//console.log(event.getLink());
		if(event.owner ==  $.cookie('username')){
			$('#event_settings').show();
		}else{
			$('#event_follow').show();
		}
		lat = event.coordX;
		lng = event.coordY;
		initialize();
		loadComments(eventURL+'/comments');
	});
}

function initialize() {
	var myLatlng = new google.maps.LatLng(lat, lng);
	var mapOptions = {
		zoom: 8,
		center: myLatlng 
	};
	map = new google.maps.Map(document.getElementById('map-canvas'),
    mapOptions);
	
	 var contentString = '<div id="content">'+
      '<div id="siteNotice">'+
      '</div>'+'<div id="bodyContent">'+'<a>Aqui va info del evento</a>'+
      '</div>'+
      '</div>';

	
	var infowindow = new google.maps.InfoWindow({
		content: contentString
	});
	 
	var marker = new google.maps.Marker({
      position: myLatlng,
      map: map,
      title: 'Hello World!'
	});
	
	google.maps.event.addListener(marker, 'click', function() {
		infowindow.open(map,marker);
	});

}

google.maps.event.addDomListener(window, 'load', initialize);


function loadComments(url){
	var comments = getComments(url, function(commentCollection){
		console.log(commentCollection.comments.length);
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
			var link = $('<div class="well well-sm"><div class="media" ><a class="thumbnail pull-left"> <img class="media-object" src="./img/error.png" height="70" width="70"></a><div class="media-body"><h4 class="media-heading">'+user.name+'</h4><p><a class="btn btn-xs btn-default" id="profile"><span class="glyphicon glyphicon-user"></span>Ver perfil</a></p></div></div></div>');
			link.click(function(e){
				 $.cookie('link-friend',  user.getLink('self').href);
				 window.location.replace("/friend_profile.html");
			});
			
			var div = $('<div></div>');
			div.append(link);
			$('#result_followers').append(div);
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

function joinEvent(){
	var user = new Object();
	user.username = $.cookie('username');
	url = 'http://localhost:8080/gvent-api/events/1/users';
	type = 'application/vnd.gvent.api.user+json';
	followEvent(url, type, JSON.stringify(user), function(user){
		window.location.reload();
	});
	
}

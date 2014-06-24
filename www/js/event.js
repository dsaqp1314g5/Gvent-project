var API_URL= "http://localhost:8080/gvent-api/";

var eventURL;
var eventID;
var commentsURL;
var lat;
var lng;
var eventTitle;
var link_owner;
$("#post_comment").click(function(e){
	e.preventDefault();
	postComment();
});

$("#settings_btn").click(function(e){
	e.preventDefault();
	window.location.replace("/edit_event.html");
});

$("#follow_btn").click(function(e){
	e.preventDefault();
	joinEvent();
}); 

$("#unfollow_btn").click(function(e){
	e.preventDefault();
	leaveEvent();
}); 

$('#logout_btn').click(function(e){
	e.preventDefault();
	deleteCookie('username');
});

$('#delete_event_btn').click(function(e){
	e.preventDefault();
	deleteUser(eventURL, function(){
		window.location.replace("/home.html");
	});
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
	loadFollowers(eventURL+"/users");
	
});

function loadEvent(url){
	getEvent(url, function (event){
		var date = new Date(event.eventDate);
		var day = date.getDate();
		var month = date.getMonth() + 1;
		var year = date.getFullYear();
		var event_date = day+'/'+month+'/'+year;
		var eventID= event.id;
		$.cookie('popularity', event.popularity);
		eventTitle=event.title;
		var owner = event.owner;
		$('<h3>' + event.owner + '</h3><br><br><br><br>').appendTo($('#event_owner'));
		$('<h1>' + event.title + '</h1>').appendTo($('#info_event'));
		$('<h2>' + event.category + '</h2>').appendTo($('#info_event'));
		$('<h4>' + event_date + '</h4>').appendTo($('#info_event'));
		$('<h6>' + event.description + '</h6>').appendTo($('#info_event'));
		if(event.owner.toUpperCase() ==  $.cookie('username').toUpperCase()){
			$('#event_settings').show();
		}

		if($.cookie('rol')=='admin' || $.cookie('username').toUpperCase()==owner.toUpperCase()){
			$('#delete_event').show();
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

function loadFollowers(url){
	var users = getUsers(url, function(userCollection){
		var following;
		$.each(userCollection.users, function(index,item){
			var user = new User(item);
			var link = $('<div class="well well-sm"><div class="media" ><a class="thumbnail pull-left"> <img class="media-object" src="./img/profile.png" height="70" width="70"></a><div class="media-body"><h4 class="media-heading">'+user.username+'</h4><p><a class="btn btn-xs btn-default" id="profile"><span class="glyphicon glyphicon-user"></span>Ver perfil</a></p></div></div></div>');
			link.click(function(e){
				 $.cookie('link-friend',  user.getLink('self').href);
				 window.location.replace("/friend_profile.html");
			});
			
			if(user.username.toUpperCase() == $.cookie("username").toUpperCase()){
				following = true;
			}
			
			var div = $('<div></div>');
			div.append(link);
			$('#result_followers').append(div);
		});
		
		if(following){
			$('#event_follow').hide();
			$('#event_unfollow').show();
		}else{
			$('#event_follow').show();
			$('#event_unfollow').hide();
		}
	});	
}

function loadMyProfile(url){
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
		var type = 'application/vnd.gvent.api.comment+json';
		createComment(eventURL+'/comments', type, JSON.stringify(comment), function(comment){
			window.location.reload();
		});
}

function joinEvent(){
	var user = new Object();
	user.username = $.cookie('username');
	url = $.cookie('link-event')+'/users';
	type = 'application/vnd.gvent.api.user+json';
	followEvent(url, type, JSON.stringify(user), function(user){
		var event = new Object();
		event.popularity = parseInt($.cookie('popularity')) + 1;
		updateEvent($.cookie('link-event'), 'application/vnd.gvent.api.event+json', JSON.stringify(event), function(event){
			window.location.reload();
		});
	});
	
	
}

function leaveEvent(){
	var user = new Object();
	user.username = $.cookie('username');
	url = $.cookie('link-event')+'/users';
	type = 'application/vnd.gvent.api.user+json';
	unfollowEvent(url, type, JSON.stringify(user), function(user){
		var event = new Object();
		event.popularity = parseInt($.cookie('popularity')) - 1;
		updateEvent($.cookie('link-event'), 'application/vnd.gvent.api.event+json', JSON.stringify(event), function(event){
			window.location.reload();
		});
	});
}


var API_URL= "http://localhost:8080/gvent-api/";
var USERNAME = "";
var PASSWORD = "";
// Autenticacion
/*
 * $.ajaxSetup({ headers: { 'Authorization': "Basic "+
 * btoa(USERNAME+':'+PASSWORD) } });
 */

var eventsURL;

$("#search_btn").click(function(e){
	console.log("search");
	console.log("link " + eventsURL);
	console.log("title: " + $('#search_event').val());
	loadEventsBy(eventsURL, $('#search_event').val());
});


$('#logout_btn').click(function(e){
	deleteCookie('username');
});

$(document).ready(function(){

	$('<a id="username_loged">'+ $.cookie('username') +'</a>').appendTo($('#user_loged'));
	$('<h1>'+ $.cookie('username') +'</h1>').appendTo($('#username'));
	console.log($.cookie('username'));
	loadRootAPI(function(rootAPI){
		eventsURL = rootAPI.getLink('events').href;
		loadEvents(rootAPI.getLink('events').href);
	});
	;
	var followedEventsURL=$.cookie('link-user')+'/events/followed';
	var myEventsURL=$.cookie('link-user')+'/events';
	var myFriendsURL=$.cookie('link-user')+'/friends';
	var myURL =$.cookie('link-user');
	loadMyFriends(myFriendsURL);
	loadMyProfile(myURL);
});

function loadEvents(url){
	var events = getEvents(url, function (eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			$('<tr><td>'+event.id +'</td><td>' + event.title +'</td><td>' + event.category + '</td><td>' + event.popularity + '</td><td>' + event.state +'</td>').appendTo($('#result_events'));
		});
	});
}

function loadEventsBy(url, title){
	var events = getEvents(url+'/search?title='+title, function (eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			$('<tr><td>'+event.id +'</td><td>' + event.title +'</td><td>' + event.category + '</td><td>' + event.popularity + '</td><td>' + event.state +'</td>').appendTo($('#result_events'));
		});
	});
}

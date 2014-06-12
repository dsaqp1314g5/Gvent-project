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
	e.preventDefault();
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
	});
	;
});
function loadEventsBy(url, title){
	$('#result_events').text('');
	var events = getEvents(url+'/search?title='+title, function(eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			console.log(event);
			alert(event.popularity);
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

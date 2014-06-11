var API_URL= "http://localhost:8080/gvent-api/";
var USERNAME = "";
var PASSWORD = "";
// Autenticacion
/*
 * $.ajaxSetup({ headers: { 'Authorization': "Basic "+
 * btoa(USERNAME+':'+PASSWORD) } });
 */




$(document).ready(function(){
		if($.cookie('username')!=null){
			window.location.replace("/home.html");
		}else{
			console.log("cookie : " +$.cookie('username'));
			loadRootAPI(function(rootAPI){
			loadEvents(rootAPI.getLink('events').href);
			// loadPopularEvents(rootAPI.getLink('events').href);
			});
		}
});

function loadEvents(url){
	var events = getEvents(url, function (eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			var link = $('<a id="event-link" class="list-group-item">'+event.title +'</a>');
			/*
			 * link.click(function(e){ e.preventDefault();
			 * loadEvent($(e.target).attr('href')); return false; });
			 */
			var div = $('<div></div>')
			div.append(link);
			$('#result_last_events').append(div);
		});
	});
}

function loadPopularEvents(url, tipo){
	var search = 'http://localhost:8080/gvent-api/events?sort=popular';
	console.log(search);
	var events = getEvents(search, function (eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			var link = $('<a id="event-link" class="list-group-item">'+event.title +'</a>');
			/*
			 * link.click(function(e){ e.preventDefault();
			 * loadEvent($(e.target).attr('href')); return false; });
			 */
			var div = $('<div></div>')
			div.append(link);
			$('#result_popular_events').append(div);
		});
	});
}




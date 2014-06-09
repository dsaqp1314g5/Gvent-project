var API_URL= "http://localhost:8080/gvent-api/";
var USERNAME = "";
var PASSWORD = "";
//Autenticacion
/*$.ajaxSetup({
	headers: { 'Authorization': "Basic "+ btoa(USERNAME+':'+PASSWORD) }
});*/


var eventsURL;

$(document).ready(function(){
	loadRootAPI(function(rootAPI){
		eventsURL = rootAPI.getLink('events').href;
		loadEvents(rootAPI.getLink('events').href);
	});
});

function loadEvents(url){
	var events = getEvents(url, function (eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			var link = $('<a id="event-link" class="list-group-item" href="'+event.getLink("self").href+'">'+event.title +'</a>');
			/*link.click(function(e){
				e.preventDefault();
				loadEvent($(e.target).attr('href'));
				return false;
			});*/
			var div = $('<div></div>')
			div.append(link);
			$('#result_last_events').append(div);
		});
	});
}


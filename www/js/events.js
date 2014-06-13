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
	loadEventsBy(eventsURL, $('#search_event').val(), document.getElementById("select_category").value);
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
		loadEventsBy(eventsURL, "", "");
	});
	;
});
function loadEventsBy(url, title, category){
	$('#result_events').text('');
	if(title =="" && category ==""){
		var urlSearch=url+'/search?title='+title+'&category='+category;
	}else if(category=="" && title!=""){
		var urlSearch=url+'/search?title='+title;
	}else if(title=="" && category!=""){
		var urlSearch=url+'/search?category='+category;
	}else{
		var urlSearch=url+'/search?title='+title+'&category='+category;
	}
	console.log(urlSearch);
	var events = getEvents(urlSearch, function(eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			console.log(event);
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

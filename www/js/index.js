var API_URL= "http://localhost:8080/gvent-api/";
var USERNAME = "";
var PASSWORD = "";
// Autenticacion
/*
 * $.ajaxSetup({ headers: { 'Authorization': "Basic "+
 * btoa(USERNAME+':'+PASSWORD) } });
 */




$(document).ready(function(){
		if($.cookie('username')!=undefined){
			window.location.replace("/home.html");
		}else{
			//console.log("cookie : " +$.cookie('username'));
			loadRootAPI(function(rootAPI){
			loadLastEvents(rootAPI.getLink('events').href + '?sort=last');
			loadPopularEvents(rootAPI.getLink('events').href +'?sort=popular');
			});
		}
});

function loadLastEvents(url){
	var events = getEvents(url, function (eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			var link = $('<div class="well well-sm"><div class="media" ><div class="media-body"><h6 class="media-heading"><strong>'+event.title+'</strong></h6><h7>'+event.description+'</h7></div></div></div>');
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

function loadPopularEvents(url){
	var events = getEvents(url, function (eventCollection){
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			var link = $('<div class="well well-sm"><div class="media" ><div class="media-body"><h6 class="media-heading"><strong>'+event.title+'</strong></h6><h7>'+event.description+'</h7></div></div></div>');
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




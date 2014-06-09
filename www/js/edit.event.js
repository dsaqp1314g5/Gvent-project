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


$('#save_settings').click(function(e){
	var event = new Object();
	event.title = $('#event_title').val();
	event.description = $('#event_description').val();
	event.coordX = $('#event_coordX').val();
	event.coordY = $('#event_coordY').val();
	//event.eventDate = $('#event_date');
	var type = 'application/vnd.gvent.api.event+json';
	updateEvent(eventURL, type, JSON.stringify(event), function(event){
		console.log("exitooooooooooooooooooooooooooooo");
		window.location.replace("/event.html");
	});
});

$(document).ready(function(){
	eventURL=$.cookie('link-event');
	$('<a id="username_loged">'+ $.cookie('username') +'</a>').appendTo($('#user_loged'));
	console.log($.cookie('username'));
	console.log(eventURL);
	loadRootAPI(function(rootAPI){
		eventsURL = rootAPI.getLink('events').href;
		loadEvent(eventURL);
	});
	
});

function loadEvent(url){
	console.log("cargando evento");
	getEvent(url, function (event){
		console.log("obteniendo evento");
		/*var date = new Date(event.creationDate);
		var day = date.getDate();
		var month = date.getMonth() + 1;
		var year = date.getFullYear();
		var event_date = year+'/'+month+'/'+day;*/
		var eventID= event.id;
		$('#event_title').val(event.title);
		$('#event_description').text(event.description);
		$('#event_coordX').val(event.coordX);
		$('#event_coordY').val(event.coordY);
		$('#event_date').val(event.eventDate);
		//init_map(event.coordX, event.coordY);
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


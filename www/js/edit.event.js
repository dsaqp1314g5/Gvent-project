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

$('#save_settings').click(function(e){
	e.preventDefault();
	console.log("ESTOY AQUI");
	var event = new Object();
	event.title = $('#event_title').val();
	event.description = $('#event_description').val();
	event.coordX = $('#event_coordX').val();//'-34.29806835099083';
	event.coordY = $('#event_coordY').val();//'147.94464111328125';
	event.eventDate = $('#event_date').val();
	//console.log(document.getElementById("select_category").value);
	event.category = document.getElementById("select_category").value;
	//alert("X " + event.coordX + " Y " +event.coordY );
	var type = 'application/vnd.gvent.api.event+json';
	//console.log("llego");
	updateEvent(eventURL, type, JSON.stringify(event), function(event){
		console.log("exitooooooooooooooooooooooooooooo");
		window.location.replace("event.html");
	});
});


$('#logout_btn').click(function(e){
	deleteCookie('username');
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
		var eventID= event.id;
		$('#event_title').val(event.title);
		$('#event_description').text(event.description);
		$('#event_coordX').val(event.coordX);
		$('#event_coordY').val(event.coordY);
		$('#event_date').val(event.eventDate);
		document.getElementById("select_category").value=event.category;
		lat = event.coordX;
		lng = event.coordY;
		initialize();
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
	  map = new google.maps.Map(document.getElementById('map-canvas'),
	      mapOptions);

	  
	  google.maps.event.addListener(map, "rightclick", function(event) {
		    var lat = event.latLng.lat();
		    var lng = event.latLng.lng();
		    var myLatlng = new google.maps.LatLng(lat,lng);
		    
		    
		    var marker = new google.maps.Marker({
		        position: myLatlng,
		        map: map,
		        title: 'Nuevo evento'
		    });

		    $('#event_coordX').val(lat);
		    $('#event_coordY').val(lng);
		});
	}

	google.maps.event.addDomListener(window, 'load', initialize);



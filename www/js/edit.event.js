var API_URL= "http://localhost:8080/gvent-api/";

var eventURL;
var eventID;
var commentsURL;
var lat;
var lng;

$('#save_settings').click(function(e){
	e.preventDefault();
	$('#result_create').text('');
	if ($('#event_title').val() == "" || $('#event_coordX').val() == "" || $('#event_date').val() == ""	) {
		$('<div class="alert alert-danger">Rellena todos los campos obligatorios por favor </div>').appendTo($("#result_edit"));
	}else{
		var event = new Object();
		event.title = $('#event_title').val();
		event.description = $('#event_description').val();
		event.coordX = $('#event_coordX').val();
		event.coordY = $('#event_coordY').val();
		event.eventDate = $('#event_date').val();
		event.state =  document.getElementById("select_state").value;
		event.category = document.getElementById("select_category").value;
		var type = 'application/vnd.gvent.api.event+json';
		updateEvent(eventURL, type, JSON.stringify(event), function(event){
			window.location.replace("event.html");
		});
	}
});


$('#logout_btn').click(function(e){
	deleteCookie('username');
});

$(document).ready(function(){
	eventURL=$.cookie('link-event');
	$('<a id="username_loged">'+ $.cookie('username') +'</a>').appendTo($('#user_loged'));

	
	loadRootAPI(function(rootAPI){
		eventsURL = rootAPI.getLink('events').href;
		loadEvent(eventURL);
	});
	
});

function loadEvent(url){
	getEvent(url, function (event){
		var eventID= event.id;
		$('#event_title').val(event.title);
		$('#event_description').text(event.description);
		$('#event_coordX').val(event.coordX);
		$('#event_coordY').val(event.coordY);
		$('#event_date').val(event.eventDate);
		document.getElementById("select_category").value=event.category;
		document.getElementById("select_state").value=event.state;
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



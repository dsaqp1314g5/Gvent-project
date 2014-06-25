var API_URL= "http://147.83.7.159:8080/gvent-api/";

var eventURL;
var eventID;
var commentsURL;
var late;
var lnge;
var markers =[];

$('#save_settings').click(function(e){
	e.preventDefault();
	var datePattern = /^\d{2,4}\-\d{1,2}\-\d{1,2}$/;
	$('#result_edit').text('');

	if($('#event_title').val().length > 50 ||$('#event_description').val().length > 500){
		$('<div class="alert alert-danger">El titulo o descripcion son demasiado largos (50 y 500 caracteres)</div>').appendTo($("#result_edit"));
	}else if ($('#event_title').val() == "" || $('#event_coordX').val() == "" || $('#event_date').val() == ""	) {
		$('<div class="alert alert-danger">Rellena todos los campos obligatorios por favor </div>').appendTo($("#result_edit"));
	}else if(!$('#event_date').val().match(datePattern)){
		$('<div class="alert alert-danger">El formato de la fecha no es el adecuado (YYYY-MM-DD) </div>').appendTo($("#result_edit"));
	}else{
		var event = new Object();
		event.title = $('#event_title').val();
		event.description = $('#event_description').val();
		event.coordX = $('#event_coordX').val();
		event.coordY = $('#event_coordY').val();
		event.eventDate = $('#event_date').val();
		event.state =  document.getElementById("select_state").value;
		event.category = document.getElementById("select_category").value;
		event.popularity = $.cookie('popularity');
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
	if($.cookie('username')==undefined){
		window.location.replace("index.html");
	}
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
		$.cookie('popularity', event.popularity);
		document.getElementById("select_category").value=event.category;
		document.getElementById("select_state").value=event.state;
		late = event.coordX;
		lnge = event.coordY;
		initialize();
	});
}


function initialize() {
	  var mapOptions = {
	    zoom: 15
	  };
	  map = new google.maps.Map(document.getElementById('map-canvas'),
	      mapOptions);

	  // Try HTML5 geolocation
	  if(navigator.geolocation) {
	    navigator.geolocation.getCurrentPosition(function(position) {
	      var pos = new google.maps.LatLng(late,
	                                       lnge);

	      var infowindow = new google.maps.InfoWindow({
	        map: map,
	        position: pos,
	        content: 'Coordenadas actuales'
	      });

	      map.setCenter(pos);
	    }, function() {
	      handleNoGeolocation(true);
	    });
	  } else {
	    // Browser doesn't support Geolocation
	    handleNoGeolocation(false);
	  }
	  
	  google.maps.event.addListener(map, "rightclick", function(event) {
		    var lat = event.latLng.lat();
		    var lng = event.latLng.lng();
		    var myLatlng = new google.maps.LatLng(lat,lng);
		    deleteMarkers();
		    addMarker(myLatlng);
		    
		    $('#event_coordX').val(lat);
		    $('#event_coordY').val(lng);
		});
	}

	function handleNoGeolocation(errorFlag) {
	  if (errorFlag) {
	    var content = 'Error: The Geolocation service failed.';
	  } else {
	    var content = 'Error: Your browser doesn\'t support geolocation.';
	  }

	  var options = {
	    map: map,
	    position: new google.maps.LatLng(60, 105),
	    content: content
	  };

	  var infowindow = new google.maps.InfoWindow(options);
	  map.setCenter(options.position);
	}

	function addMarker(myLatlng) {
		  markers.push(new google.maps.Marker({
		    position: myLatlng,
		    map: map,
		    draggable: false,
	        title: 'Nuevo evento'
		  }));
		}

		function setAllMap(map) {
			  for (var i = 0; i < markers.length; i++) {
			    markers[i].setMap(map);
			  }
			}

		function clearMarkers() {
			  setAllMap(null);
			}

		function showMarkers() {
			  setAllMap(map);
			}

		function deleteMarkers() {
			  clearMarkers();
			  markers = [];
			}

	google.maps.event.addDomListener(window, 'load', initialize);


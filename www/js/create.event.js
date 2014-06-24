var map;
var eventsURL;
var markers =[];
$(document).ready(function(){
	$('<a id="username_loged">'+ $.cookie('username') +'</a>').appendTo($('#user_loged'));
	initialize();
	loadRootAPI(function(rootAPI){
		eventsURL = rootAPI.getLink('create-event');
	});
});

$('#create_btn').click(function(e){
	e.preventDefault();
	$('#result_create').text('');
	if ($('#event_title').val() == "" || $('#event_coordX').val() == "" || $('#event_date').val() == ""	) {
		$('<div class="alert alert-danger">Rellena todos los campos obligatorios por favor </div>').appendTo($("#result_create"));
	}else{
		createEvent2();
	}
});


$('#logout_btn').click(function(e){
	deleteCookie('username');
});

function initialize() {
  var mapOptions = {
    zoom: 15
  };
  map = new google.maps.Map(document.getElementById('map-canvas'),
      mapOptions);

  // Try HTML5 geolocation
  if(navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(function(position) {
      var pos = new google.maps.LatLng(position.coords.latitude,
                                       position.coords.longitude);

      var infowindow = new google.maps.InfoWindow({
        map: map,
        position: pos,
        content: 'Estas aqui'
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


function createEvent2(){
	var event = new Object();
	event.title = $('#event_title').val();
	event.coordX = $('#event_coordX').val();
	event.coordY = $('#event_coordY').val();
	event.category = document.getElementById("select_category").value;
	event.description = $('#event_description').val();
	event.owner = $.cookie('username');
	event.state = "Abierto";
	event.publicEvent = true;
	event.eventDate = $('#event_date').val();
	event.popularity = 0;
	createEvent(eventsURL.href, eventsURL.type, JSON.stringify(event), function(event){
		window.location.replace("home.html");
	});
}
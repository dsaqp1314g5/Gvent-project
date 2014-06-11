var map;
var eventsURL;
$(document).ready(function(){
	$('<a id="username_loged">'+ $.cookie('username') +'</a>').appendTo($('#user_loged'));
	initialize();
	loadRootAPI(function(rootAPI){
		eventsURL = rootAPI.getLink('events');
	});
});

$('#create_btn').click(function(e){
	createEvent();
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
	    
	    
	    var marker = new google.maps.Marker({
	        position: myLatlng,
	        map: map,
	        title: 'Nuevo evento'
	    });

	    $('#coordX').val(lat);
	    $('#coordY').val(lng);
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

google.maps.event.addDomListener(window, 'load', initialize);


function createEvent(){

	var event = new Object();
	event.title = $('#event_title').val();
	event.description = $('#event_description').text();
	event.coordX = $('#event_coordX').val();
	event.coordY = $('#event_coordY').val();
	//var position=document.getElementById('category').options.selectedIndex; 
	event.category = "deportes"; //document.getElementById('category').options[position].text;
	event.owner = $.cookie('username');
	event.state = "Abierto";
	event.publicEvent = true;
	event.date = "2014-09-08";//$('#event_date').val();
	event.popularity = 0;
	event.puntuation = 0;
	event.votes = 0;
	console.log(eventsURL.type);
	createEvent(eventsURL.href, eventsURL.type, JSON.stringify(event), function(event){
		console.log("exitooooooooooooooooooooooooooooo");
		window.location.replace("/home.html");
	});
}
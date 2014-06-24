var API_URL= "http://localhost:8080/gvent-api/";

var eventsURL;
var markers = [];
var iterator = 0;
$("#search_btn").click(function(e){
	e.preventDefault();
	loadEventsBy(eventsURL, $('#search_event').val(), document.getElementById("select_category").value);
});


$('#logout_btn').click(function(e){
	deleteCookie('username');
});

$(document).ready(function(){

	$('<a id="username_loged">'+ $.cookie('username') +'</a>').appendTo($('#user_loged'));
	$('<h1>'+ $.cookie('username') +'</h1>').appendTo($('#username'));
	loadRootAPI(function(rootAPI){
		eventsURL = rootAPI.getLink('events').href;
		loadEventsBy(eventsURL, "", "");
	});
	;
});
function loadEventsBy(url, title, category){
	deleteMarkers();
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
	var events = getEvents(urlSearch, function(eventCollection){
		var neighborhoods = new Array();
		$.each(eventCollection.events, function(index,item){
			var event = new Event(item);
			neighborhoods.push(new google.maps.LatLng(event.coordX, event.coordY));
			var link = $('<div class="well well-sm"><div class="media" ><div class="media-body"><h4 class="media-heading">'+event.title+'</h4><h6>Followers: '+event.popularity+'</h6><h6>Estado: '+event.state+'</h6><p><a class="btn btn-xs btn-default"><span class="glyphicon glyphicon-map-marker"></span>Ver evento</a></p></div></div></div>');
			link.click(function(e){
				 $.cookie('link-event',  event.getLink("self").href);
				 window.location.replace("/event.html");
			});
			
			var div = $('<div></div>');
			div.append(link);
			$('#result_events').append(div);
		});
		initialize(neighborhoods);
	});
	

}

function initialize(neighborhoods) {
  var mapOptions = {
    zoom: 9
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
        position: pos
      });

      map.setCenter(pos);
    }, function() {
      handleNoGeolocation(true);
    });
  } else {
    // Browser doesn't support Geolocation
    handleNoGeolocation(false);
  }
  
  drop(neighborhoods);
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

function drop(neighborhoods) {
  for (var i = 0; i < neighborhoods.length; i++) {
    setTimeout(function() {
      addMarker(neighborhoods);
    }, i * 200);
  }
}

function addMarker(neighborhoods) {
	
  markers.push(new google.maps.Marker({
    position: neighborhoods[iterator],
    map: map,
    draggable: false,
    animation: google.maps.Animation.DROP
  }));
  iterator++;
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
		    
		   

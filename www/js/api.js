var GVENT_API_HOME="http://localhost:8080/gvent-api";

function deleteCookie( name ) {
	  document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
	}

function Link(rel, linkheader){
	this.rel = rel;
	this.href = decodeURIComponent(linkheader.find(rel).template().template);
	this.type = linkheader.find(rel).attr('type');
	this.title = linkheader.find(rel).attr('title');
}

function buildLinks(linkheaders){
	var links = {};
	$.each(linkheaders, function(i,linkheader){
		var linkhdr = $.linkheaders(linkheader);
		var rels = linkhdr.find().attr('rel').split(' ');
		$.each(rels, function(key,rel){
			var link = new Link(rel, linkhdr);
			links[rel] = link;
		});
	});

	return links;
}


function RootAPI(rootAPI){
	this.links = buildLinks(rootAPI.links);
	var instance = this;
	this.getLink = function(rel){
		return this.links[rel];
	}
}


function loadRootAPI(success){
	$.ajax({
		url : GVENT_API_HOME,
		type : 'GET',
		crossDomain : true,
		dataType: 'json'
	})
	.done(function (data, status, jqxhr) {
		var response = $.parseJSON(jqxhr.responseText);
		var rootAPI = new RootAPI(response);
    	success(rootAPI);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
	
}

// EVENTS

function Event(event){
	this.id = event.id;
	this.title = event.title;
	this.coordX = event.coordX;
	this.coordY = event.coordY;
	this.category = event.category;
	this.description = event.description;
	this.owner = event.owner;
	this.state = event.state;
	this.publicEvent = event.publicEvent;
	this.creationDate = event.creationDate;
	this.eventDate = event.eventDate;
	this.popularity = event.popularity;
	this.puntuation = event.puntuation;
	this.votes = event.votes;
	this.links = buildLinks(event.links);
	
	var instance = this;
	this.getLink = function(rel){
		return this.links[rel];
	}
}

function EventCollection(eventCollection){

	this.oldestTimestamp = eventCollection.oldestTimestamp;
	this.newestTimestamp = eventCollection.newestTimestamp;
	this.events = eventCollection.events;

	this.links = buildLinks(eventCollection.links);
	var instance = this;
	this.getLink = function(rel){
		return this.links[rel];
	}
}


function getEvents(url, success){
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType: 'json'
	})
	.done(function (data, status, jqxhr) {
		var response = $.parseJSON(jqxhr.responseText);
		var eventCollection = new EventCollection(response);
		//success(response.stings);
		success(eventCollection);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}


function createEvent(url, type, event, success){
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		contentType: type, 
		data: event
	})
	.done(function (data, status, jqxhr) {
		var event = $.parseJSON(jqxhr.responseText);
		success(event);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function followEvent(url, type, user, success){

	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		contentType: type, 
		data: user
	})
	.done(function (data, status, jqxhr) {
		//var user = $.parseJSON(jqxhr.responseText);
		success();
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function getEvent(url, success){
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType: 'json'
	}).done(function (data, status, jqxhr) {
		var event = $.parseJSON(jqxhr.responseText);
		success(event);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function updateEvent(url, type, event, success){
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		contentType: type, 
		data: event
	})
	.done(function (data, status, jqxhr) {
		var event = $.parseJSON(jqxhr.responseText);
		success(event);
		//console.log("SUCCESS ON PUT!!");
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function deleteEvent(url, success){
	$.ajax({
		url : url,
		type : 'DELETE',
		crossDomain : true
	})
	.done(function (data, status, jqxhr) {
		//var sting = $.parseJSON(jqxhr.responseText);
		success();
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function leaveEvent(url, success){
	$.ajax({
		url : url,
		type : 'DELETE',
		crossDomain : true
	})
	.done(function (data, status, jqxhr) {
		//var sting = $.parseJSON(jqxhr.responseText);
		success();
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

//USERS

function User(user){
	this.username = user.username;
	this.userpass = user.userpass;
	this.name = user.name;
	this.email = user.email;
	this.registerDate = user.registerDate;
	this.links = buildLinks(user.links);
	
	var instance = this;
	this.getLink = function(rel){
		return this.links[rel];
	}
}

function UserCollection(userCollection){

	this.oldestTimestamp = userCollection.oldestTimestamp;
	this.newestTimestamp = userCollection.newestTimestamp;
	this.users = userCollection.users;

	this.links = buildLinks(userCollection.links);
	var instance = this;
	this.getLink = function(rel){
		return this.links[rel];
	}
}


function getUsers(url, success){
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType: 'json'
	})
	.done(function (data, status, jqxhr) {
		var response = $.parseJSON(jqxhr.responseText);
		var userCollection = new UserCollection(response);
		//success(response.stings);
		success(userCollection);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}


function createUser(url, type, user, success){
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		contentType: type, 
		data: user
	})
	.done(function (data, status, jqxhr) {
		var user = $.parseJSON(jqxhr.responseText);
		success(user);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function getUser(url, success){
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType: 'json'
	}).done(function (data, status, jqxhr) {
		var user = $.parseJSON(jqxhr.responseText);
		success(user);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}



function updateUser(url, type, user, success){
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		contentType: type, 
		data: user
	})
	.done(function (data, status, jqxhr) {
		var user = $.parseJSON(jqxhr.responseText);
		success(user);
		//console.log("SUCCESS ON PUT!!");
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function deleteUser(url, success){
	$.ajax({
		url : url,
		type : 'DELETE',
		crossDomain : true
	})
	.done(function (data, status, jqxhr) {
		//var sting = $.parseJSON(jqxhr.responseText);
		success();
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}


//// COMMENTS

function Comment(comment){
	this.id = comment.id;
	this.username = comment.username;
	this.eventId = comment.eventId;
	this.comment = comment.comment;
	this.lastModified = comment.lastModified;
	this.links = buildLinks(comment.links);
	
	var instance = this;
	this.getLink = function(rel){
		return this.links[rel];
	}
}

function CommentCollection(commentCollection){

	this.oldestTimestamp = commentCollection.oldestTimestamp;
	this.newestTimestamp = commentCollection.newestTimestamp;
	this.comments = commentCollection.comments;

	this.links = buildLinks(commentCollection.links);
	var instance = this;
	this.getLink = function(rel){
		return this.links[rel];
	}
}


function getComments(url, success){
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType: 'json'
	})
	.done(function (data, status, jqxhr) {
		var response = $.parseJSON(jqxhr.responseText);
		var commentCollection = new CommentCollection(response);
		//success(response.stings);
		success(commentCollection);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}


function createComment(url, type, comment, success){
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		contentType: type, 
		data: comment
	})
	.done(function (data, status, jqxhr) {
		var comment = $.parseJSON(jqxhr.responseText);
		success(comment);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function getComment(url, success){
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType: 'json'
	}).done(function (data, status, jqxhr) {
		var comment = $.parseJSON(jqxhr.responseText);
		success(comment);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}


function updateComment(url, type, comment, success){
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		contentType: type, 
		data: comment
	})
	.done(function (data, status, jqxhr) {
		var comment = $.parseJSON(jqxhr.responseText);
		success(comment);
		//console.log("SUCCESS ON PUT!!");
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function deleteComment(url, success){
	$.ajax({
		url : url,
		type : 'DELETE',
		crossDomain : true
	})
	.done(function (data, status, jqxhr) {
		//var sting = $.parseJSON(jqxhr.responseText);
		success();
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}


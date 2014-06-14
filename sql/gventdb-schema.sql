drop database if exists gventdb;
create database gventdb;

use gventdb;

create table users (
	name		varchar(50) not null,
	username	varchar(50) not null,
	userpass	char(32) not null,
	email		varchar(50) not null,
	register_date timestamp not null,
	primary key (username)

);


create table user_roles (
	username			varchar(105) not null,
	rolename 			varchar(20) not null,
	foreign key(username) references users(username) on delete cascade,
	primary key (username, rolename)
);


create table events (
	id 			int not null auto_increment primary key,
	title		varchar(50) not null,
	coord_x		varchar(25) not null,
	coord_y		varchar(25) not null,
	category	varchar(20) not null,
	description	varchar(200) not null,
	owner		varchar(20) not null,
	state		varchar(10) not null,
	public		boolean not null,
	creation_date	timestamp not null,
	event_date	date,
	popularity integer,
	foreign key(owner) references users(username) on delete cascade
);

create table comments (
	id				int not null auto_increment primary key,
	username 		varchar(20) not null,
	event_id		int not null,
	comment			varchar(200) not null,
	last_modified	timestamp not null,
	foreign key(username) references users(username),
	foreign key(event_id) references events(id)
);

create table images (
	id			int not null auto_increment primary key,
	username 	varchar(20) not null,
	image		varchar(1) not null,
	foreign key(username) references users(username)
);

create table event_users (
	event_id	int not null,
	username	varchar(20) not null,
	foreign key(username) references users(username),
	foreign key(event_id) references events(id),
	primary key (event_id, username)
);

create table friends (
    Username_A  varchar(20) not null,
    Username_B  varchar(20) not null,
    foreign key(Username_A) references users(Username) on delete cascade,
    foreign key(Username_B) references users(Username) on delete cascade,
    primary key(Username_A,Username_B)
);

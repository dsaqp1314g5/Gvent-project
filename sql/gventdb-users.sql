drop user 'gvent'@'localhost';
create user 'gvent'@'localhost' identified by 'gvent';
grant all privileges on gventdb.* to 'gvent'@'localhost';
flush privileges;
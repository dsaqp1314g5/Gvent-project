INSERT INTO users(name, username, userpass, email) VALUES ('Pepe', 'pepito', 'pepito', 'pepon@hotmail.com');
INSERT INTO users(name, username, userpass, email) VALUES ('Menga', 'menganito', 'menganito', 'megano@hotmail.com');
INSERT INTO users(name, username, userpass, email) VALUES ('Jose', 'josito', 'josito', 'joson@hotmail.com');
INSERT INTO user_roles(username, rolename) VALUES ('pepito', 'admin');
INSERT INTO user_roles(username, rolename) VALUES ('menganito', 'registered');
INSERT INTO user_roles(username, rolename) VALUES ('josito', 'registered');
INSERT INTO events(title, coord_x, coord_y, category, description, owner, state, public, event_date, popularity, puntuation, votes) VALUES('Super Evento', '89.999', '90.099', 'Ocio', 'El evento mas genial', 'pepito', 'Abierto', TRUE, DATE '2014-05-20', 1, 4, 1);
INSERT INTO events(title, coord_x, coord_y, category, description, owner, state, public, event_date, popularity, puntuation, votes) VALUES('Final del Mundial', '23.678', '56.099', 'Deportes', 'Brasil-España', 'pepito', 'Abierto', TRUE, DATE '2014-06-30', 3, 4.5, 3);
INSERT INTO events(title, coord_x, coord_y, category, description, owner, state, public, event_date, popularity, puntuation, votes) VALUES('Exposicion Cuadros', '23.678', '56.099', 'Cultura', 'Mis mejores cuadros a buen precio', 'menganito', 'Cerrado', TRUE, DATE '2014-04-23', 2, 3, 2);
INSERT INTO comments(username, event_id, comment) VALUES('menganito', 1, 'Es verdad que es genial');
INSERT INTO comments(username, event_id, comment) VALUES('pepito', 1, 'Lo se menganito');
INSERT INTO comments(username, event_id, comment) VALUES('menganito', 2, 'Quien crees que ganara?');
INSERT INTO comments(username, event_id, comment) VALUES('pepito', 2, 'Yo creo que España');
INSERT INTO comments(username, event_id, comment) VALUES('josito', 2, 'Pues yo pienso que Brasil');
INSERT INTO event_users(username, event_id) VALUES('menganito', 2);
INSERT INTO event_users(username, event_id) VALUES('josito', 2);
INSERT INTO event_users(username, event_id) VALUES('pepito', 2);
INSERT INTO event_users(username, event_id) VALUES('pepito', 1);
INSERT INTO event_users(username, event_id) VALUES('menganito', 3);
INSERT INTO event_users(username, event_id) VALUES('josito', 3);
INSERT INTO event_users(username, event_id) VALUES('pepito', 3);
INSERT INTO friends(username_a, username_b) VALUES('pepito', 'josito');
INSERT INTO friends(username_a, username_b) VALUES('pepito', 'menganito');
INSERT INTO friends(username_a, username_b) VALUES('menganito', 'josito');



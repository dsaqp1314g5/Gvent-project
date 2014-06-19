INSERT INTO users(name, username, userpass, email) VALUES ('Ruben', 'nbRuben', '1234', 'nbRuben@gmail.com');
INSERT INTO users(name, username, userpass, email) VALUES ('Miki', 'Mikiodelg', '1234', 'Mikiodelg@hotmail.com');
INSERT INTO users(name, username, userpass, email) VALUES ('Alejandro', 'Alej563', '1234', 'socorro@hotmail.com');
INSERT INTO user_roles(username, rolename) VALUES ('nbRuben', 'admin');
INSERT INTO user_roles(username, rolename) VALUES ('Mikiodelg', 'registered');
INSERT INTO user_roles(username, rolename) VALUES ('Alej563', 'registered');
INSERT INTO events(title, coord_x, coord_y, category, description, owner, state, public, event_date, popularity) VALUES('Super Evento', '40.36328834091582', '-3.636474609375', 'ocio', 'El evento mas genial', 'nbRuben', 'Abierto', TRUE, DATE '2014-05-20', 1);
INSERT INTO events(title, coord_x, coord_y, category, description, owner, state, public, event_date, popularity) VALUES('Final del Mundial', '-9.10209673872643', '-52.44873046875', 'deportes', 'Brasil-España', 'nbRuben', 'Abierto', TRUE, DATE '2014-06-30', 3);
INSERT INTO events(title, coord_x, coord_y, category, description, owner, state, public, event_date, popularity) VALUES('Exposicion Cuadros', '41.284127499930406', '1.9776248931884766', 'cultura', 'Mis mejores cuadros a buen precio', 'Mikiodelg', 'Cerrado', TRUE, DATE '2014-04-23', 2);
INSERT INTO comments(username, event_id, comment) VALUES('Mikiodelg', 1, 'Es verdad que es genial');
INSERT INTO comments(username, event_id, comment) VALUES('nbRuben', 1, 'Lo se Mikiodelg');
INSERT INTO comments(username, event_id, comment) VALUES('Mikiodelg', 2, 'Quien crees que ganara?');
INSERT INTO comments(username, event_id, comment) VALUES('nbRuben', 2, 'Yo creo que España');
INSERT INTO comments(username, event_id, comment) VALUES('Alej563', 2, 'Pues yo pienso que Brasil');
INSERT INTO event_users(username, event_id) VALUES('Mikiodelg', 2);
INSERT INTO event_users(username, event_id) VALUES('Alej563', 2);
INSERT INTO event_users(username, event_id) VALUES('nbRuben', 2);
INSERT INTO event_users(username, event_id) VALUES('nbRuben', 1);
INSERT INTO event_users(username, event_id) VALUES('Mikiodelg', 3);
INSERT INTO event_users(username, event_id) VALUES('Alej563', 3);
INSERT INTO event_users(username, event_id) VALUES('nbRuben', 3);
INSERT INTO friends(username_a, username_b) VALUES('nbRuben', 'Alej563');
INSERT INTO friends(username_a, username_b) VALUES('nbRuben', 'Mikiodelg');
INSERT INTO friends(username_a, username_b) VALUES('Mikiodelg', 'Alej563');



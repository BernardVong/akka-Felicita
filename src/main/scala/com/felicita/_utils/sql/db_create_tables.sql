CREATE TABLE users (
    id integer primary key autoincrement,
    pseudo text,
    first_name text,
    last_name text,
    is_subscriber boolean,
    is_blacklisted boolean
);
CREATE TABLE donations(
    id varchar(30) primary key,
    user_id integer,
    amount float
);
CREATE TABLE giveaways (
    id integer primary key autoincrement,
    user_id_winner integer,
    description_giveaway text
);
CREATE TABLE giveaway_subscribers (
    id integer primary key autoincrement,
    giveaway_id integer,
    user_id integer
);
CREATE TABLE surveys (
    id varchar(30) primary key,
    response_1 integer,
    response_2 integer
);


INSERT INTO users(id,first_name,last_name,pseudo,is_subscriber,is_blacklisted) VALUES (1, 'Pierre','Simon','psim',True,False);
INSERT INTO users(id,first_name,last_name,pseudo,is_subscriber,is_blacklisted) VALUES (2, 'Paul','Trois','polo',False, False);
INSERT INTO users(id,first_name,last_name,pseudo,is_subscriber,is_blacklisted) VALUES (3, 'Jacque','Gaudefroy','jacouille',True, False);
INSERT INTO users(id,first_name,last_name,pseudo,is_subscriber,is_blacklisted) VALUES (4, 'William','Sheakspear','serein',False, False);

INSERT INTO donations(id, user_id, amount) VALUES ('243KJF9','1','10');

INSERT INTO giveaways(id, description_giveaway) VALUES (1,'dons de sang');

INSERT INTO giveaway_subscribers(id, giveaway_id, user_id) VALUES (1,'1','3');

INSERT INTO surveys(id,response_1, response_2) VALUES (1,10, 12);

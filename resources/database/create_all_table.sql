CREATE TABLE users (
    id integer primary key autoincrement,
    first_name text,
    last_name text,
    pseudo text,
    subscriber boolean,
    is_blacklist boolean
);
INSERT INTO users(first_name,last_name,pseudo,subscriber,is_blacklist) VALUES ('Pierre','Simon','psim',True,False);
INSERT INTO users(first_name,last_name,pseudo,subscriber,is_blacklist) VALUES ('Paul','Trois','polo',False, False);
INSERT INTO users(first_name,last_name,pseudo,subscriber,is_blacklist) VALUES ('Jacque','Gaudefroy','jacouille',True, False);
INSERT INTO users(first_name,last_name,pseudo,subscriber,is_blacklist) VALUES ('William','Sheakspear','serein',False, False);

CREATE TABLE donations(
    id integer primary key autoincrement,
    user_id varchar(30),
    amount float,
    description text
);
INSERT INTO donations(user_id,amount,description) VALUES ('1','10','dons de sang pour X');

CREATE TABLE giveaways (
    id integer primary key autoincrement,
    description_giveaway text
);

INSERT INTO giveaways(description_giveaway) VALUES ('dons de sang');

CREATE TABLE giveaway_subscribers (
    id integer primary key autoincrement,
    giveaway_id integer,
    subscriber_id varchar(30)
);

INSERT INTO giveaway_subscribers(giveaway_id,subscriber_id) VALUES ('1','3');

CREATE TABLE surveys (
    id integer primary key autoincrement,
    total_response_0 float,
    user_id int
);

INSERT INTO surveys(id,total_response_0,user_id) VALUES ('1','10','2');
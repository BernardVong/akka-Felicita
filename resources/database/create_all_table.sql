CREATE TABLE users (
    id varchar(30) primary key,
    first_name text,
    last_name text,
    pseudo text,
    subscriber boolean,
    is_blacklist boolean
);
INSERT INTO users(id,first_name,last_name,pseudo,subscriber,is_blacklist) VALUES ('1','Pierre','Simon','psim',True,False);
INSERT INTO users(id,first_name,last_name,pseudo,subscriber,is_blacklist) VALUES ('2','Paul','Trois','polo',False, False);
INSERT INTO users(id,first_name,last_name,pseudo,subscriber,is_blacklist) VALUES ('3','Jacque','Gaudefroy','jacouille',True, False);
INSERT INTO users(id,first_name,last_name,pseudo,subscriber,is_blacklist) VALUES ('4','William','Sheakspear','serein',False, False);

CREATE TABLE donations(
    id varchar(30) primary key,
    user_id varchar(30),
    amount float
);
INSERT INTO donations(id,user_id,amount) VALUES ('243KJF9','1','10');

CREATE TABLE giveaways (
    id varchar(30) primary key,
    description_giveaway text
);

INSERT INTO giveaways(id,description_giveaway) VALUES ('1','dons de sang');

CREATE TABLE giveaway_subscribers (
    id varchar(30) primary key,
    giveaway_id varchar(30),
    subscriber_id varchar(30)
);

INSERT INTO giveaway_subscribers(id,giveaway_id,subscriber_id) VALUES ('AKKA','1','3');

CREATE TABLE surveys (
    id varchar(30) primary key,
    total_response_0 float
);

INSERT INTO surveys(id,total_response_0) VALUES ('1','10');
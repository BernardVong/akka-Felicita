CREATE TABLE subscriber (
    id varchar(30) primary key,
    first_name text,
    last_name text,
    pseudo text
);
INSERT INTO subscriber(id,first_name,last_name,pseudo) VALUES ('1','Pierre','Simon','psim');
INSERT INTO subscriber(id,first_name,last_name,pseudo) VALUES ('2','Paul','Trois','polo');
INSERT INTO subscriber(id,first_name,last_name,pseudo) VALUES ('3','Jacque','Gaudefroy','jacouille');
INSERT INTO subscriber(id,first_name,last_name,pseudo) VALUES ('4','William','Sheakspear','serein');

CREATE TABLE donations(
    id varchar(30) primary key,
    subscriber_id varchar(30),
    amount float
);
INSERT INTO donations(id,subscriber_id,amount) VALUES ('243KJF9','1','10');

CREATE TABLE giveaway (
    id varchar(30) primary key,
    description_giveaway text
);

INSERT INTO giveaway(id,description_giveaway) VALUES ('1','dons de sang');

CREATE TABLE giveaway_subscriber (
    id varchar(30) primary key,
    giveaway_id varchar(30),
    subscriber_id varchar(30)
);

INSERT INTO giveaway_subscriber(id,giveaway_id,subscriber_id) VALUES ('AKKA','1','3');

CREATE TABLE blacklist (
    id varchar(30) primary key,
    subscriber_id varchar(30)
);

INSERT INTO blacklist(id,subscriber_id) VALUES ('1','3');

CREATE TABLE survey (
    id varchar(30) primary key,
    total_response_0 float
);

INSERT INTO survey(id,total_response_0) VALUES ('1','10');
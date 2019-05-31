CREATE TABLE subscriber (
    subscriber_key varchar(30) primary key,
    first_name text,
    last_name text
);
INSERT INTO subscriber(f1,f2,F3) VALUES ('243KJF9','Pierre','Simon');
INSERT INTO subscriber(f1,f2,F3) VALUES ('9282JHS','Paul','Trois');
INSERT INTO subscriber(f1,f2,F3) VALUES ('2SJF92L','Jacque','Gaudefroy');
INSERT INTO subscriber(f1,f2,F3) VALUES ('DJF92SA','William','Sheakspear');

CREATE TABLE donator (
    donator_key varchar(30) primary key,
    first_name text,
    last_name text
);
INSERT INTO donateurs(f1,f2,F3) VALUES ('243KJF9','Pierre','Simon');


CREATE TABLE giveaway (
    giveaway_name varchar(30) primary key,
    description_giveways text
);

CREATE TABLE blacklist (
    giveaway_name varchar(30),
    subscriber_key varchar(30)
);

CREATE TABLE sondages (
    giveaways_name varchar(30),
    subscriber_key varchar(30),
    result_sondages varchar(30)
);
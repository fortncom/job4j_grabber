create table if not exists posts(
    id serial primary key,
    name varchar(255),
    link varchar(255),
    text text,
    created timestamp
);
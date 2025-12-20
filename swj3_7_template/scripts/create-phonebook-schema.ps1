$schema=@'
create schema if not exists phonebook_db;
use phonebook_db;
drop table if exists person;
create table person (id int auto_increment primary key,
                     first_name varchar(20), last_name varchar(25), address varchar(30),
                     phone_number varchar(20));

insert into person (first_name, last_name, address, phone_number)
values ('Anna', 'Mayr', 'Schoenbrunner Str. 45, Wien', '+43 1 2345678');

insert into person (first_name, last_name, address, phone_number)
values ('Lukas', 'Huber', 'Graben 12, Salzburg', '+43 662 987654');

insert into person (first_name, last_name, address, phone_number)
values ('Katharina', 'Bauer', 'Hauptplatz 3, Linz', '+43 732 123456');
'@

echo $schema | docker exec -i mysql mysql

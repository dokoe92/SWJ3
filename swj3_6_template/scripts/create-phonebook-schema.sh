#!/usr/bin/env bash

schema=$(cat <<'EOF'
create schema if not exists phonebook_db;
use phonebook_db;
drop table if exists person;
create table person (
  id int auto_increment primary key,
  first_name varchar(20),
  last_name varchar(25),
  address varchar(30),
  phone_number varchar(20)
);
EOF
)

echo "$schema" | docker exec -i mysql mysql

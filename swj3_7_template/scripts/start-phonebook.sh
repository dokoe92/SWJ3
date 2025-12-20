#!/usr/bin/env bash

mysqlDriver="$SWJ3_HOME/lib/db/mysql-connector-j-9.4.0.jar"

if [ ! -f "$mysqlDriver" ]; then
  echo "Error: MySql JDBC driver $mysqlDriver not found." >&2
  exit 1
fi

java -cp "$mysqlDriver:../bin/classes" swj3.simpledal.PhoneBookApplication

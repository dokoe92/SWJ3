$mysqlDriver = Join-Path $Env:SWJ3_HOME "lib/db/mysql-connector-j-9.4.0.jar"

if (-not (Test-Path $mysqlDriver)) {
  Write-Error "Error: MySql JDBC driver $mysqlDriver not found."
  Exit 1
}

java -cp "$mysqlDriver;../bin/classes" swj3.simpledal.PhoneBookApplication

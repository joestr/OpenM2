#!/bin/bash

#
# OpenM2
#
# Copyright (c) 2024  Joel Strasser <joelstrasser1@gmail.com>
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
# 
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

# Check if sqlcmd from Microsoft SQL Server is installed
command -v sqlcmd >/dev/null 2>&1 || { echo >&2 "This install script requires \"sqlcmd\" to be installed!"; exit 1; }

read -p "Database user's username: " DatabaseUserUsername
read -p "Password for \"$DatabaseUserUsername\": " DatabaseUserPassword

# Connection test
result=$(sqlcmd -S localhost -U $DatabaseUserUsername -P $DatabaseUserPassword -No -i "./www/modules/ibsbase/install/sql/MS-SQL/install/testconnection.sql")
expected="                              
------------------------------
TEST ERFOLGREICH ABGESCHLOSSEN
"

if [ "$result" = "$expected" ]; then
  echo "Connection test successful!"
else
  echo >&2 "Connection test unsuccessful!"
  exit 1
fi

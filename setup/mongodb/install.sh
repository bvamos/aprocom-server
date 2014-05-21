#!/bin/sh

DB=aprotest

mongo localhost:27017/$DB aprocom.js

mongoimport --db $DB --collection helyseg --file aprocom/helyseg.json
mongoimport --db $DB --collection kategoria --file aprocom/kategoria.json

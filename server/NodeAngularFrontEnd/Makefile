SHELL=zsh

USER=postgres
DATABASE=liveandgov
TABLES=${PWD}/doc/database_dumps/tables/*
SCHEMA=${PWD}/doc/database_dumps/schema/schema.sql

NODE_DIR=${PWD}/server/.node
NODE_MODULES=${PWD}/server/node_modules


# requires 
node_modules:
	@git clone https://github.com/joyent/node ${PWD}/.node

node_install:
	@npm install --nodedir=${PWD}/node

database: clean import

clean: drop create

import: import_schema import_tables

drop:
	@dropdb ${DATABASE}

create:
	@createdb -e -O ${USER} ${DATABASE}

import_schema:
	@psql -U ${USER} ${DATABASE} -f ${SCHEMA}

import_tables:
	@for table in ${TABLES}; do psql -U ${USER} -c "COPY $${table:t:r} FROM '$${table}';" ${DATABASE}; done


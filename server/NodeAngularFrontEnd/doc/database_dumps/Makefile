SHELL=zsh
USER=postgres
DATABASE=liveandgov
TABLES=${PWD}/tables/*
SCHEMA=${PWD}/schema/schema.sql

all: clean import

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


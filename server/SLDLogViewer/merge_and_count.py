#!/usr/bin/python
# -*- coding: utf-8 -*-

import string
import sys
import json
from os import curdir, sep
from sets import Set

allRoutes = Set()
goldstandard = []
computed_results = []

# route user iso_ts responsetime
# 1002	8550102e445ac626	2013-12-19 21:24:04	1444
with open('extract_route_user_ts_responsetime_out.csv', 'r') as f:
  for line in f:
    cols = line.split(";")
    print cols
    computed_results.append(cols)

# unix_ts route user iso_ts
# $ sudo su postgres
# $ COPY (SELECT ts, tag, user_id from sensor_tags, trip where sensor_tags.trip_id = trip.trip_id AND tag like '%Manually selected line with%') TO '/tmp/query.csv' (format csv, delimiter ';');
# $ psql -d liveandgov
# mvn exec:java -Dexec.mainClass=eu.liveandgov.wp1.backend.Timestamp2String -Dexec.args="query-ts.csv"

# 2014-01-24 08:05:42;"""Manually selected line with id: 1004direction : 1""";35
# 2014-01-24 08:05:42;"""Manually selected line with id: 1065A""";8550102e445ac626
with open('query.csv', 'r') as f:
  for line in f:
    cols = line.split("\t")
    cols[1] = string.replace(cols[1],'"""Manually selected line with id: ', "")
    cols[1] = string.replace(cols[1],'"""', "")
    cols[1] = string.replace(cols[1],'direction : 1', "")
    cols[1] = string.replace(cols[1],'direction : 2', "")
    #print cols[0], cols[1], cols[2]
    goldstandard.append(cols)
    print cols
found = False
total_hits = 0
total = 0
for g in goldstandard:
  found_in_this_loop = 0
  for c in computed_results:
    if g[2].strip() == c[1].strip(): # same user
      if g[0][0:10].strip() ==  c[2][0:10].strip(): # same day
        if g[1].strip() == c[0].strip(): # route
          found = True
          found_in_this_loop += 1
          allRoutes.add(g[1].strip())
          print total, "|", g[1], "<->", c[0], "|", g[0], g[2].strip(), c[2], found_in_this_loop
  if found: # found a match for this day
    total_hits +=1
    found = False
  total += 1

print "total API calls: " + str(len(computed_results)) + " maches: " + str(total_hits) + " total: " + str(total) + " hit rate: " + str(float(total_hits)/float(total))
print allRoutes
    

#!/usr/bin/python
# -*- coding: utf-8 -*-

import sys
import json
from os import curdir, sep

goldstandard = []
computed_results = []

# route user iso_ts responsetime
# 1002	8550102e445ac626	2013-12-19 21:24:04	1444
with open('route_user_ts_responsetime.csv', 'r') as f:
  for line in f:
    cols = line.split("\t")
    computed_results.append(cols)

# unix_ts route user iso_ts
# 1387438062628	1007B	a4ddad0baf3b86ae	2013-12-19 09:27:42
with open('alltags.csv', 'r') as f:
  for line in f:
    cols = line.split("\t")
    goldstandard.append(cols)

found = False
total_hits = 0
total = 0
for g in goldstandard:
  found_in_this_loop = 0
  for c in computed_results:
    if g[2] == c[1]: # same user
      if g[3][0:10] ==  c[2][0:10]: # same day
        if g[1] == c[0]: # route
          found = True
          found_in_this_loop += 1
          print g[1], g[2], g[3].strip(), c[2], found_in_this_loop
  if found: # found a match for this day
    total_hits +=1
    found = False
  total += 1

print "total API calls: " + str(len(computed_results)) + " maches: " + str(total_hits) + " total: " + str(total) + " hit rate: " + str(float(total_hits)/float(total))
    

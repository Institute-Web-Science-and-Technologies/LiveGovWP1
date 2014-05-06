#!/usr/bin/python
# -*- coding: utf-8 -*-

import sys
import json
from os import curdir, sep

PORT = 8080
logs = {}


""" read in the logfile """
with open('ServiceLineDetection.log', 'r') as f:
	for line in f:
		if line[49:57] == "response":
			jso = json.loads(line[47:])
			out = ""
			if jso["username"] <> "test_user" and jso["username"].strip() <> "":
				if len(jso["response"]["routes"]) > 0:
					out += jso["response"]["routes"][0]["route_id"] + ";"
				else:
					out += ";"

				out += jso["username"] + ";" +  jso["inputCoordinates"][0]["ts"] + ";" + str(jso["responseTime"])
				print out

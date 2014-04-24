#!/usr/bin/python
# -*- coding: utf-8 -*-

import sys
import threading
import webbrowser
import BaseHTTPServer
import SimpleHTTPServer
import json
from os import curdir, sep

PORT = 8080
logs = {}

class AjaxHandler(SimpleHTTPServer.SimpleHTTPRequestHandler):  
  print """The ajax handler is running..."""
  def __init__(self, request, client_address, server):
    SimpleHTTPServer.SimpleHTTPRequestHandler.__init__(self, request, client_address, server)

  def do_GET(self):

    if self.path == "/keys":
      data = """{"keys":""" + json.dumps(logs.keys()) + "}"
    elif self.path[:3] == "/20":
      data = logs[self.path[1:].replace("%20"," ")]
    else:
      f = open(curdir + sep + self.path, 'rb')
      data = f.read()
      f.close()

    self.send_response(200) 
    self.send_header('Content-Type', 'text/html') 
    self.end_headers()
 
    self.wfile.write(data);
    self.wfile.close();

def start_server():
  """ read in the logfile """
  with open('ServiceLineDetection.log', 'r') as f:
    for line in f:
      if line[49:57] == "response":
        logs[line[5:24]] = line[47:]

  """Start the server."""
  server_address = ("", PORT)
  server = BaseHTTPServer.HTTPServer(server_address, AjaxHandler)
  server.serve_forever()


if __name__ == "__main__":
  start_server()


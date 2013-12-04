#!/usr/bin/python
#
# Upload Servlet System Test
#
# Sends test requests to the upload servlet and checks response.
#
# by Heinrich Hartmann
#

import requests # if not avaialbel do `sudo easy_install requests`
from StringIO import StringIO

SERVLET_URL = "http://141.26.71.84:8080/UploadServlet/"

EXAMPLE_CONTENTS = "TEST UPLOAD\n" * 100
fh = StringIO(EXAMPLE_CONTENTS)

import sys

def main():
    if (len(sys.argv) != 2):
        print "Usage: Upload.py filename.ssf"
        return
    
    filename = sys.argv[1]

    fh = open(filename)

    v_req = requests.post(SERVLET_URL, files = {"upfile" : fh}, headers = {'ID' : 'Upload.py'})

    if (v_req.status_code == 202):
        print "OK: Status Code 202: Accepted"
    else:
        print "ERROR: " + str(v_req.status_code)

    print v_req.text

if __name__ == "__main__": main()

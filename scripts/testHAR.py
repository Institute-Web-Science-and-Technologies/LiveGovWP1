#!/usr/bin/python
#
# Upload Servlet System Test
#
# Sends test requests to the upload servlet and checks response.
#
# by Heinrich Hartmann
#

import requests # if not avaialbel do `sudo easy_install requests`
import sys

SERVICE_URL = "http://mobile-sensing.west.uni-koblenz.de/HAR/api"

def main():
    filename = ""
    endpoint = SERVICE_URL

    if (len(sys.argv) == 2):
        filename = sys.argv[1]
    elif (len(sys.argv) == 3):
        endpoint = get_servlet_url(sys.argv[1])
        filename = sys.argv[2]
    else:
        print "Usage: testHAR.py [host] filename"
        return

    print "Uploading %s to %s" % (filename, endpoint)

    is_compressed = filename.endswith(".gz")

    fh = open(filename)

    v_req = requests.post(endpoint, files = {"upfile" : fh},
                          headers = {
                              'ID' : 'testHAR.py',
                              'COMPRESSED' : is_compressed
                          })

    if (v_req.status_code == 202):
        print "OK: Status Code 202: Accepted"
    else:
        print "ERROR: " + str(v_req.status_code)

    print v_req.text

if __name__ == "__main__": main()

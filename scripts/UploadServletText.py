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

def ValidRequest():
    print "Testing: Valid Request"
    v_req = requests.post(SERVLET_URL, files = {"upfile" : fh}, headers = {'ID' : 'TEST'})
    if (v_req.status_code == 200):
        print "OK: Status Code 200"
    else:
        print "ERROR: Status Code:" + str(v_req.status_code)
        return;

    try:
        resp = v_req.text
        file_name = (resp.split("\n")[1]).split(":")[1]
        print "OK: Wrote to file: " + file_name;
    except IndexError:
        print "ERROR: Cannot parse reponse text. Have you edited the UploadServlet.java?"
        return

    try:
        nfh = open(file_name)
        CONTENTS  = nfh.read()
    except IOError:
        print "ERROR: Cannot open file. This is expected if this script is not run on the server."
        return

    if (CONTENTS == EXAMPLE_CONTENTS):
        print "OK: Contents of file are valid."
    else:
        print "ERROR: Wrong content in file."

def NoUpfile():
    print "Testing: Request without upfile"
    req = requests.post(SERVLET_URL, headers = {'ID' : 'TEST'})
    if (v_req.status_code == 400):
        print "OK: Reveived Status Code 400: Bad request."
    else:
        print "ERROR: Invalid status code: " + str(v_req.status_code)

ValidRequest()

NoUpfile()

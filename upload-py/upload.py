#
# Simple Python Upload Server
#
# by Heinrich Hartmann (hartmann@uni-koblenz.de)
#
# LICENSE: LGPL (http://www.gnu.org/licenses/lgpl.html)
#
# Send test data with:
# echo "SOME TEST DATA" | curl localhost:3001 -F "upfile=@-"
#

import string,cgi,time
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer

OUT_FILE = "sensor.log"
PORT = 3001

OUT_FH = open(OUT_FILE,'a')

class MyHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-type','text/html')
        self.end_headers()
        self.wfile.write("Python Upload Servlet")
        return
     
    def do_POST(self):
        fs = cgi.FieldStorage(self.rfile, self.headers)

        # The fllowing part is taken from:
        #
        # http://code.google.com/p/python-simple-fileserver/
        # Copyright Jon Berg , turtlemeat.com
        # Modified by nikomu @ code.google.com    
        # Published under LGPL
        #
        try:
            ctype, pdict = cgi.parse_header(self.headers.getheader('content-type'))    
            
            if ctype == 'multipart/form-data':
                # using cgi.FieldStorage instead, see
                # http://stackoverflow.com/questions/1417918/time-out-error-while-creating-cgi-fieldstorage-object    
                fs = cgi.FieldStorage( fp = self.rfile,
                                       headers = self.headers, # headers_,
                                       environ={ 'REQUEST_METHOD':'POST' } # all the rest will come from the 'headers' object,    
                                       # but as the FieldStorage object was designed for CGI, absense of 'POST' value in environ    
                                       # will prevent the object from using the 'fp' argument !    
                                       )
            else: raise Exception("Unexpected POST request")

            file_fs = fs['upfile']

            if file_fs != None:
                print "Writing content to " + OUT_FILE
                OUT_FH.write(file_fs.file.read())
                # OUT_FH.flush()
            else:
                print "No upfile found."
            
            self.send_response(200)
            self.end_headers()
            
            self.wfile.write( "POST OK. Recieved " + self.headers['content-length'] + "b." );
        except Exception as e:
            print e
            self.send_error(404,'POST to "%s" failed: %s' % (self.path, str(e)) )
 

def main():
    try:
        server = HTTPServer(('', PORT), MyHandler)
        print 'Started UploadServlet.'
        print 'Listening on port ' + PORT
        print 'Writing to ' + OUT_FILE
        server.serve_forever()
    except KeyboardInterrupt:
        print '^C received, shutting down server'
        server.socket.close()

if __name__ == '__main__':
    main()


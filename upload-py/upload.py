import string,cgi,time
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer

PORT = 3001

class MyHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-type','text/html')
        self.end_headers()
        self.wfile.write("Python Upload Servlet")
        return
     
    def do_POST(self):
        print self.rfile.read(int(self.headers['content-length']))

def main():
    try:
        server = HTTPServer(('', PORT), MyHandler)
        print 'started UploadServlet'
        server.serve_forever()
    except KeyboardInterrupt:
        print '^C received, shutting down server'
        server.socket.close()

if __name__ == '__main__':
    main()


package eu.liveandgov.wp1.server;

import eu.liveandgov.wp1.serialization.impl.ItemSerialization;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.zip.GZIPInputStream;

public class CheckSSF extends HttpServlet {
    private static final String FIELD_NAME_UPFILE = "upfile";
    private static final Logger Log = Logger.getLogger(CheckSSF.class);

    static {
        try {
            SimpleLayout layout = new SimpleLayout();
            FileAppender appender = null;
            appender = new FileAppender(layout, "/var/log/CheckSSF.log", true);
            Logger.getRootLogger().addAppender(appender);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Log.info("Incoming GET request from " + req.getRemoteAddr());

        PrintWriter writer = resp.getWriter();
        writer.write(
                "<html>" +
                        "<h1>Check SSF Servlet</h1>" +
                        "<form action=\"\" enctype=\"multipart/form-data\" method=\"post\">" +
                        "Upload File: <input type=\"file\" name=\"upfile\" size=\"40\"/><br/>" +
                        "<input type=\"submit\" value=\"Submit\">" +
                        "</form>" +
                        "</html>"
        );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Log.info("Incoming POST request from " + req.getRemoteAddr());

        // Retrieve Upfile
        InputStream fileStream = getStreamFromField(req, FIELD_NAME_UPFILE);

        if (fileStream == null) {
            Log.error("Field not found: " + FIELD_NAME_UPFILE);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Insert directly to database
        BufferedReader r;

        if (!isCompressed(req))
            r = new BufferedReader(new InputStreamReader(fileStream));
        else
            r = new BufferedReader(new InputStreamReader(new GZIPInputStream(fileStream), "UTF8"));

        PrintWriter w = resp.getWriter();

        int ok = 0;
        int nok = 0;

        String l;
        while ((l = r.readLine()) != null) {
            try {
                ItemSerialization.ITEM_SERIALIZATION.deSerialize(l);
                ok++;
            } catch (Throwable t) {
                w.println("<table border=\"1\">");
                w.print("<tr><td><tt>");
                w.println(l);
                w.println("</tt></td></tr>");
                w.print("<tr><td>");


                Throwable ct = t;
                while (true) {
                    w.println("<p>Exception: " + ct.getClass().getSimpleName() + "</p>");
                    w.println("<p>Message: " + ct.getMessage() + "</p>");

                    w.println("<ol>");
                    for (StackTraceElement e : ct.getStackTrace()) {
                        w.println("<li>");
                        w.println(e);
                        w.println("</li>");
                    }
                    w.println("</ol>");

                    if (ct.getCause() == null)
                        break;

                    w.println("<p>... caused by ...</p>");
                    ct = ct.getCause();
                }

                w.println("</td></tr>");
                w.println("</table>");
                nok++;

                w.println("<hr/>");
            }
        }

        w.println("Ok: " + ok);
        w.println("Not ok: " + nok);

        resp.setStatus(HttpServletResponse.SC_ACCEPTED);
    }

    private boolean isCompressed(HttpServletRequest req) {
        return Boolean.parseBoolean(req.getHeader("COMPRESSED"));
    }

    private InputStream getStreamFromField(HttpServletRequest req, String fieldName) {
        try {
            // Check that we have a file upload request
            boolean isMultipart = ServletFileUpload.isMultipartContent(req);
            if (!isMultipart) {
                Log.error("Received non-multipart POST request");
                return null;
            }

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload();

            // Parse the request
            FileItemIterator iter = upload.getItemIterator(req);

            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                String name = item.getFieldName();

                if (!name.equals(FIELD_NAME_UPFILE)) {
                    Log.info("Found unknown field " + name);
                    continue;
                }
                return item.openStream();
            }
        } catch (FileUploadException e) {
            Log.error("Error parsing the upfile", e);
        } catch (IOException e) {
            Log.error("Network error while parsing file.", e);
        }
        return null;
    }

}

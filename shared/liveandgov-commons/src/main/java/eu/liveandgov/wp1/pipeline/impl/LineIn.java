package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Producer;
import eu.liveandgov.wp1.util.LocalBuilder;

import java.io.*;
import java.util.Scanner;

/**
 * <p>The line-in producer reads lines with newline from a scanner</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public class LineIn extends Producer<String> {
    /**
     * Reads all lines from an input stream by wrapping it in a buffered reader
     *
     * @param inputStream The stream to read from
     */
    public void readFrom(InputStream inputStream) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = br.readLine()) != null) {
            final StringBuilder stringBuilder = LocalBuilder.acquireBuilder();
            stringBuilder.append(line);
            stringBuilder.append("\r\n");

            produce(stringBuilder.toString());
        }

        br.close();
    }
}

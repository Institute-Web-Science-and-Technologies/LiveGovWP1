package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Producer;
import eu.liveandgov.wp1.util.LocalBuilder;

import java.io.InputStream;
import java.util.Scanner;

/**
 * <p>The line-in producer reads lines with newline from a scanner</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public class LineIn extends Producer<String> {
    /**
     * Reads all lines from a scanner into the output
     *
     * @param scanner The scanner to read from
     */
    public void readFrom(Scanner scanner) {
        while (scanner.hasNextLine()) {
            final StringBuilder stringBuilder = LocalBuilder.acquireBuilder();
            stringBuilder.append(scanner.nextLine());
            stringBuilder.append("\r\n");

            produce(stringBuilder.toString());
        }
    }

    /**
     * Reads all lines from an input stream by wrapping it in a scanner
     *
     * @param inputStream The stream to read from
     */
    public void readFrom(InputStream inputStream) {
        readFrom(new Scanner(inputStream));
    }
}

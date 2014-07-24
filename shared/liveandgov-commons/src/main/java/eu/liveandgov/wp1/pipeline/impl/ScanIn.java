package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Producer;

import java.io.InputStream;
import java.util.Scanner;

/**
 * <p>The scan-in producer reads from a scanner</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public class ScanIn extends Producer<String> {
    /**
     * Reads all lines from a scanner into the output
     *
     * @param scanner The scanner to read from
     */
    public void readFrom(Scanner scanner) {
        while (scanner.hasNextLine()) {
            produce(scanner.nextLine());
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

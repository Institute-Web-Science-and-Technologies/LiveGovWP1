package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Producer;

import java.io.InputStream;
import java.util.Scanner;

/**
 * <p>The line-in producer reads from a scanner</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public class LineInProducer extends Producer<String> {
    public void readFrom(Scanner scanner) {
        while (scanner.hasNextLine()) {
            produce(scanner.nextLine());
        }
    }

    public void readFrom(InputStream inputStream) {
        readFrom(new Scanner(inputStream));
    }
}

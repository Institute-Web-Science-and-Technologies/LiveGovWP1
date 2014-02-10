package eu.liveandgov.wp1.pipeline.implementations;

import eu.liveandgov.wp1.pipeline.Producer;

import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.Executor;

/**
 * <p>The line-in producer reads from a scanner with the executor provided</p>
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

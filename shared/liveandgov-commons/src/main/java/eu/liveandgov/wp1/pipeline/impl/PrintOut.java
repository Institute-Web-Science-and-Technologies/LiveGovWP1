package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Consumer;

import java.io.PrintStream;

/**
 * <p>The print-out consumer consumes objects and prints them with a print stream</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public class PrintOut implements Consumer<Object> {
    /**
     * Target stream
     */
    public final PrintStream printStream;

    /**
     * Creates a new instance with the given values
     * @param printStream Target stream
     */
    public PrintOut(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void push(Object o) {
        printStream.print(o);
    }
}

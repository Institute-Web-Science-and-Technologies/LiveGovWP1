package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Consumer;

import java.io.PrintStream;

/**
 * <p>The line-out consumer consumes objects and prints them with a print stream</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public class LinesOut implements Consumer<Object> {
    /**
     * Target stream
     */
    public final PrintStream printStream;

    /**
     * Creates a new instance with the given values
     * @param printStream Target stream
     */
    public LinesOut(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void push(Object o) {
        printStream.println(o);
    }
}

package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Pipeline;

/**
 * <p>Filters a stream of object by instanceof conversion</p>
 * Created by Lukas Härtel on 01.03.14.
 */
public class ClassFilter<Target> extends Pipeline<Object, Target> {
    /**
     * The class to check
     */
    public final Class<Target> targetClass;

    /**
     * Creates a new instance with the given values
     *
     * @param targetClass The class to check
     */
    public ClassFilter(Class<Target> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public void push(Object o) {
        if (targetClass.isInstance(o)) {
            produce(targetClass.cast(o));
        }
    }
}

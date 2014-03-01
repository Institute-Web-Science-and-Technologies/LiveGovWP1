package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Pipeline;

/**
 * Created by Lukas Härtel on 01.03.14.
 */
public class ClassFilter<Target> extends Pipeline<Object, Target> {
    public final Class<Target> targetClass;

    public ClassFilter(Class<Target> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public void push(Object o) {
        if(targetClass.isInstance(o)){
            produce(targetClass.cast(o));
        }
    }
}

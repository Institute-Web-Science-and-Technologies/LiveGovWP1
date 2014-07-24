package eu.liveandgov.wp1.pipeline.impl;

import com.google.common.base.Function;
import eu.liveandgov.wp1.pipeline.Pipeline;

/**
 * <p>The transformation element uses a given function to calculate its product</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public class Transformation<SourceItem, TargetItem> extends Pipeline<SourceItem, TargetItem> {
    /**
     * Function used to transform
     */
    public final Function<? super SourceItem, ? extends TargetItem> function;

    /**
     * Constructs a new instance with the given values
     *
     * @param function Function used to transform
     */
    public Transformation(Function<? super SourceItem, ? extends TargetItem> function) {
        this.function = function;
    }

    @Override
    public void push(SourceItem sourceItem) {
        produce(function.apply(sourceItem));
    }
}

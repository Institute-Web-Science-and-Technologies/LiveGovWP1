package eu.liveandgov.wp1.pipeline.impl;

import com.google.common.base.Function;
import eu.liveandgov.wp1.pipeline.Pipeline;

/**
 * <p>The functional eu.liveandgov.wp1.pipeline element uses a given function to calculate its product</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public class FunctionalPipeline<SourceItem, TargetItem> extends Pipeline<SourceItem, TargetItem> {
    public final Function<? super SourceItem, ? extends TargetItem> function;

    public FunctionalPipeline(Function<? super SourceItem, ? extends TargetItem> function) {
        this.function = function;
    }

    @Override
    public void push(SourceItem sourceItem) {
        produce(function.apply(sourceItem));
    }
}

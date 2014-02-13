package eu.liveandgov.wp1.pipeline;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public abstract class MultiPipeline<SourceItem, TargetItem> extends MultiProducer<TargetItem> implements Consumer<SourceItem> {
}

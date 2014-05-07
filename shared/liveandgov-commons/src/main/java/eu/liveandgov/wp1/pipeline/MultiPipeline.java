package eu.liveandgov.wp1.pipeline;

/**
 * <p>Multi pipeline is a consumer that is also a multi producer</p>
 * Created by Lukas Härtel on 08.02.14.
 */
public abstract class MultiPipeline<SourceItem, TargetItem> extends MultiProducer<TargetItem> implements Consumer<SourceItem> {
}

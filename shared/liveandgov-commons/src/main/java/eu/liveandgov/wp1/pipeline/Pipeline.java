package eu.liveandgov.wp1.pipeline;

/**
 * <p>Pipeline represents a consumer that is also a producer</p>
 * Created by Lukas Härtel on 08.02.14.
 */
public abstract class Pipeline<SourceItem, TargetItem> extends Producer<TargetItem> implements Consumer<SourceItem> {
}

package eu.liveandgov.wp1.pipeline;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public abstract class Pipeline<SourceItem, TargetItem> extends Producer<TargetItem> implements Consumer<SourceItem> {
}

package org.swisspush.logtransformer.strategy;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Default implementation of the {@link TransformStrategyFinder}
 *
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public class DefaultTransformStrategyFinder implements TransformStrategyFinder {

    private final String strategyHeader;
    private final Logger log = LoggerFactory.getLogger(DefaultTransformStrategyFinder.class);

    private Vertx vertx;

    private DoNothingTransformStrategy doNothingTransformStrategy;
    private SplitStorageExpandLogStrategy splitStorageExpandLogStrategy;

    public DefaultTransformStrategyFinder(Vertx vertx, String strategyHeader) {
        this.vertx = vertx;
        this.strategyHeader = strategyHeader;
    }

    /**
     * Returns the corresponding {@link TransformStrategy} implementation based on the provided header strategy property.
     *
     * @param headers the headers containing the name of the strategy
     * @return returns the corresponding {@link TransformStrategy} implementation or {@link DoNothingTransformStrategy} when no matching strategy was found
     */
    @Override
    public TransformStrategy findTransformStrategy(MultiMap headers){
        String strategy = headers.get(strategyHeader);

        if(isEmpty(strategy)){
            return getDoNothingTransformStrategy();
        } else if("SplitStorageExpandLogStrategy".equalsIgnoreCase(strategy)){
            return getSplitStorageExpandLogStrategy();
        }

        log.warn("No log transform strategy found for value '" + strategy + "'. Using DoNothingTransformStrategy instead");
        return getDoNothingTransformStrategy();
    }

    private DoNothingTransformStrategy getDoNothingTransformStrategy(){
        if(doNothingTransformStrategy == null){
            doNothingTransformStrategy = new DoNothingTransformStrategy(vertx);
        }
        return doNothingTransformStrategy;
    }

    private SplitStorageExpandLogStrategy getSplitStorageExpandLogStrategy(){
        if(splitStorageExpandLogStrategy == null){
            splitStorageExpandLogStrategy = new SplitStorageExpandLogStrategy(vertx);
        }
        return splitStorageExpandLogStrategy;
    }

    private boolean isEmpty(String stringToTest){
        if(stringToTest == null){
            return true;
        }
        String trimmed = stringToTest.trim();
        return trimmed.length() == 0;
    }
}

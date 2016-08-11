package org.swisspush.logtransformer.strategy;

import io.vertx.core.MultiMap;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public class TransformStrategyFinder {

    private static final String STRATEGY_HEADER_PROPERTY = "metadata";
    private final Logger log = LoggerFactory.getLogger(TransformStrategyFinder.class);

    private DoNothingTransformStrategy doNothingTransformStrategy;

    public TransformStrategy findTransformStrategy(MultiMap headers){
        String strategy = headers.get(STRATEGY_HEADER_PROPERTY);

        if(isEmpty(strategy)){
            return getDoNothingTransformStrategy();
        }

        log.warn("No log transform strategy found for value '" + strategy + "'. Using DoNothingTransformStrategy instead");
        return getDoNothingTransformStrategy();
    }

    private DoNothingTransformStrategy getDoNothingTransformStrategy(){
        if(doNothingTransformStrategy == null){
            doNothingTransformStrategy = new DoNothingTransformStrategy();
        }
        return doNothingTransformStrategy;
    }

    private boolean isEmpty(String stringToTest){
        if(stringToTest == null){
            return true;
        }
        String trimmed = stringToTest.trim();
        return trimmed.length() == 0;
    }
}

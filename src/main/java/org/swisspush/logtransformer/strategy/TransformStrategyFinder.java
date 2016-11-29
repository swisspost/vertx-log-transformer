package org.swisspush.logtransformer.strategy;

import io.vertx.core.MultiMap;

/**
 * Manages the mapping of the {@link TransformStrategy} implementations to their strategy name (metadata)
 *
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public interface TransformStrategyFinder {

    /**
     * Returns the corresponding {@link TransformStrategy} implementation based on the provided header strategy property.
     *
     * @param headers the headers containing the name of the strategy
     * @return returns the corresponding {@link TransformStrategy} implementation
     */
    TransformStrategy findTransformStrategy(MultiMap headers);
}

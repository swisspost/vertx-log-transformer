package org.swisspush.logtransformer.strategy;

import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for the {@link TransformStrategyFinder} class
 *
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
@RunWith(VertxUnitRunner.class)
public class TransformStrategyFinderTest {

    @Test
    public void testNoOrEmptyStrategyProvided(TestContext context){
        TransformStrategyFinder finder = new TransformStrategyFinder();

        TransformStrategy strategy = finder.findTransformStrategy(new CaseInsensitiveHeaders());
        context.assertNotNull(strategy);
        context.assertEquals(DoNothingTransformStrategy.class, strategy.getClass());

        strategy = finder.findTransformStrategy(new CaseInsensitiveHeaders().add("metadata", ""));
        context.assertNotNull(strategy);
        context.assertEquals(DoNothingTransformStrategy.class, strategy.getClass());
    }

    @Test
    public void testUnknownStrategyProvided(TestContext context){
        TransformStrategyFinder finder = new TransformStrategyFinder();

        TransformStrategy strategy = finder.findTransformStrategy(new CaseInsensitiveHeaders().add("metadata", "some_unknown_strategy"));
        context.assertNotNull(strategy);
        context.assertEquals(DoNothingTransformStrategy.class, strategy.getClass());
    }
}

package org.swisspush.logtransformer.strategy;

import io.vertx.core.Vertx;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for the {@link DefaultTransformStrategyFinder} class
 *
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
@RunWith(VertxUnitRunner.class)
public class DefaultTransformStrategyFinderTest {

    private Vertx vertx;
    private DefaultTransformStrategyFinder finder;
    private final String STRATEGY_HEADER = "transformStrategy";

    @Before
    public void setUp(){
        this.vertx = Vertx.vertx();
        this.finder = new DefaultTransformStrategyFinder(vertx, STRATEGY_HEADER);
    }

    @Test
    public void testNoOrEmptyStrategyProvided(TestContext context){
        TransformStrategy strategy = finder.findTransformStrategy(new CaseInsensitiveHeaders());
        assertStrategy(context, strategy, DoNothingTransformStrategy.class);

        strategy = finder.findTransformStrategy(new CaseInsensitiveHeaders().add(STRATEGY_HEADER, ""));
        assertStrategy(context, strategy, DoNothingTransformStrategy.class);
    }

    @Test
    public void testUnknownStrategyProvided(TestContext context){
        TransformStrategy strategy = finder.findTransformStrategy(new CaseInsensitiveHeaders().add(STRATEGY_HEADER, "some_unknown_strategy"));
        assertStrategy(context, strategy, DoNothingTransformStrategy.class);
    }

    @Test
    public void testSplitStorageExpandLogStrategy(TestContext context){
        TransformStrategy strategy = finder.findTransformStrategy(new CaseInsensitiveHeaders().add(STRATEGY_HEADER, "SplitStorageExpandLogStrategy"));
        assertStrategy(context, strategy, SplitStorageExpandLogStrategy.class);

        strategy = finder.findTransformStrategy(new CaseInsensitiveHeaders().add(STRATEGY_HEADER, "splitstorageexpandlogstrategy"));
        assertStrategy(context, strategy, SplitStorageExpandLogStrategy.class);

        strategy = finder.findTransformStrategy(new CaseInsensitiveHeaders().add(STRATEGY_HEADER, "SPLITSTORAGEEXPANDLOGSTRATEGY"));
        assertStrategy(context, strategy, SplitStorageExpandLogStrategy.class);

        strategy = finder.findTransformStrategy(new CaseInsensitiveHeaders().add(STRATEGY_HEADER, "Split_Storage_Expand_Log_Strategy"));
        assertStrategy(context, strategy, DoNothingTransformStrategy.class);
    }

    private void assertStrategy(TestContext context, TransformStrategy strategy, Class clazz){
        context.assertNotNull(strategy);
        context.assertEquals(clazz, strategy.getClass());
    }
}

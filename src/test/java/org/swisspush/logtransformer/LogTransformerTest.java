package org.swisspush.logtransformer;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.swisspush.logtransformer.logger.LogTransformLogger;
import org.swisspush.logtransformer.util.Configuration;

import java.util.List;

/**
 * Tests for the {@link LogTransformer} class
 *
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
@RunWith(VertxUnitRunner.class)
public class LogTransformerTest {

    private Vertx vertx;

    @org.junit.Rule
    public Timeout rule = Timeout.seconds(5);

    @Before
    public void setUp(){
        vertx = Vertx.vertx();
    }

    @Test
    public void testDeployLogTransformerVerticle(TestContext context){
        Async async = context.async();
        vertx.deployVerticle(new LogTransformer(), event -> {
            context.assertTrue(event.succeeded());
            async.complete();
        });
    }

    @Test
    public void testDefaultTransformStrategyDoesNotChangeLog(TestContext context){
        Async async = context.async();
        JsonObject config = Configuration.with().address("new_address").build().asJsonObject();

        LogTransformer transformer = new LogTransformer(new TestLogger(context, async, "some logs"));

        DeploymentOptions options = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(transformer, options, deployEvent -> {
            context.assertTrue(deployEvent.succeeded());
            vertx.eventBus().publish("new_address", "some logs");
        });
    }

    class TestLogger implements LogTransformLogger {

        private TestContext context;
        private Async async;
        private String expectedStringToLog;

        public TestLogger(TestContext context, Async async, String expectedStringToLog) {
            this.context = context;
            this.async = async;
            this.expectedStringToLog = expectedStringToLog;
        }

        @Override
        public void doLog(List<String> logEntries) {
            context.assertEquals(expectedStringToLog, logEntries.get(0));
            async.complete();
        }
    }
}

package org.swisspush.logtransformer;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
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

        LogTransformer transformer = new LogTransformer(new TestLogger(vertx, context, "some logs"));

        DeploymentOptions options = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(transformer, options, deployEvent -> {
            context.assertTrue(deployEvent.succeeded());
            vertx.eventBus().send("new_address", "some logs", res -> {
                context.assertTrue(res.succeeded());
                async.complete();
            });
        });
    }

    class TestLogger implements LogTransformLogger {

        private Vertx vertx;
        private TestContext context;
        private String expectedStringToLog;

        public TestLogger(Vertx vertx, TestContext context, String expectedStringToLog) {
            this.vertx = vertx;
            this.context = context;
            this.expectedStringToLog = expectedStringToLog;
        }

        @Override
        public void doLog(List<String> logEntries, Handler<AsyncResult<Void>> resultHandler) {
            vertx.executeBlocking(future -> {
                context.assertEquals(expectedStringToLog, logEntries.get(0));
                future.complete();
            }, resultHandler);
        }
    }
}

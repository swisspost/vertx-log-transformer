package org.swisspush.logtransformer;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.swisspush.logtransformer.logger.LogTransformLogger;
import org.swisspush.logtransformer.strategy.TransformStrategy;
import org.swisspush.logtransformer.strategy.TransformStrategyFinder;
import org.swisspush.logtransformer.util.Configuration;

import java.util.Collections;
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

        LogTransformer transformer = new LogTransformer(new TestLogger(true, context, "some logs", null));

        DeploymentOptions options = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(transformer, options, deployEvent -> {
            context.assertTrue(deployEvent.succeeded());
            vertx.eventBus().send("new_address", "some logs", res -> {
                context.assertTrue(res.succeeded());
                async.complete();
            });
        });
    }

    @Test
    public void testFailDuringLogTransformation(TestContext context){
        Async async = context.async();
        JsonObject config = Configuration.with().address("new_address").build().asJsonObject();

        TransformStrategyFinder transformStrategyFinder
                = new TestTransformStrategyFinder(false, null, new IllegalStateException("something went wrong during transformation"));
        LogTransformLogger logTransformLogger = new TestLogger(true, context, "some logs", null);

        LogTransformer transformer = new LogTransformer(logTransformLogger, transformStrategyFinder);

        DeploymentOptions options = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(transformer, options, deployEvent -> {
            context.assertTrue(deployEvent.succeeded());
            vertx.eventBus().send("new_address", "some logs", res -> {
                context.assertFalse(res.succeeded());
                context.assertEquals("something went wrong during transformation", res.cause().getMessage());
                async.complete();
            });
        });
    }

    @Test
    public void testFailDuringLogging(TestContext context){
        Async async = context.async();
        JsonObject config = Configuration.with().address("new_address").build().asJsonObject();

        TransformStrategyFinder transformStrategyFinder
                = new TestTransformStrategyFinder(true, Collections.singletonList("some logs"), null);
        LogTransformLogger logTransformLogger = new TestLogger(false, context, "some logs", new IllegalStateException("something went wrong during logging"));

        LogTransformer transformer = new LogTransformer(logTransformLogger, transformStrategyFinder);

        DeploymentOptions options = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(transformer, options, deployEvent -> {
            context.assertTrue(deployEvent.succeeded());
            vertx.eventBus().send("new_address", "some logs", res -> {
                context.assertFalse(res.succeeded());
                context.assertEquals("something went wrong during logging", res.cause().getMessage());
                async.complete();
            });
        });
    }

    @Test
    public void testFailDuringLoggingWithPublish(TestContext context){
        Async async = context.async();
        JsonObject config = Configuration.with().address("new_address").build().asJsonObject();

        TransformStrategyFinder transformStrategyFinder
                = new TestTransformStrategyFinder(true, Collections.singletonList("some logs"), null);
        LogTransformLogger logTransformLogger = new TestLogger(false, context, "some logs",
                new IllegalStateException("something went wrong during logging"), async);

        LogTransformer transformer = new LogTransformer(logTransformLogger, transformStrategyFinder);

        DeploymentOptions options = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(transformer, options, deployEvent -> {
            context.assertTrue(deployEvent.succeeded());
            vertx.eventBus().publish("new_address", "some logs");
        });
    }


    class TestTransformStrategy implements TransformStrategy {

        private boolean success;
        private List<String> transformedLog;
        private Throwable throwable;

        public TestTransformStrategy(boolean success, List<String> transformedLog, Throwable throwable) {
            this.success = success;
            this.transformedLog = transformedLog;
            this.throwable = throwable;
        }

        @Override
        public void transformLog(String logToTransform, Handler<AsyncResult<List<String>>> resultHandler) {
            resultHandler.handle(new AsyncResult<List<String>>() {
                @Override
                public List<String> result() { return transformedLog; }

                @Override
                public Throwable cause() { return throwable; }

                @Override
                public boolean succeeded() { return success; }

                @Override
                public boolean failed() { return !success; }
            });
        }
    }

    class TestTransformStrategyFinder implements TransformStrategyFinder {

        private boolean success;
        private List<String> transformedLog;
        private Throwable throwable;

        public TestTransformStrategyFinder(boolean success, List<String> transformedLog, Throwable throwable) {
            this.success = success;
            this.transformedLog = transformedLog;
            this.throwable = throwable;
        }

        @Override
        public TransformStrategy findTransformStrategy(MultiMap headers) {
            return new TestTransformStrategy(success, transformedLog, throwable);
        }
    }

    class TestLogger implements LogTransformLogger {

        private TestContext context;
        private String expectedStringToLog;
        private Async async;
        private boolean success;
        private Throwable throwable;

        public TestLogger(boolean success, TestContext context, String expectedStringToLog, Throwable throwable) {
            this(success, context, expectedStringToLog, throwable, null);
        }

        public TestLogger(boolean success, TestContext context, String expectedStringToLog, Throwable throwable, Async async) {
            this.success = success;
            this.context = context;
            this.expectedStringToLog = expectedStringToLog;
            this.throwable = throwable;
            this.async = async;
        }

        @Override
        public void doLog(List<String> logEntries, Handler<AsyncResult<Void>> resultHandler) {
            context.assertEquals(expectedStringToLog, logEntries.get(0));
            resultHandler.handle(new AsyncResult<Void>() {
                @Override
                public Void result() {
                    return null;
                }

                @Override
                public Throwable cause() {
                    return throwable;
                }

                @Override
                public boolean succeeded() {
                    return success;
                }

                @Override
                public boolean failed() {
                    return !success;
                }
            });
            if(async != null){
                async.complete();
            }
        }
    }
}

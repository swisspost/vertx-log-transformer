package org.swisspush.logtransformer.strategy;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of the {@link TransformStrategy} doing no transformation at all.
 *
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public class DoNothingTransformStrategy implements TransformStrategy {

    private Vertx vertx;

    public DoNothingTransformStrategy(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void transformLog(String logToTransform, Handler<AsyncResult<List<String>>> resultHandler) {
        vertx.executeBlocking(future -> {
            future.complete(Collections.singletonList(logToTransform));
        }, resultHandler);
    }

}

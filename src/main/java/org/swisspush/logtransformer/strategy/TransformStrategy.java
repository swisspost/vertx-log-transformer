package org.swisspush.logtransformer.strategy;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

/**
 * TransformStrategy defines a strategy how to transform log input.
 *
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public interface TransformStrategy {

    /**
     * Transforms the provided logToTransform and returns a list of log entries asynchronous.
     *
     * @param logToTransform the log to transform
     * @param resultHandler the handler containing the transformed log
     *
     */
    void transformLog(String logToTransform, Handler<AsyncResult<List<String>>> resultHandler);
}

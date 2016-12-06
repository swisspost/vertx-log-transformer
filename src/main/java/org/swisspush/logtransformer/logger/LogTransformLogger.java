package org.swisspush.logtransformer.logger;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

/**
 * The LogTransformLogger logs the provided log entries line-by-line
 *
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public interface LogTransformLogger {

    /**
     * Logs the provided log entries asynchronously line-by-line
     *
     * @param logEntries a list of log entries to log line-by-line
     */
    void doLog(List<String> logEntries, Handler<AsyncResult<Void>> resultHandler);
}

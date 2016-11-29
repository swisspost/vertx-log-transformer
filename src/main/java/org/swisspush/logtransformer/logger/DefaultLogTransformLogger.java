package org.swisspush.logtransformer.logger;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.List;

/**
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public class DefaultLogTransformLogger implements LogTransformLogger {

    private final Logger log;
    private Vertx vertx;

    public DefaultLogTransformLogger(Vertx vertx, String loggerName) {
        this.vertx = vertx;
        this.log = LoggerFactory.getLogger(loggerName);
    }

    @Override
    public void doLog(List<String> logEntries, Handler<AsyncResult<Void>> resultHandler) {
        vertx.executeBlocking(future -> {
            if (logEntries != null) {
                for (String logEntry : logEntries) {
                    log.info(logEntry);
                }
            }
            future.complete();
        }, resultHandler);
    }
}

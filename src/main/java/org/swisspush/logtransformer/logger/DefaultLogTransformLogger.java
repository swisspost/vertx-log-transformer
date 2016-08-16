package org.swisspush.logtransformer.logger;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.List;

/**
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public class DefaultLogTransformLogger implements LogTransformLogger {

    private final Logger log;

    public DefaultLogTransformLogger(String loggerName){
        this.log = LoggerFactory.getLogger(loggerName);
    }

    @Override
    public void doLog(List<String> logEntries) {
        for (String logEntry : logEntries) {
            log.info(logEntry);
        }
    }
}

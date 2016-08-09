package org.swisspush.logtransformer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public class LogTransformer extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(LogTransformer.class);

    @Override
    public void start(Future<Void> fut) {
        log.info("LogTransformer started");
        fut.complete();
    }
}

package org.swisspush.logtransformer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.swisspush.logtransformer.logger.DefaultLogTransformLogger;
import org.swisspush.logtransformer.logger.LogTransformLogger;
import org.swisspush.logtransformer.strategy.TransformStrategy;
import org.swisspush.logtransformer.strategy.TransformStrategyFinder;
import org.swisspush.logtransformer.util.Configuration;

import java.util.List;

/**
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public class LogTransformer extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(LogTransformer.class);
    private final LogTransformLogger logTransformLogger;
    private final TransformStrategyFinder transformStrategyFinder = new TransformStrategyFinder();

    public LogTransformer(){
        this.logTransformLogger = new DefaultLogTransformLogger();
    }

    public LogTransformer(LogTransformLogger logTransformLogger) {
        this.logTransformLogger = logTransformLogger;
    }

    @Override
    public void start(Future<Void> future) {
        log.info("LogTransformer started");

        final EventBus eb = vertx.eventBus();
        Configuration modConfig = Configuration.fromJsonObject(config());
        log.info("Starting LogTransformer module with configuration: " + modConfig);

        eb.consumer(modConfig.getAddress(), event -> {
            TransformStrategy strategy = transformStrategyFinder.findTransformStrategy(event.headers());
            log.info("About to transform log with strategy '" + strategy.getClass().getSimpleName() + "'");
            List<String> transformedLogEntries = strategy.transformLog(event.body().toString());
            logTransformLogger.doLog(transformedLogEntries);
        });

        future.complete();
    }
}

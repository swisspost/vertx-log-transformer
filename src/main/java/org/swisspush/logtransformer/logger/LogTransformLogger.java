package org.swisspush.logtransformer.logger;

import org.swisspush.logtransformer.strategy.TransformStrategy;

/**
 * Created by webermarca on 11.08.2016.
 */
public interface LogTransformLogger {

    void doLog(String stringToLog);
}

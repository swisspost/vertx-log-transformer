package org.swisspush.logtransformer.strategy;

import java.util.List;

/**
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public interface TransformStrategy {

    /**
     * Transforms the provided logToTransform and returns a list of log entries.
     *
     * @param logToTransform the log to transform
     * @return a list of log entries
     */
    List<String> transformLog(String logToTransform);
}

package org.swisspush.logtransformer.logger;

import java.util.List;

/**
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public interface LogTransformLogger {

    void doLog(List<String> logEntries);
}

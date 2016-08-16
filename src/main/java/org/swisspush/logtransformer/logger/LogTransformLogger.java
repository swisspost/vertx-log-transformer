package org.swisspush.logtransformer.logger;

import java.util.List;

/**
 * The LogTransformLogger logs the provided log entries line-by-line
 *
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public interface LogTransformLogger {

    /**
     * Logs the provided log entries line-by-line
     *
     * @param logEntries a list of log entries to log line-by-line
     */
    void doLog(List<String> logEntries);
}

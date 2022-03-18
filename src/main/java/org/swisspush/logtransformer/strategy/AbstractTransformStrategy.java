package org.swisspush.logtransformer.strategy;

import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract implementation of the {@link TransformStrategy} interface providing functionality
 * like error handling and JSON parsing.
 *
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public abstract class AbstractTransformStrategy implements TransformStrategy {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Logs the provided errorMessage to the log and returns the original (not transformed) log input.
     * Use this method when the provided log input cannot be transformed correctly.
     *
     * @param logToTransform the log input to transform
     * @param errorMessage the error message to write to the log
     * @return returns a list containing the original log input
     */
    protected List<String> doNothingInCaseOfError(String logToTransform, String errorMessage) {
        log.error(errorMessage + ". Logging original log input instead");
        if(logToTransform == null){
            return new ArrayList<>();
        }
        return Collections.singletonList(logToTransform);
    }

    /**
     * Parses the provided log input into a JsonObject.
     *
     * @param logToTransform the log input to transform
     * @return a JsonObject representation of the log input
     * @throws LogContentException when log input was <code>null</code> or could not be parsed into a JsonObject
     */
    protected JsonObject parseStringLogToJsonObject(String logToTransform) throws LogContentException {
        try{
            if(logToTransform == null){
                throw new LogContentException("Log was null and therefore could not be converted to JSON");
            }
            return new JsonObject(logToTransform);
        }catch (DecodeException ex){
            throw new LogContentException("Log could not be converted to JSON", ex);
        }
    }
}

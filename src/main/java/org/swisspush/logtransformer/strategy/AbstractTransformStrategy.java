package org.swisspush.logtransformer.strategy;

import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public abstract class AbstractTransformStrategy implements TransformStrategy {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected List<String> doNothingInCaseOfError(String logToTransform, String errorMessage) {
        log.error(errorMessage + ". Logging original log input instead");
        if(logToTransform == null){
            return new ArrayList<>();
        }
        return Collections.singletonList(logToTransform);
    }

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

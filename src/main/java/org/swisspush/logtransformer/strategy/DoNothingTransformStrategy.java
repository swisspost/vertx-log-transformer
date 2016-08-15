package org.swisspush.logtransformer.strategy;

import java.util.Collections;
import java.util.List;

/**
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public class DoNothingTransformStrategy implements TransformStrategy {

    @Override
    public List<String> transformLog(String logToTransform) {
        return Collections.singletonList(logToTransform);
    }
}

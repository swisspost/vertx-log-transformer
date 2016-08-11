package org.swisspush.logtransformer.strategy;

/**
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public class DoNothingTransformStrategy implements TransformStrategy {

    @Override
    public String transformLog(String logToTransform) {
        return logToTransform;
    }
}

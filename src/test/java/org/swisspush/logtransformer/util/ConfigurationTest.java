package org.swisspush.logtransformer.util;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.swisspush.logtransformer.util.Configuration.*;

/**
 * Tests for {@link Configuration} class.
 *
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
@RunWith(VertxUnitRunner.class)
public class ConfigurationTest {

    @Test
    public void testDefaultConfiguration(TestContext testContext){
        Configuration config = new Configuration();
        testContext.assertEquals("swisspush.logtransformer", config.getAddress());
        testContext.assertEquals("LogTransformerLogger", config.getLoggerName());
        testContext.assertEquals("transformStrategy", config.getStrategyHeader());
    }

    @Test
    public void testOverrideConfiguration(TestContext testContext){
        Configuration config = with()
                .address("new_address")
                .loggerName("new_loggerName")
                .strategyHeader("new_strategyHeader")
                .build();

        testContext.assertEquals("new_address", config.getAddress());
        testContext.assertEquals("new_loggerName", config.getLoggerName());
        testContext.assertEquals("new_strategyHeader", config.getStrategyHeader());
    }

    @Test
    public void testGetDefaultAsJsonObject(TestContext testContext){
        Configuration config = new Configuration();
        JsonObject json = config.asJsonObject();

        testContext.assertEquals("swisspush.logtransformer", json.getString(PROP_ADDRESS));
        testContext.assertEquals("LogTransformerLogger", json.getString(PROP_LOGGER_NAME));
        testContext.assertEquals("transformStrategy", json.getString(PROP_STRATEGY_HEADER));
    }

    @Test
    public void testGetOverriddenAsJsonObject(TestContext testContext){

        Configuration config = with()
                .address("new_address")
                .loggerName("new_loggerName")
                .strategyHeader("new_strategyHeader")
                .build();

        JsonObject json = config.asJsonObject();

        // overridden values
        testContext.assertEquals("new_address", json.getString(PROP_ADDRESS));
        testContext.assertEquals("new_loggerName", json.getString(PROP_LOGGER_NAME));
        testContext.assertEquals("new_strategyHeader", json.getString(PROP_STRATEGY_HEADER));
    }

    @Test
    public void testGetDefaultFromJsonObject(TestContext testContext){
        JsonObject json  = new Configuration().asJsonObject();
        Configuration config = fromJsonObject(json);

        testContext.assertEquals("swisspush.logtransformer", config.getAddress());
        testContext.assertEquals("LogTransformerLogger", config.getLoggerName());
        testContext.assertEquals("transformStrategy", config.getStrategyHeader());
    }

    @Test
    public void testGetOverriddenFromJsonObject(TestContext testContext){
        JsonObject json = new JsonObject();
        json.put(PROP_ADDRESS, "new_address");
        json.put(PROP_LOGGER_NAME, "new_loggerName");
        json.put(PROP_STRATEGY_HEADER, "new_strategyHeader");

        Configuration config = fromJsonObject(json);
        testContext.assertEquals("new_address", config.getAddress());
        testContext.assertEquals("new_loggerName", config.getLoggerName());
        testContext.assertEquals("new_strategyHeader", config.getStrategyHeader());
    }
}

package org.swisspush.logtransformer.util;

import io.vertx.core.json.JsonObject;

/**
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public class Configuration {
    private String address;
    private String loggerName;
    private String strategyHeader;

    public static final String PROP_ADDRESS = "address";
    public static final String PROP_LOGGER_NAME = "loggerName";
    public static final String PROP_STRATEGY_HEADER = "strategyHeader";

    /**
     * Constructor with default values. Use the {@link ConfigurationBuilder} class
     * for simplified custom configuration.
     */
    public Configuration(){
        this(new ConfigurationBuilder());
    }

    public Configuration(String address, String loggerName, String strategyHeader) {
        this.address = address;
        this.loggerName = loggerName;
        this.strategyHeader = strategyHeader;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public String getAddress() {
        return address;
    }

    public String getStrategyHeader() { return strategyHeader; }

    public static ConfigurationBuilder with(){
        return new ConfigurationBuilder();
    }

    public JsonObject asJsonObject(){
        JsonObject obj = new JsonObject();
        obj.put(PROP_ADDRESS, getAddress());
        obj.put(PROP_LOGGER_NAME, getLoggerName());
        obj.put(PROP_STRATEGY_HEADER, getStrategyHeader());
        return obj;
    }

    public static Configuration fromJsonObject(JsonObject json){
        ConfigurationBuilder builder = Configuration.with();
        if(json.containsKey(PROP_ADDRESS)){
            builder.address(json.getString(PROP_ADDRESS));
        }
        if(json.containsKey(PROP_LOGGER_NAME)){
            builder.loggerName(json.getString(PROP_LOGGER_NAME));
        }
        if(json.containsKey(PROP_STRATEGY_HEADER)){
            builder.strategyHeader(json.getString(PROP_STRATEGY_HEADER));
        }
        return builder.build();
    }

    private Configuration(ConfigurationBuilder builder){
        this(builder.address, builder.loggerName, builder.strategyHeader);
    }

    @Override
    public String toString() {
        return asJsonObject().toString();
    }

    /**
     * ConfigurationBuilder class for simplified configuration.
     *
     * <pre>Usage:</pre>
     * <pre>
     * Configuration config = Configuration.with()
     *      .address("eventBus_address")
     *      .loggerName("myLogTransformerLogger")
     *      .strategyHeader("myCustomStrategyHeader")
     *      .build();
     * </pre>
     */
    public static class ConfigurationBuilder {
        private String address;
        private String loggerName;
        private String strategyHeader;

        public ConfigurationBuilder() {
            this.address = "swisspush.logtransformer";
            this.loggerName = "LogTransformerLogger";
            this.strategyHeader = "transformStrategy";
        }

        public ConfigurationBuilder address(String address){
            this.address = address;
            return this;
        }

        public ConfigurationBuilder loggerName(String loggerName){
            this.loggerName = loggerName;
            return this;
        }

        public ConfigurationBuilder strategyHeader(String strategyHeader){
            this.strategyHeader = strategyHeader;
            return this;
        }

        public Configuration build(){
            return new Configuration(this);
        }
    }
}

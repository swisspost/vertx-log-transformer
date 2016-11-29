# vertx-log-transformer

[![Build Status](https://drone.io/github.com/swisspush/vertx-log-transformer/status.png)](https://drone.io/github.com/swisspush/vertx-log-transformer/latest)

Transforms log content received over the Vert.x [EventBus](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/EventBus.html) and writes the transformed logs to the configured destination.

To use the vertx-log-transformer, the log content has to be sent over the EventBus to the configured address. To select the log transform strategy, the transform strategy name has to be provided in the [DeliveryOptions](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/DeliveryOptions.html) headers as property with the configured _strategyHeader_ value.

An example of how to send the logs to the vertx-log-transformer can be seen below:

```java
DeliveryOptions options = new DeliveryOptions();
options.setHeaders(new CaseInsensitiveHeaders().add("strategyHeader", "myLogTransformStrategy"));

Vertx.vertx().eventBus().publish("swisspush.logtransformer", "log content to transform", options);
```

> When no (or an unknown) log transform strategy is provided, the _DoNothingTransformStrategy_ is applied which logs the original (not transformed) log input !

## Write custom log transform strategies
To implement a custom log transform strategy create a new Class implementing [TransformStrategy](src/main/java/org/swisspush/logtransformer/strategy/TransformStrategy.java) interface or extending the [AbstractTransformStrategy](src/main/java/org/swisspush/logtransformer/strategy/AbstractTransformStrategy.java) class.

The [DoNothingTransformStrategy](src/main/java/org/swisspush/logtransformer/strategy/DoNothingTransformStrategy.java) is a very simple example of a log transform strategy:

```java
public class DoNothingTransformStrategy implements TransformStrategy {

    private Vertx vertx;

    public DoNothingTransformStrategy(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void transformLog(String logToTransform, Handler<AsyncResult<List<String>>> resultHandler) {
        vertx.executeBlocking(future -> future.complete(Collections.singletonList(logToTransform)), resultHandler);
    }
}
```

By extending the [AbstractTransformStrategy](src/main/java/org/swisspush/logtransformer/strategy/AbstractTransformStrategy.java) class, basic functionality like error handling and JSON parsing are available.

When writing a new custom log transform strategy also extend the _**findTransformStrategy(MultiMap headers)**_ method of [DefaultTransformStrategyFinder](src/main/java/org/swisspush/logtransformer/strategy/DefaultTransformStrategyFinder.java) class (or write a new one) to match the provided strategy name (_strategyHeader_) to the strategy implementation.

Example:
```java
if("MyNewCustomLogTransformStrategy".equalsIgnoreCase(headers.get("strategyHeader"))){
    return new MyNewCustomLogTransformStrategy();
}
```

## Configuration
The following configuration values are available:
```
{
    "address": "swisspush.logtransformer",      // The event bus address to listen on
    "loggerName": "LogTransformerLogger",       // The name of the logger to write the logfile to
    "strategyHeader": "transformStrategy"       // The name of the header property containing the strategy
}
```

### Configuration util
The configurations have to be passed as JsonObject to the module. For a simplyfied configuration the ConfigurationBuilder can be used.

Example:

```java
Configuration config = Configuration.with()
        .address("some_eventbus_address")
        .loggerName("MyCustomLogger")
        .strategyHeader("customStrategy")
        .build();

JsonObject json = config.asJsonObject();
```

### Log4j configuration
The vertx-log-transfomer module requires a configured **_loggerName_** (default is 'LogTransformerLogger'). This loggerName has to be used in the log4j configuration.

A log4j configuration example:
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">

<log4j:configuration debug="false"
    xmlns:log4j='http://jakarta.apache.org/log4j/'>

    <loggername="LogTransformerLogger"additivity="false">
        <appender-refref="LogTransformerFileAppender"/>
    </logger>

    <appendername="LogTransformerFileAppender"class="org.apache.log4j.DailyRollingFileAppender">
        <paramname="File"value="/path/to/logfiles/logTransformer.log"/>
        <paramname="Encoding"value="UTF-8"/>
        <paramname="Append"value="true"/>
        <layoutclass="org.apache.log4j.EnhancedPatternLayout">
            <paramname="ConversionPattern" value="%m%n"/>
        </layout>
    </appender>

</log4j:configuration>
```


## Use gradle with alternative repositories

As standard the default maven repositories are set.
You can overwrite these repositories by setting these properties (`-Pproperty=value`):

* `repository` this is the repository where resources are fetched
* `uploadRepository` the repository used in `uploadArchives`
* `repoUsername` the username for uploading archives
* `repoPassword` the password for uploading archives
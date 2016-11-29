package org.swisspush.logtransformer.strategy;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.swisspush.logtransformer.util.ResourcesUtils;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.swisspush.logtransformer.strategy.SplitStorageExpandLogStrategy.*;

/**
 * Tests for the {@link SplitStorageExpandLogStrategy} class
 *
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
@RunWith(VertxUnitRunner.class)
public class SplitStorageExpandLogStrategyTest {

    private SplitStorageExpandLogStrategy strategy;
    private Vertx vertx;

    private final String VALID_LOG_RESOURCE = ResourcesUtils.loadResource("valid_storageExpand_resource", true);

    @Before
    public void setUp(){
        vertx = Mockito.spy(Vertx.vertx());
        strategy = Mockito.spy(new SplitStorageExpandLogStrategy(vertx));
    }

    @Test
    public void testJsonLogContentNull(TestContext context){
        Async async = context.async();
        strategy.transformLog(null, res -> {
            context.assertTrue(res.succeeded());
            context.assertEquals(0, res.result().size());
            verify(strategy, times(1)).doNothingInCaseOfError(eq(null), eq("Log was null and therefore could not be converted to JSON"));
            async.complete();
        });
    }

    @Test
    public void testNonJsonLogContent(TestContext context){
        Async async = context.async();
        strategy.transformLog("a non-json log entry", res -> {
            context.assertTrue(res.succeeded());
            context.assertEquals(1, res.result().size());
            context.assertEquals("a non-json log entry", res.result().get(0));
            verify(strategy, times(1)).doNothingInCaseOfError(eq("a non-json log entry"), eq("Log could not be converted to JSON"));
            async.complete();
        });
    }

    @Test
    public void testCorrectStorageExpandLogContentShouldNotResultInError(TestContext context){
        Async async = context.async();
        strategy.transformLog(VALID_LOG_RESOURCE, res -> {
            context.assertTrue(res.succeeded());
            verify(strategy, never()).doNothingInCaseOfError(anyString(), anyString());
            async.complete();
        });
    }

    @Test
    public void testNoUrlPropertyInLogContent(TestContext context){
        Async async = context.async();
        JsonObject input = getValidLogInput();
        input.remove(PROP_URL);
        validateUrlProperty(async, context, input);
    }

    @Test
    public void testInvalidUrlPropertyInLogContent(TestContext context){
        Async async = context.async();
        JsonObject input = getValidLogInput();
        input.put(PROP_URL, "/some/url/without/storageexpand/param");
        validateUrlProperty(async, context, input);
    }

    private void validateUrlProperty(Async async, TestContext context, JsonObject input){
        strategy.transformLog(input.encode(), res -> {
            context.assertTrue(res.succeeded());
            context.assertEquals(1, res.result().size());
            verify(strategy, times(1)).doNothingInCaseOfError(eq(input.encode()), eq("Property 'url' is missing or has invalid content"));
            async.complete();
        });
    }

    @Test
    public void testInvalidUrlPropertyTypeInLogContent(TestContext context){
        Async async = context.async();
        JsonObject input = getValidLogInput();
        input.put(PROP_URL, new JsonObject());
        strategy.transformLog(input.encode(), res -> {
            context.assertTrue(res.succeeded());
            context.assertEquals(1, res.result().size());
            verify(strategy, times(1)).doNothingInCaseOfError(eq(input.encode()), eq("Property 'url' has an unexpected type"));
            async.complete();
        });
    }

    @Test
    public void testNoResponsePropertyInLogContent(TestContext context){
        Async async = context.async();
        JsonObject input = getValidLogInput();
        input.remove(PROP_RESPONSE);
        strategy.transformLog(input.encode(), res -> {
            context.assertTrue(res.succeeded());
            context.assertEquals(1, res.result().size());
            verify(strategy, times(1)).doNothingInCaseOfError(eq(input.encode()), eq("Property 'response' is missing or has invalid content"));
            async.complete();
        });
    }

    @Test
    public void testResponsePropertyWrongTypeInLogContent(TestContext context){
        Async async = context.async();
        JsonObject input = getValidLogInput();
        input.put(PROP_RESPONSE, 1234);
        strategy.transformLog(input.encode(), res -> {
            context.assertTrue(res.succeeded());
            context.assertEquals(1, res.result().size());
            verify(strategy, times(1)).doNothingInCaseOfError(anyString(), eq("Property 'response' has an unexpected type"));
            async.complete();
        });
    }

    @Test
    public void testNoResponseBodyPropertyInLogContent(TestContext context){
        Async async = context.async();
        JsonObject input = getValidLogInput();
        input.getJsonObject(PROP_RESPONSE).remove(PROP_BODY);
        strategy.transformLog(input.encode(), res -> {
            context.assertTrue(res.succeeded());
            context.assertEquals(1, res.result().size());
            verify(strategy, times(1)).doNothingInCaseOfError(anyString(), eq("Property 'response.body' is missing or has invalid content"));
            async.complete();
        });
    }

    @Test
    public void testResponseBodyPropertyWrongTypeInLogContent(TestContext context){
        Async async = context.async();
        JsonObject input = getValidLogInput();
        input.getJsonObject(PROP_RESPONSE).put(PROP_BODY, 1234);
        strategy.transformLog(input.encode(), res -> {
            context.assertTrue(res.succeeded());
            context.assertEquals(1, res.result().size());
            verify(strategy, times(1)).doNothingInCaseOfError(anyString(), eq("Property 'response.body' has an unexpected type"));
            async.complete();
        });
    }

    @Test
    public void testTransformedLogOutput(TestContext context){
        Async async = context.async();
        strategy.transformLog(VALID_LOG_RESOURCE, res -> {
            context.assertTrue(res.succeeded());
            verify(strategy, never()).doNothingInCaseOfError(anyString(), anyString());
            context.assertEquals(3, res.result().size());
            verifyLogEntry(context, res.result().get(0), 70000009);
            verifyLogEntry(context, res.result().get(1), 70000008);
            verifyLogEntry(context, res.result().get(2), 70000007);

            async.complete();
        });

    }

    private void verifyLogEntry(TestContext context, String logEntry, int dataId){
        try{
            JsonObject obj = new JsonObject(logEntry);

            context.assertEquals(obj.getString(PROP_METHOD), "GET");

            context.assertTrue(obj.getString(PROP_URL).endsWith(String.valueOf(dataId)));
            context.assertFalse(obj.getString(PROP_URL).contains("/" + PARAM_STORAGE_EXPAND));

            context.assertTrue(obj.containsKey(PROP_RESPONSE));
            context.assertFalse(obj.getJsonObject(PROP_RESPONSE).containsKey(PROP_HEADERS));
            context.assertTrue(obj.getJsonObject(PROP_RESPONSE).containsKey(PROP_BODY));
            context.assertEquals(obj.getJsonObject(PROP_RESPONSE).getJsonObject(PROP_BODY).getInteger("dataId"), dataId);

            context.assertTrue(obj.containsKey(PROP_REQUEST));
            context.assertTrue(obj.getJsonObject(PROP_REQUEST).containsKey(PROP_HEADERS));
            context.assertFalse(obj.getJsonObject(PROP_REQUEST).containsKey(PROP_BODY));

        } catch (Exception ex){
            context.fail(ex);
        }
    }

    private JsonObject getValidLogInput(){
        return new JsonObject(VALID_LOG_RESOURCE);
    }
}

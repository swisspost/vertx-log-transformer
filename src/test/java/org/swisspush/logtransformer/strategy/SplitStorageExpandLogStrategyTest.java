package org.swisspush.logtransformer.strategy;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.swisspush.logtransformer.util.ResourcesUtils;

import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.swisspush.logtransformer.strategy.SplitStorageExpandLogStrategy.*;

/**
 * Tests for the {@link SplitStorageExpandLogStrategy} class
 *
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
@RunWith(VertxUnitRunner.class)
public class SplitStorageExpandLogStrategyTest {

    private SplitStorageExpandLogStrategy strategy;

    private final String VALID_LOG_RESOURCE = ResourcesUtils.loadResource("valid_storageExpand_resource", true);

    @Before
    public void setUp(){
        strategy = Mockito.spy(new SplitStorageExpandLogStrategy());
    }

    @Test
    public void testJsonLogContentNull(TestContext context){
        List<String> transformedLogs = strategy.transformLog(null);
        context.assertEquals(0, transformedLogs.size());
        verify(strategy, times(1)).doNothingInCaseOfError(eq(null), eq("Log was null and therefore could not be converted to JSON"));
    }

    @Test
    public void testNonJsonLogContent(TestContext context){
        List<String> transformedLogs = strategy.transformLog("a non-json log entry");
        context.assertEquals(1, transformedLogs.size());
        context.assertEquals("a non-json log entry",transformedLogs.get(0));
        verify(strategy, times(1)).doNothingInCaseOfError(eq("a non-json log entry"), eq("Log could not be converted to JSON"));
    }

    @Test
    public void testCorrectStorageExpandLogContentShouldNotResultInError(TestContext context){
        strategy.transformLog(VALID_LOG_RESOURCE);
        verify(strategy, never()).doNothingInCaseOfError(anyString(), anyString());
    }

    @Test
    public void testNoUrlPropertyInLogContent(TestContext context){
        JsonObject input = getValidLogInput();
        input.remove(PROP_URL);
        List<String> transformedLogs = strategy.transformLog(input.encode());
        context.assertEquals(1, transformedLogs.size());
        verify(strategy, times(1)).doNothingInCaseOfError(eq(input.encode()), eq("Property 'url' is missing or has invalid content"));
    }

    @Test
    public void testInvalidUrlPropertyInLogContent(TestContext context){
        JsonObject input = getValidLogInput();
        input.put(PROP_URL, "/some/url/without/storageexpand/param");
        List<String> transformedLogs = strategy.transformLog(input.encode());
        context.assertEquals(1, transformedLogs.size());
        verify(strategy, times(1)).doNothingInCaseOfError(eq(input.encode()), eq("Property 'url' is missing or has invalid content"));
    }

    @Test
    public void testInvalidUrlPropertyTypeInLogContent(TestContext context){
        JsonObject input = getValidLogInput();
        input.put(PROP_URL, new JsonObject());
        List<String> transformedLogs = strategy.transformLog(input.encode());
        context.assertEquals(1, transformedLogs.size());
        verify(strategy, times(1)).doNothingInCaseOfError(eq(input.encode()), eq("Property 'url' has an unexpected type"));
    }

    @Test
    public void testNoResponsePropertyInLogContent(TestContext context){
        JsonObject input = getValidLogInput();
        input.remove(PROP_RESPONSE);
        List<String> transformedLogs = strategy.transformLog(input.encode());
        context.assertEquals(1, transformedLogs.size());
        verify(strategy, times(1)).doNothingInCaseOfError(eq(input.encode()), eq("Property 'response' is missing or has invalid content"));
    }

    @Test
    public void testResponsePropertyWrongTypeInLogContent(TestContext context){
        JsonObject input = getValidLogInput();
        input.put(PROP_RESPONSE, 1234);
        List<String> transformedLogs = strategy.transformLog(input.encode());
        context.assertEquals(1, transformedLogs.size());
        verify(strategy, times(1)).doNothingInCaseOfError(anyString(), eq("Property 'response' has an unexpected type"));
    }

    @Test
    public void testNoResponseBodyPropertyInLogContent(TestContext context){
        JsonObject input = getValidLogInput();
        input.getJsonObject(PROP_RESPONSE).remove(PROP_BODY);
        List<String> transformedLogs = strategy.transformLog(input.encode());
        context.assertEquals(1, transformedLogs.size());
        verify(strategy, times(1)).doNothingInCaseOfError(anyString(), eq("Property 'response.body' is missing or has invalid content"));
    }

    @Test
    public void testResponseBodyPropertyWrongTypeInLogContent(TestContext context){
        JsonObject input = getValidLogInput();
        input.getJsonObject(PROP_RESPONSE).put(PROP_BODY, 1234);
        List<String> transformedLogs = strategy.transformLog(input.encode());
        context.assertEquals(1, transformedLogs.size());
        verify(strategy, times(1)).doNothingInCaseOfError(anyString(), eq("Property 'response.body' has an unexpected type"));
    }

    @Test
    public void testTransformedLogOutput(TestContext context){
        List<String> transformedLogs = strategy.transformLog(VALID_LOG_RESOURCE);
        verify(strategy, never()).doNothingInCaseOfError(anyString(), anyString());

        context.assertEquals(3, transformedLogs.size());
        verifyLogEntry(context, transformedLogs.get(0), 70000009);
        verifyLogEntry(context, transformedLogs.get(1), 70000008);
        verifyLogEntry(context, transformedLogs.get(2), 70000007);
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

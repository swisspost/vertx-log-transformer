package org.swisspush.logtransformer.strategy;

import io.vertx.core.json.JsonObject;

import java.util.*;

/**
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public class SplitStorageExpandLogStrategy extends AbstractTransformStrategy {

    public static final String PROP_URL = "url";
    public static final String PROP_METHOD = "method";
    public static final String PROP_HEADERS = "headers";
    public static final String PROP_REQUEST = "request";
    public static final String PROP_RESPONSE = "response";
    public static final String PROP_BODY = "body";
    public static final String PARAM_STORAGE_EXPAND = "?storageExpand=true";

    @Override
    public List<String> transformLog(String logToTransform) {
        try {
            List<String> logEntries = new ArrayList<>();
            JsonObject storageExpandLog = verifyLogInput(logToTransform);
            Map<String, JsonObject> subRequestsMap = extractSubLogEntries(storageExpandLog);
            JsonObject skeleton = buildLogEntryJsonObjectSkeleton(storageExpandLog);
            for (Map.Entry<String, JsonObject> subRequest : subRequestsMap.entrySet()) {
                JsonObject subRequestObj = skeleton.copy();
                subRequestObj.put(PROP_URL, subRequestObj.getString(PROP_URL).replace(PARAM_STORAGE_EXPAND, subRequest.getKey()));
                subRequestObj.getJsonObject(PROP_RESPONSE).put(PROP_BODY, subRequest.getValue());
                logEntries.add(subRequestObj.encode());
            }
            return logEntries;
        } catch (LogContentException ex) {
            return doNothingInCaseOfError(logToTransform, ex.getMessage());
        }
    }

    private JsonObject verifyLogInput(String logToTransform) throws LogContentException {
        JsonObject storageExpandLog = parseStringLogToJsonObject(logToTransform);

        try {
            if(!storageExpandLog.containsKey(PROP_URL) || storageExpandLog.getString(PROP_URL) == null || !storageExpandLog.getString(PROP_URL).endsWith("/" + PARAM_STORAGE_EXPAND)){
                throw new LogContentException("Property '"+PROP_URL+"' is missing or has invalid content");
            }
        } catch (ClassCastException ex) {
            throw new LogContentException("Property '"+PROP_URL+"' has an unexpected type");
        }

        try {
            if (!storageExpandLog.containsKey(PROP_RESPONSE) || storageExpandLog.getJsonObject(PROP_RESPONSE) == null) {
                throw new LogContentException("Property '"+PROP_RESPONSE+"' is missing or has invalid content");
            }
        } catch (ClassCastException ex) {
            throw new LogContentException("Property '"+PROP_RESPONSE+"' has an unexpected type");
        }

        try {
            if (!storageExpandLog.getJsonObject(PROP_RESPONSE).containsKey(PROP_BODY) || storageExpandLog.getJsonObject(PROP_RESPONSE).getJsonObject(PROP_BODY) == null) {
                throw new LogContentException("Property '"+PROP_RESPONSE+"."+PROP_BODY+"' is missing or has invalid content");
            }
        } catch (ClassCastException ex) {
            throw new LogContentException("Property '"+PROP_RESPONSE+"."+PROP_BODY+"' has an unexpected type");
        }

        try {
            if (!storageExpandLog.containsKey(PROP_REQUEST) || storageExpandLog.getJsonObject(PROP_REQUEST) == null) {
                throw new LogContentException("Property '"+PROP_REQUEST+"' is missing or has invalid content");
            }
        } catch (ClassCastException ex) {
            throw new LogContentException("Property '"+PROP_REQUEST+"' has an unexpected type");
        }

        return storageExpandLog;
    }

    private Map<String, JsonObject> extractSubLogEntries(JsonObject storageExpandLog) throws LogContentException {
        try {
            Map<String, JsonObject> map = new HashMap<>();
            JsonObject body = storageExpandLog.getJsonObject(PROP_RESPONSE).getJsonObject(PROP_BODY);

            Set<String> messages = body.fieldNames();
            for (String message : messages) {
                JsonObject messageObject = body.getJsonObject(message);
                if(messageObject == null){
                    throw new LogContentException("No message body found");
                }
                map.put(message, messageObject);
            }
            return map;
        } catch (ClassCastException ex){
            throw new LogContentException("Unexpected property type found");
        }
    }

    private JsonObject buildLogEntryJsonObjectSkeleton(JsonObject storageExpandLog) {
        JsonObject skeleton = storageExpandLog.copy();
        skeleton.put(PROP_METHOD, "GET");
        skeleton.getJsonObject(PROP_REQUEST).remove(PROP_BODY);
        skeleton.getJsonObject(PROP_RESPONSE).remove(PROP_HEADERS);
        skeleton.getJsonObject(PROP_RESPONSE).remove(PROP_BODY);
        return skeleton;
    }
}

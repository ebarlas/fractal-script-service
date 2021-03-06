package org.barlas.fractal.service.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import org.barlas.fractal.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DynamoTagService implements TagService {

    private static final String SCRIPT_ID = "scriptId";
    private static final String USER_ID = "userId";
    private static final String TAG_NAME = "tagName";
    private static final String INDEX_SUFFIX = "-index";

    @Value("${tagsTableName}")
    private String tagsTableName;
    @Autowired
    private AmazonDynamoDBClient dynamo;

    public void createTags(String userId, String scriptId, Set<String> tagNames) {
        Map<String, List<WriteRequest>> requestItems = new HashMap<String, List<WriteRequest>>();
        List<WriteRequest> tagList = new ArrayList<WriteRequest>();
        requestItems.put(tagsTableName, tagList);

        for(String tagName : tagNames) {
            Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            item.put(SCRIPT_ID, new AttributeValue(scriptId));
            item.put(USER_ID, new AttributeValue(userId));
            item.put(TAG_NAME, new AttributeValue(tagName));
            tagList.add(new WriteRequest().withPutRequest(new PutRequest().withItem(item)));
        }

        BatchWriteItemResult result;
        BatchWriteItemRequest batchWriteItemRequest = new BatchWriteItemRequest().withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);

        do {
            batchWriteItemRequest.withRequestItems(requestItems);
            result = dynamo.batchWriteItem(batchWriteItemRequest);
            requestItems = result.getUnprocessedItems();
        } while (result.getUnprocessedItems().size() > 0);
    }

    public Map<String, Set<String>> getTagNames(String userId) {
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(userId));

        Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        keyConditions.put(USER_ID, condition);

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(tagsTableName)
                .withIndexName(USER_ID + INDEX_SUFFIX)
                .withKeyConditions(keyConditions)
                .withAttributesToGet(SCRIPT_ID, TAG_NAME);

        QueryResult result = dynamo.query(queryRequest);

        List<Map<String, AttributeValue>> items = result.getItems();
        if(items == null || items.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        for(Map<String, AttributeValue> item : items) {
            String scriptId = item.get(SCRIPT_ID).getS();

            Set<String> set = map.get(scriptId);
            if(set == null) {
                set = new HashSet<String>();
                map.put(scriptId, set);
            }

            set.add(item.get(TAG_NAME).getS());
        }

        return map;
    }

    public Set<String> getTags(String scriptId) {
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(scriptId));

        Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        keyConditions.put(SCRIPT_ID, condition);

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(tagsTableName)
                .withKeyConditions(keyConditions)
                .withAttributesToGet(TAG_NAME);

        QueryResult result = dynamo.query(queryRequest);

        List<Map<String, AttributeValue>> items = result.getItems();
        if(items == null || items.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> set = new HashSet<String>();
        for(Map<String, AttributeValue> item : items) {
            set.add(item.get(TAG_NAME).getS());
        }

        return set;
    }

}

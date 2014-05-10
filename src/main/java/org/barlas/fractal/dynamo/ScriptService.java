package org.barlas.fractal.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import org.barlas.fractal.domain.Script;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptService {

    private static final String SCRIPTS = "scripts";
    private static final String SCRIPT_ID = "scriptId";
    private static final String USER_ID = "userId";
    private static final String SCRIPT = "script";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String INDEX_SUFFIX = "-index";

    @Autowired
    private AmazonDynamoDBClient dynamo;

    public void createScript(Script script) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put(SCRIPT_ID, new AttributeValue(script.getId()));
        item.put(USER_ID, new AttributeValue(script.getUserId()));
        item.put(SCRIPT, new AttributeValue(script.getScript()));
        item.put(NAME, new AttributeValue(script.getName()));
        item.put(DESCRIPTION, new AttributeValue(script.getDescription()));

        PutItemRequest putItemRequest = new PutItemRequest(SCRIPTS, item);
        dynamo.putItem(putItemRequest);
    }

    public List<Script> getScripts(String userId) {
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(userId));

        Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        keyConditions.put(USER_ID, condition);

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(SCRIPTS)
                .withIndexName(USER_ID + INDEX_SUFFIX)
                .withKeyConditions(keyConditions)
                .withSelect()
                .withAttributesToGet(Arrays.asList(SCRIPT_ID, SCRIPT, NAME, DESCRIPTION));

        QueryResult result = dynamo.query(queryRequest);

        List<Script> scripts = new ArrayList<Script>();
        for (Map<String, AttributeValue> item : result.getItems()) {
            Script script = new Script();
            script.setUserId(userId);
            script.setId(item.get(SCRIPT_ID).getS());
            script.setScript(item.get(SCRIPT).getS());
            script.setName(item.get(NAME).getS());
            script.setDescription(item.get(DESCRIPTION).getS());
            scripts.add(script);
        }

        return scripts;
    }

}

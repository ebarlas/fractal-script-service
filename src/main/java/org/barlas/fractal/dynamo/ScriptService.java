package org.barlas.fractal.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import org.barlas.fractal.domain.Script;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class ScriptService {

    private static final String SCRIPTS = "scripts";
    private static final String SCRIPT_ID = "scriptId";
    private static final String USER_ID = "userId";
    private static final String SCRIPT = "script";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";

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

}

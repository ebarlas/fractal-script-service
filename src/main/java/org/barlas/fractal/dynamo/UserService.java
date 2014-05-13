package org.barlas.fractal.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import org.barlas.fractal.domain.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {

    private static final String USERS = "users";
    private static final String USER_ID = "userId";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String INDEX_SUFFIX = "-index";

    @Autowired
    private AmazonDynamoDBClient dynamo;

    public void createUser(User user) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put(USER_ID, new AttributeValue(user.getId()));
        item.put(NAME, new AttributeValue(user.getName()));
        item.put(EMAIL, new AttributeValue(user.getEmail()));

        PutItemRequest putItemRequest = new PutItemRequest(USERS, item);
        dynamo.putItem(putItemRequest);
    }

    public User getUserByEmail(String email) {
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(email));

        Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        keyConditions.put(EMAIL, condition);

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(USERS)
                .withIndexName(EMAIL + INDEX_SUFFIX)
                .withKeyConditions(keyConditions)
                .withLimit(1)
                .withAttributesToGet(Arrays.asList(NAME, USER_ID));

        QueryResult result = dynamo.query(queryRequest);

        List<Map<String, AttributeValue>> items = result.getItems();
        if(items == null || items.isEmpty()) {
            return null;
        }

        Map<String, AttributeValue> item = items.get(0);

        AttributeValue nameValue = item.get(NAME);
        AttributeValue userIdValue = item.get(USER_ID);

        User user = new User();
        user.setId(userIdValue.getS());
        user.setName(nameValue.getS());
        user.setEmail(email);
        return user;
    }

}

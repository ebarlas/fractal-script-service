package org.barlas.fractal.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import org.barlas.fractal.domain.SocialNetwork;
import org.barlas.fractal.domain.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class UserService {

    private static final String USERS = "users";
    private static final String USER_ID = "userId";
    private static final String DISPLAY_NAME = "displayName";

    private static final String SOCIAL_NETWORKS = "socialNetworks";
    private static final String SOCIAL_ID = "socialId";
    private static final String NETWORK = "network";

    @Autowired
    private AmazonDynamoDBClient dynamo;

    public void createUser(User user) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put(USER_ID, new AttributeValue(user.getId()));
        item.put(DISPLAY_NAME, new AttributeValue(user.getDisplayName()));

        PutItemRequest putItemRequest = new PutItemRequest(USERS, item);
        dynamo.putItem(putItemRequest);
    }

    public User getUser(String userId) {
        Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put(USER_ID, new AttributeValue().withS(userId));

        GetItemRequest getItemRequest = new GetItemRequest()
                .withTableName("users")
                .withKey(key)
                .withAttributesToGet(DISPLAY_NAME);

        GetItemResult result = dynamo.getItem(getItemRequest);
        Map<String, AttributeValue> item = result.getItem();
        if(item == null || item.isEmpty()) {
            return null;
        }

        AttributeValue value = item.get(DISPLAY_NAME);

        User user = new User();
        user.setId(userId);
        user.setDisplayName(value.getS());
        return user;
    }

    public String getUserId(String socialId) {
        Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put(SOCIAL_ID, new AttributeValue().withS(socialId));

        GetItemRequest getItemRequest = new GetItemRequest()
                .withTableName(SOCIAL_NETWORKS)
                .withKey(key)
                .withAttributesToGet(USER_ID);

        GetItemResult result = dynamo.getItem(getItemRequest);
        Map<String, AttributeValue> item = result.getItem();
        if(item == null || item.isEmpty()) {
            return null;
        }

        AttributeValue value = item.get(USER_ID);
        return value.getS();
    }

    public void createSocialNetwork(SocialNetwork network) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put(SOCIAL_ID, new AttributeValue(network.getId()));
        item.put(USER_ID, new AttributeValue(network.getUserId()));
        item.put(NETWORK, new AttributeValue(network.getNetwork()));

        PutItemRequest putItemRequest = new PutItemRequest(SOCIAL_NETWORKS, item);
        dynamo.putItem(putItemRequest);
    }

}

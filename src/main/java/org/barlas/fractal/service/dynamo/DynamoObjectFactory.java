package org.barlas.fractal.service.dynamo;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import org.springframework.beans.factory.FactoryBean;

public class DynamoObjectFactory implements FactoryBean<AmazonDynamoDBClient> {

    @Override
    public Class<?> getObjectType() {
        return AmazonDynamoDBClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public AmazonDynamoDBClient getObject() throws Exception {
        AmazonDynamoDBClient dynamo = new AmazonDynamoDBClient(new ClasspathPropertiesFileCredentialsProvider());
        dynamo.setRegion(Region.getRegion(Regions.US_WEST_1));
        return dynamo;
    }
}

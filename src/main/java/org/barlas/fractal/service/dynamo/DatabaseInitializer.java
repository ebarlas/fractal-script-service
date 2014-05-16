package org.barlas.fractal.service.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.Tables;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class DatabaseInitializer {

    private static final Logger logger = Logger.getLogger(DatabaseInitializer.class);

    private static final String indexSuffix = "-index";

    private static final String usersTable = "users";
    private static final String userId = "userId";
    private static final String email = "email";
    
    private static final String tagsTable = "tags";
    private static final String tagName = "tagName";
    
    private static final String scriptsTable = "scripts";
    private static final String scriptId = "scriptId";

    @Autowired
    private AmazonDynamoDBClient dynamo;

    @PostConstruct
    public void init() {
        createUsersTable();
        createTagsTable();
        createScriptsTable();
    }

    private void createTagsTable() {
        if (Tables.doesTableExist(dynamo, tagsTable)) {
            logger.info("Table " + tagsTable + " is already ACTIVE");
            return;
        }

        CreateTableRequest createTableRequest = new CreateTableRequest()
                .withTableName(tagsTable)
                .withKeySchema(
                        new KeySchemaElement()
                                .withAttributeName(scriptId)
                                .withKeyType(KeyType.HASH),
                        new KeySchemaElement()
                                .withAttributeName(tagName)
                                .withKeyType(KeyType.RANGE))
                .withGlobalSecondaryIndexes(
                        new GlobalSecondaryIndex()
                                .withIndexName(tagName + indexSuffix)
                                .withKeySchema(new KeySchemaElement().withKeyType(KeyType.HASH).withAttributeName(tagName))
                                .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L))
                )
                .withGlobalSecondaryIndexes(
                        new GlobalSecondaryIndex()
                                .withIndexName(userId + indexSuffix)
                                .withKeySchema(new KeySchemaElement().withKeyType(KeyType.HASH).withAttributeName(userId))
                                .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L))
                )
                .withAttributeDefinitions(
                        new AttributeDefinition()
                                .withAttributeName(scriptId)
                                .withAttributeType(ScalarAttributeType.S),
                        new AttributeDefinition()
                                .withAttributeName(tagName)
                                .withAttributeType(ScalarAttributeType.S),
                        new AttributeDefinition()
                                .withAttributeName(userId)
                                .withAttributeType(ScalarAttributeType.S))
                .withProvisionedThroughput(
                        new ProvisionedThroughput()
                                .withReadCapacityUnits(1L)
                                .withWriteCapacityUnits(1L));

        TableDescription createdTableDescription = dynamo.createTable(createTableRequest).getTableDescription();
        logger.info("Created Table: " + createdTableDescription);

        // Wait for it to become active
        logger.info("Waiting for " + tagsTable + " to become ACTIVE...");
        Tables.waitForTableToBecomeActive(dynamo, tagsTable);
    }

    private void createUsersTable() {
        if (Tables.doesTableExist(dynamo, usersTable)) {
            logger.info("Table " + usersTable + " is already ACTIVE");
            return;
        }

        CreateTableRequest createTableRequest = new CreateTableRequest()
                .withTableName(usersTable)
                .withKeySchema(
                        new KeySchemaElement()
                                .withAttributeName(userId)
                                .withKeyType(KeyType.HASH))
                .withGlobalSecondaryIndexes(
                        new GlobalSecondaryIndex()
                                .withIndexName(email + indexSuffix)
                                .withKeySchema(new KeySchemaElement().withKeyType(KeyType.HASH).withAttributeName(email))
                                .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L))
                )
                .withAttributeDefinitions(
                        new AttributeDefinition()
                                .withAttributeName(userId)
                                .withAttributeType(ScalarAttributeType.S),
                        new AttributeDefinition()
                                .withAttributeName(email)
                                .withAttributeType(ScalarAttributeType.S)
                )
                .withProvisionedThroughput(
                        new ProvisionedThroughput()
                                .withReadCapacityUnits(1L)
                                .withWriteCapacityUnits(1L)
                );

        TableDescription createdTableDescription = dynamo.createTable(createTableRequest).getTableDescription();
        logger.info("Created Table: " + createdTableDescription);

        // Wait for it to become active
        logger.info("Waiting for " + usersTable + " to become ACTIVE...");
        Tables.waitForTableToBecomeActive(dynamo, usersTable);
    }

    private void createScriptsTable() {
        if (Tables.doesTableExist(dynamo, scriptsTable)) {
            logger.info("Table " + scriptsTable + " is already ACTIVE");
            return;
        }

        CreateTableRequest createTableRequest = new CreateTableRequest()
                .withTableName(scriptsTable)
                .withKeySchema(
                        new KeySchemaElement()
                                .withAttributeName(scriptId)
                                .withKeyType(KeyType.HASH))
                .withGlobalSecondaryIndexes(
                        new GlobalSecondaryIndex()
                                .withIndexName(userId + indexSuffix)
                                .withKeySchema(new KeySchemaElement().withKeyType(KeyType.HASH).withAttributeName(userId))
                                .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L))
                )
                .withAttributeDefinitions(
                        new AttributeDefinition()
                                .withAttributeName(scriptId)
                                .withAttributeType(ScalarAttributeType.S),
                        new AttributeDefinition()
                                .withAttributeName(userId)
                                .withAttributeType(ScalarAttributeType.S)
                )
                .withProvisionedThroughput(
                        new ProvisionedThroughput()
                                .withReadCapacityUnits(1L)
                                .withWriteCapacityUnits(1L)
                );

        TableDescription createdTableDescription = dynamo.createTable(createTableRequest).getTableDescription();
        logger.info("Created Table: " + createdTableDescription);

        // Wait for it to become active
        logger.info("Waiting for " + scriptsTable + " to become ACTIVE...");
        Tables.waitForTableToBecomeActive(dynamo, scriptsTable);
    }

}
